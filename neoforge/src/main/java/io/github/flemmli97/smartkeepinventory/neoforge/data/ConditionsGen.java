package io.github.flemmli97.smartkeepinventory.neoforge.data;

import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import io.github.flemmli97.smartkeepinventory.data.ConditionData;
import io.github.flemmli97.smartkeepinventory.data.provider.ConditionsProvider;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SmartKeepInventory.MODID)
public class ConditionsGen extends ConditionsProvider {

    public ConditionsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @SubscribeEvent
    public static void data(GatherDataEvent.Server event) {
        DataGenerator data = event.getGenerator();
        data.addProvider(true, new ConditionsGen(data.getPackOutput(), event.getLookupProvider()));
    }

    @Override
    protected void add(HolderLookup.Provider provider) {
        this.add(Identifier.fromNamespaceAndPath(SmartKeepInventory.MODID, "bosses"), new ConditionData(
                Optional.empty(),
                Optional.of(EntityPredicate.Builder.entity().of(provider.lookupOrThrow(Registries.ENTITY_TYPE), Tags.EntityTypes.BOSSES).build()),
                Optional.empty(), true, false));
    }
}
