package io.github.flemmli97.smartkeepinventory;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;

import java.util.List;
import java.util.Set;

/**
 * Because the last damaging entity and damageSource gets cleared after a while it needs to be saved separately
 */
public interface ServerPlayerDeathSource {

    boolean smartInv$shouldKeepInventory(Identifier context);

    void smartInv$calculateKeepInventoryState(DamageSource source);

    record DeathData(Set<Identifier> context) {

        public static final Codec<DeathData> CODEC = Identifier.CODEC.listOf().xmap(l -> new DeathData(Set.copyOf(l)),
                d -> List.copyOf(d.context()));
    }
}
