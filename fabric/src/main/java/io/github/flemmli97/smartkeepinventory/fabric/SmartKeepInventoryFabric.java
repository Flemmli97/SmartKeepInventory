package io.github.flemmli97.smartkeepinventory.fabric;

import io.github.flemmli97.smartkeepinventory.data.ConditionsManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SmartKeepInventoryFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ConditionsManager.ID, provider -> new IdentifiableResourceReloadListener() {

            private final PreparableReloadListener listener = ConditionsManager.create(provider);

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return this.listener.reload(barrier, manager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }

            @Override
            public ResourceLocation getFabricId() {
                return ConditionsManager.ID;
            }
        });
    }
}
