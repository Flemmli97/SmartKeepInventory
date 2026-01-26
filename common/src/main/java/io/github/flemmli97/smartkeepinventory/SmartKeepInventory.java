package io.github.flemmli97.smartkeepinventory;

import com.google.common.collect.ImmutableSet;
import io.github.flemmli97.smartkeepinventory.data.ConditionsManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SmartKeepInventory {

    public static final String MODID = "smart_keep_inventory";
    public static final Logger LOGGER = LogManager.getLogger("SmartKeepInventory");

    public static final ResourceLocation INVENTORY = ResourceLocation.fromNamespaceAndPath(MODID, "inventory");
    public static final ResourceLocation EXPERIENCE = ResourceLocation.fromNamespaceAndPath(MODID, "experience");

    private static ServerPlayer player;
    private static ResourceLocation context;

    public static void setGameRulePlayer(ServerPlayer player) {
        SmartKeepInventory.player = player;
    }

    public static void setGameRuleContextPlayer(ResourceLocation context) {
        SmartKeepInventory.context = context;
    }

    public static boolean shouldKeepInventory() {
        if (player == null)
            return false;
        return ((ServerPlayerDeathSource) player).smartInv$shouldKeepInventory(context == null ? INVENTORY : context);
    }

    public static ServerPlayerDeathSource.DeathData calculateKeepInventoryState(ServerPlayer player, DamageSource source, @Nullable Entity killer) {
        ImmutableSet.Builder<ResourceLocation> context = ImmutableSet.builder();
        if (ConditionsManager.getInstance().matches(player, source, killer, SmartKeepInventory.INVENTORY))
            context.add(SmartKeepInventory.INVENTORY);
        if (ConditionsManager.getInstance().matches(player, source, killer, SmartKeepInventory.EXPERIENCE))
            context.add(SmartKeepInventory.EXPERIENCE);
        return new ServerPlayerDeathSource.DeathData(context.build());
    }
}
