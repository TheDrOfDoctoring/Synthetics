package com.thedrofdoctoring.synthetics.mixin;

import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.particles.GenericParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class )
public abstract class BlockBehaviourMixin {

    @Shadow public abstract FluidState getFluidState();
    @Unique
    public final VoxelShape synthetics$voxelShape = Shapes.block();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "RETURN"), cancellable = true)
    private void getCollisionShape(BlockGetter getter, BlockPos pos, CollisionContext con, CallbackInfoReturnable<VoxelShape> cir) {
        if (!(con instanceof EntityCollisionContext)) {
            return;
        }
        Entity entity = ((EntityCollisionContext) con).getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        SyntheticsPlayerCache atts = SyntheticsPlayerCache.get(player);
        boolean canWaterWalk = atts.isWaterWalking && getFluidState().is(FluidTags.WATER);

        if (canWaterWalk) {
            if(!entity.isInWater() && !entity.isCrouching() && synthetics$isAbove(entity, synthetics$voxelShape, pos)) {
                cir.setReturnValue(synthetics$voxelShape);
                float offset = player.getRandom().nextFloat() / 4;
                player.level().addParticle(new GenericParticle.Options(ResourceLocation.fromNamespaceAndPath("minecraft", "generic_5"), 1, 0xd3fafb, 0F), player.getX() +offset, player.getY(), player.getZ() + offset, 2, 0, 0);
            }
        }
    }
    @Unique
    private boolean synthetics$isAbove(Entity entity, VoxelShape shape, BlockPos pos) {
        return entity.getY() > pos.getY() + shape.max(Direction.Axis.Y) - (entity.onGround() ? 8.05/16.0 : 0.0015);
    }
}
