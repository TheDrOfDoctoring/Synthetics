package com.thedrofdoctoring.synthetics.mixin;

import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.generators.FoodGeneratorAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.ISyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ISyntheticsPlayerCache {

    @Unique
    private final SyntheticsPlayerCache synthetics$syntheticsPlayerCache = new SyntheticsPlayerCache();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean onClimbable() {
        if(synthetics$syntheticsPlayerCache.hasWallClimb && (this.horizontalCollision || this.minorHorizontalCollision)) {

            return true;
        }
        return super.onClimbable();
    }

    @Override
    public SyntheticsPlayerCache synthetics$getCache() {
        return this.synthetics$syntheticsPlayerCache;
    }

    @Inject(method = "eat", at = @At("HEAD"))
    private void eat(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
        SyntheticsPlayer synthetics = SyntheticsPlayer.get((Player) (Object)this);
        Collection<IntObjectPair<AbilityPassiveInstance<?>>> instances = synthetics.getAbilityManager().getPassiveAbilitiesPairs();
        for(IntObjectPair<AbilityPassiveInstance<?>> pair : instances) {
            AbilityPassiveInstance<?> instance = pair.right();
            if(instance.getAbility().equals(SyntheticAbilities.FOOD_GENERATOR.get())) {
                FoodGeneratorAbility ability = (FoodGeneratorAbility) instance.getAbility();
                ability.onEaten(instance, pair.leftInt(), synthetics, foodProperties);
            }
        }
    }
}
