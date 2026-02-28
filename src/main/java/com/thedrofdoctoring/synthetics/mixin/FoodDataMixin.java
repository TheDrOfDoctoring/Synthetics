package com.thedrofdoctoring.synthetics.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private void addExhaustionScale(FoodData instance, float exhaustion, Operation<Void> original, Player player) {
        float exhaustionScale = (float) player.getAttributeValue(SyntheticsAttributes.EXHAUSTION_MULTIPLIER);
        original.call(instance, (exhaustion * exhaustionScale));
    }
}
