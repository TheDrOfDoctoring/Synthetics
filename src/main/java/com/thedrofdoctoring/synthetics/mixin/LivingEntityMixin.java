package com.thedrofdoctoring.synthetics.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thedrofdoctoring.synthetics.abilities.active.types.WallClimbAbility;
import com.thedrofdoctoring.synthetics.abilities.passive.types.EffectAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.cache.EffectAmplifierCache;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    protected Optional<BlockPos> lastClimbablePos;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean modifyEffectAmplify(LivingEntity instance, MobEffectInstance effInstance, Entity entity, Operation<Boolean> original) {

        if(instance instanceof Player player) {
            EffectAmplifierCache cache = EffectAbilityType.getEffectCache(player);
            int amplifyModifier = cache.getAmplificationModifier(effInstance.getEffect());
            int modifiedAmplification = effInstance.getAmplifier() + amplifyModifier;
            if(modifiedAmplification >= 0) {
                return original.call(instance, new MobEffectInstance(effInstance.getEffect(), effInstance.getDuration(), modifiedAmplification, effInstance.isAmbient(), effInstance.isVisible(), effInstance.showIcon(), effInstance.hiddenEffect), entity);
            }
            return false;
        }

        return original.call(instance, effInstance, entity);
    }

    @ModifyReturnValue(method = "onClimbable", at = @At("RETURN"))
    private boolean onClimbable(boolean original) {
        if(original) {
            return true;
        }
        if(!level().isClientSide) return false;
        boolean shouldUpdateServer = false;
        boolean canClimb = false;
        LivingEntity self = (LivingEntity) (Object) this;
        if(!(self instanceof Player player)) return false;
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player);
        boolean hasCollision = level().collidesWithSuffocatingBlock(player, player.getBoundingBox().inflate(0.1f, 0, 0.1f));

        if(cache.hasWallClimb && hasCollision) {
            if(!cache.onWall && cache.onRoofTimer == 0) {
                cache.onWall = true;
                this.refreshDimensions();
                shouldUpdateServer = true;
            }
            if(cache.onWall && cache.onRoof && cache.onRoofTimer == 0) {
                cache.onRoof = false;
                shouldUpdateServer = true;
                self.setPos(position().x, position().y + 0.5f, position().z);
                cache.onRoofTimer = 10;
            }
            canClimb = true;
            this.lastClimbablePos = Optional.of(new BlockPos((int) self.position().x, (int) self.position().y, (int) self.position().z).relative(self.getMotionDirection()));
        } else if(cache.onWall) {
            cache.onWall = false;
            shouldUpdateServer = true;
            this.setPose(Pose.STANDING);
            this.refreshDimensions();
        }
        if(shouldUpdateServer) {
            WallClimbAbility.updateServerPlayer(player);
        }

        return canClimb;
    }

    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions onModifiedDimensions(EntityDimensions original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(self instanceof Player player) {

            if(SyntheticsPlayerCache.get(player).onRoof) {
                return WallClimbAbility.UPSIDE_DOWN_DIMENSIONS;
            }

            if(SyntheticsPlayerCache.get(player).onWall && player.getLastClimbablePos().isPresent()) {
                return WallClimbAbility.WALL_CLIMB_DIMENSIONS;
            }
        }
        return original;
    }

    @Inject(method = "updateInvisibilityStatus", at = @At("RETURN"))
    private void handleInvisibilityStatus(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(self instanceof Player player && SyntheticsPlayerCache.get(player).invisible) {
            this.setInvisible(true);
        }
    }


}
