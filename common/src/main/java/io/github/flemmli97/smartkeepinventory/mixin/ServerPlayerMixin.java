package io.github.flemmli97.smartkeepinventory.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import io.github.flemmli97.smartkeepinventory.ServerPlayerDeathSource;
import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ServerPlayerDeathSource {

    @Unique
    private DeathData smartInv$deathData;

    private ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @WrapMethod(method = "restoreFrom")
    private void onRestore(ServerPlayer that, boolean keepEverything, Operation<Void> original) {
        SmartKeepInventory.setGameRulePlayer(that);
        original.call(that, keepEverything);
        // The vanilla handling is all under one block so if we want to handle it separately we do it here
        if (!keepEverything && !((ServerPlayerDeathSource) that).smartInv$shouldKeepInventory(SmartKeepInventory.EXPERIENCE)) {
            this.experienceLevel = 0;
            this.totalExperience = 0;
            this.experienceProgress = 0;
            this.setScore(0);
        }
        SmartKeepInventory.setGameRulePlayer(null);
    }

    /**
     * The death data needs to be persistent in case the player logs out after dying
     */
    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void saveData(CompoundTag compound, CallbackInfo info) {
        if (this.smartInv$deathData != null) {
            compound.put(SmartKeepInventory.MODID + ":deathData",
                    DeathData.CODEC.encodeStart(NbtOps.INSTANCE, this.smartInv$deathData).getOrThrow());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void loadData(CompoundTag compound, CallbackInfo info) {
        if (compound.contains(SmartKeepInventory.MODID + ":deathData")) {
            this.smartInv$deathData = DeathData.CODEC.parse(NbtOps.INSTANCE, compound.get(SmartKeepInventory.MODID + ":deathData"))
                    .ifError(e -> SmartKeepInventory.LOGGER.error(e.message()))
                    .result().orElse(null);
        }
    }

    @Override
    public boolean smartInv$shouldKeepInventory(ResourceLocation context) {
        return this.smartInv$deathData != null && this.smartInv$deathData.context().contains(context);
    }

    @Override
    public void smartInv$calculateKeepInventoryState(DamageSource source) {
        Entity killer = source.getEntity() != null ? source.getEntity() : this.getKillCredit();
        this.smartInv$deathData = SmartKeepInventory.calculateKeepInventoryState((ServerPlayer) (Object) this, source, killer);
    }
}
