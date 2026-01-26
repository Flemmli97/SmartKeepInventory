package io.github.flemmli97.smartkeepinventory.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ConditionsManager extends SimpleJsonResourceReloadListener {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(SmartKeepInventory.MODID, "condition");
    public static final String DIRECTORY = String.format("%s/%s", ID.getNamespace(), ID.getPath());
    private static final Gson GSON = new GsonBuilder().create();

    private static ConditionsManager INSTANCE;

    private Map<ResourceLocation, ConditionData> predicates = ImmutableMap.of();

    private final HolderLookup.Provider provider;

    private ConditionsManager(HolderLookup.Provider provider) {
        super(GSON, DIRECTORY);
        this.provider = provider;
    }

    public static ConditionsManager create(HolderLookup.Provider provider) {
        INSTANCE = new ConditionsManager(provider);
        return getInstance();
    }

    public static ConditionsManager getInstance() {
        return INSTANCE;
    }

    public boolean matches(ServerPlayer player, DamageSource source, @Nullable Entity killer, ResourceLocation context) {
        for (ConditionData predicate : this.predicates.values()) {
            if (predicate.matches(player, source, killer, context))
                return true;
        }
        return false;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager manager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, ConditionData> reader = ImmutableMap.builder();
        DynamicOps<JsonElement> ops = this.provider.createSerializationContext(JsonOps.INSTANCE);
        data.forEach((res, el) -> {
            try {
                ConditionData predicate = ConditionData.CODEC.parse(ops, el).getOrThrow();
                if (!predicate.disabled()) {
                    reader.put(res, predicate);
                }
            } catch (Exception ex) {
                SmartKeepInventory.LOGGER.error("Couldn't parse conditions json {} {}", res, ex, ex.fillInStackTrace());
            }
        });
        this.predicates = reader.build();
    }
}
