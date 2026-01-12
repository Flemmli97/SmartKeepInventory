package io.github.flemmli97.smartkeepinventory.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.flemmli97.smartkeepinventory.ServerPlayerDeathSource;
import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class, priority = 500)
public class LivingEntityMixin {

    /**
     * To innately support other mods (at least the ones checking the game rule) we capture the player here.
     * This allows instead to provide a player context for the keep inventory gamerule check
     */
    @WrapMethod(method = "dropAllDeathLoot")
    private void onDeathDrop(ServerLevel level, DamageSource damageSource, Operation<Void> original) {
        if ((Object) this instanceof ServerPlayer player) {
            ((ServerPlayerDeathSource) player).smartInv$calculateKeepInventoryState(damageSource);
            SmartKeepInventory.setGameRulePlayer(player);
        }
        original.call(level, damageSource);
        SmartKeepInventory.setGameRulePlayer(null);
    }

    @WrapOperation(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)V"))
    private void onDropExp(LivingEntity instance, ServerLevel level, Entity entity, Operation<Void> original) {
        SmartKeepInventory.setGameRuleContextPlayer(SmartKeepInventory.EXPERIENCE);
        original.call(instance, level, entity);
        SmartKeepInventory.setGameRuleContextPlayer(null);
    }
}
