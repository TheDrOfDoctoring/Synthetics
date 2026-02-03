package com.thedrofdoctoring.synthetics.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.thedrofdoctoring.synthetics.abilities.active.types.WallClimbAbility;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin extends AttachmentHolder {

    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    private AABB getModifiedBounds(AABB original) {
        Entity self = (Entity) (Object) this;
        if(self instanceof Player player) {
            if(SyntheticsPlayerCache.get(player).onRoof) {
                return WallClimbAbility.UPSIDE_DOWN_DIMENSIONS.makeBoundingBox(player.position());
            }
            if(SyntheticsPlayerCache.get(player).onWall && (!player.level().isClientSide || player.getLastClimbablePos().isPresent())) {
                return WallClimbAbility.WALL_CLIMB_DIMENSIONS.makeBoundingBox(player.position());
            }
        }
        return original;
    }
}
