package io.github.flemmli97.smartkeepinventory.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.smartkeepinventory.ServerPlayerDeathSource;
import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ConditionData(Optional<EntityPredicate> player,
                            Optional<EntityPredicate> killer, Optional<DamageSourcePredicate> damage,
                            boolean keepExperience,
                            boolean disabled) {

    public static final Codec<ConditionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(EntityPredicate.CODEC.optionalFieldOf("player").forGetter(ConditionData::player),
                            EntityPredicate.CODEC.optionalFieldOf("killer").forGetter(ConditionData::killer),
                            DamageSourcePredicate.CODEC.optionalFieldOf("damage").forGetter(ConditionData::damage),
                            Codec.BOOL.optionalFieldOf("keepExperience").forGetter(d -> !d.keepExperience() ? Optional.of(false) : Optional.empty()),
                            Codec.BOOL.optionalFieldOf("disabled").forGetter(d -> d.disabled() ? Optional.of(true) : Optional.empty()))
                    .apply(instance, (player, killer, damage, keepExperience, disabled)
                            -> new ConditionData(player, killer, damage, keepExperience.orElse(true), disabled.orElse(false))));

    public boolean matches(ServerPlayer player, DamageSource source, @Nullable Entity killer, ResourceLocation context) {
        if (SmartKeepInventory.EXPERIENCE.equals(context) && !this.keepExperience())
            return false;
        if (!this.player.map(p -> p.matches(player, player)).orElse(true))
            return false;
        if (this.killer().isPresent()) {
            if (killer == null)
                return false;
            if (!this.killer().get().matches(player, killer))
                return false;
        }
        return this.damage.map(p -> p.matches(player, source)).orElse(true);
    }
}
