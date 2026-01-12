package io.github.flemmli97.smartkeepinventory.mixin;

import io.github.flemmli97.smartkeepinventory.SmartKeepInventory;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.gamerules.GameRules.class)
public class GameRulesMixin {

    @SuppressWarnings("unchecked")
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private <T> void onGetGameRule(GameRule<T> gameRule, CallbackInfoReturnable<T> cir) {
        if (gameRule.equals(GameRules.KEEP_INVENTORY) && SmartKeepInventory.shouldKeepInventory()) {
            cir.setReturnValue((T) Boolean.TRUE);
        }
    }
}
