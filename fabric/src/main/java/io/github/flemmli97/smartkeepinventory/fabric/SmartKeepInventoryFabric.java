package io.github.flemmli97.smartkeepinventory.fabric;

import io.github.flemmli97.smartkeepinventory.data.ConditionsManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;

public class SmartKeepInventoryFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DataResourceLoader.get().registerReloader(ConditionsManager.ID.identifier(), ConditionsManager::create);
    }
}
