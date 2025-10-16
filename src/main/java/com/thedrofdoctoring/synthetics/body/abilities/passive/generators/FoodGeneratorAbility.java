package com.thedrofdoctoring.synthetics.body.abilities.passive.generators;

import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticAbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticPassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;

public class FoodGeneratorAbility extends SyntheticPassiveAbilityType {
    public FoodGeneratorAbility(ResourceLocation id) {
        super(id);
    }

    public void onEaten(SyntheticAbilityPassiveInstance instance, SyntheticsPlayer player, FoodProperties properties) {
        FoodData food = player.getEntity().getFoodData();
        int excessFood = Math.max(0, properties.nutrition() + food.getFoodLevel() - 20);
        if(excessFood > 0) {
            double factor = instance.getAbilityFactor();
            int generated = (int) ((properties.saturation() / 2) * excessFood * factor);
            player.getPowerManager().addPower(generated);
        }
    }
}
