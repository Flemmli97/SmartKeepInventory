package io.github.flemmli97.smartkeepinventory.data;

import com.google.common.collect.ImmutableMap;
import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ConditionsManager extends SimpleJsonResourceReloadListener<ConditionData> {

    public static final ResourceKey<? extends Registry<ConditionData>> ID =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(SmartKeepInventory.MODID, "condition"));

    private static ConditionsManager INSTANCE;

    private Map<Identifier, ConditionData> predicates = ImmutableMap.of();

    private ConditionsManager(HolderLookup.Provider provider) {
        super(provider, ConditionData.CODEC, ID);
    }

    public static ConditionsManager create(HolderLookup.Provider provider) {
        INSTANCE = new ConditionsManager(provider);
        return getInstance();
    }

    public static ConditionsManager getInstance() {
        return INSTANCE;
    }

    public boolean matches(ServerPlayer player, DamageSource source, @Nullable Entity killer, Identifier context) {
        for (ConditionData predicate : this.predicates.values()) {
            if (predicate.matches(player, source, killer, context))
                return true;
        }
        return false;
    }

    @Override
    protected void apply(Map<Identifier, ConditionData> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.predicates = ImmutableMap.copyOf(map);
    }
}
