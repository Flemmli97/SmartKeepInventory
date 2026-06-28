package io.github.flemmli97.smartkeepinventory.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ConditionData(Optional<PredicateHolder> player,
                            Optional<PredicateHolder> killer, Optional<DamageSourcePredicate> damage,
                            boolean keepExperience,
                            boolean disabled) {

    public static final Codec<ConditionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(PredicateHolder.CODEC.optionalFieldOf("player").forGetter(ConditionData::player),
                            PredicateHolder.CODEC.optionalFieldOf("killer").forGetter(ConditionData::killer),
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

    public record PredicateHolder(EntityPredicate predicate, boolean invert) {

        public static final Codec<PredicateHolder> CODEC = Codec.either(RecordCodecBuilder.<PredicateHolder>create(instance ->
                        instance.group(EntityPredicate.CODEC.fieldOf("predicate").forGetter(PredicateHolder::predicate),
                                        Codec.BOOL.fieldOf("invert").forGetter(PredicateHolder::invert))
                                .apply(instance, PredicateHolder::new)),
                EntityPredicate.CODEC).xmap(e -> e.map(h -> h, p -> new PredicateHolder(p, false)),
                h -> h.invert() ? Either.left(h) : Either.right(h.predicate()));

        public PredicateHolder(EntityPredicate predicate) {
            this(predicate, false);
        }

        public boolean matches(ServerPlayer player, Entity entity) {
            if (this.invert())
                return !this.predicate.matches(player, entity);
            return this.predicate.matches(player, entity);
        }
    }
}
