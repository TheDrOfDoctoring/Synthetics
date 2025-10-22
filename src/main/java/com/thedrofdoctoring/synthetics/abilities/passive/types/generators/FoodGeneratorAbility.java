package com.thedrofdoctoring.synthetics.abilities.passive.types.generators;

import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;

public class FoodGeneratorAbility extends PassiveAbilityType {
    public FoodGeneratorAbility(ResourceLocation id) {
        super(id);
    }

    public void onEaten(AbilityPassiveInstance<?> instance, int count, SyntheticsPlayer player, FoodProperties properties) {
        FoodData food = player.getEntity().getFoodData();
        int excessFood = Math.max(0, properties.nutrition() + food.getFoodLevel() - 20);
        if(excessFood > 0) {
            double factor = instance.factor();
            int generated = (int) ((properties.saturation() / 2) * excessFood * factor);
            player.getPowerManager().addPower(generated * count);
        }
    }
}
