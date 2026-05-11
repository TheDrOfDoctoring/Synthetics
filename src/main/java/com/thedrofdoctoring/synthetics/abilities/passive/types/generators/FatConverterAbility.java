package com.thedrofdoctoring.synthetics.abilities.passive.types.generators;

import com.thedrofdoctoring.synthetics.abilities.passive.IAbilityEventListener;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.GenericPassiveAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.types.StandardPassiveAbility;
import com.thedrofdoctoring.synthetics.capabilities.PowerManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodData;

public class FatConverterAbility extends StandardPassiveAbility implements IAbilityEventListener<GenericPassiveAbilityInstance<?>> {
    private static final float MINIMUM_ENERGY_FOR_CONVERSION = 0.3f;
    private static final float STOP_AT_STORED_ENERGY = 0.95f;
    private static final int REQUIRED_STORED_FOOD = 17;
    private static final int STOP_AT_STORED_FOOD = 8;
    private static final int UPDATE_ON = 4;
    private static final int BASE_ENERGY_FOOD_GENERATED = 3250;
    private static final int BASE_ENERGY_SATURATION_GENERATED = 1750;

    public FatConverterAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onTick(GenericPassiveAbilityInstance<?> instance, int instanceCount, SyntheticsPlayer player) {
        if(player.getEntity().tickCount % UPDATE_ON == 0) {
            PowerManager powerManager = player.getPowerManager();
            if(powerManager.getMaxPower() == 0) return;
            float storedPowerProportion = (float) powerManager.getStoredPower() / powerManager.getMaxPower();
            SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player.getEntity());
            FoodData foodData = player.getEntity().getFoodData();
            if(cache.startedFatConversion) {

                if(storedPowerProportion >= STOP_AT_STORED_ENERGY || foodData.getFoodLevel() <= STOP_AT_STORED_FOOD) {
                    cache.startedFatConversion = false;
                    return;
                }
                consumeFoodForEnergy(powerManager, foodData, instance.factor() * instanceCount);

            } else if(storedPowerProportion <= MINIMUM_ENERGY_FOR_CONVERSION){
                if(foodData.getFoodLevel() >= REQUIRED_STORED_FOOD) {
                    cache.startedFatConversion = true;
                    consumeFoodForEnergy(powerManager, foodData, instance.factor() * instanceCount);
                }
            }
        }
    }

    private void consumeFoodForEnergy(PowerManager manager, FoodData foodData, double factor) {
        if(foodData.getSaturationLevel() > 1) {
            foodData.setSaturation(Math.max(0, foodData.getSaturationLevel() - 1));
            manager.addPower((int) (factor * BASE_ENERGY_SATURATION_GENERATED));
            manager.markDirty();
        } else if(foodData.getFoodLevel() > STOP_AT_STORED_FOOD) {
            foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - 1));
            manager.addPower((int) (factor * BASE_ENERGY_FOOD_GENERATED));
            manager.markDirty();
        }
    }
}
