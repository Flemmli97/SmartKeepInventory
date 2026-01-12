package io.github.flemmli97.smartkeepinventory.neoforge;

import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import io.github.flemmli97.smartkeepinventory.data.ConditionsManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@Mod(value = SmartKeepInventory.MODID)
public class SmartKeepInventoryNeoForge {

    public SmartKeepInventoryNeoForge() {
        IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener(this::addReloadListener);
    }

    public void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(ConditionsManager.ID.identifier(), ConditionsManager.create(event.getServerResources().getRegistryLookup()));
    }
}
