package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientEventHandler;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundWallClimbPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class WallClimbAbility extends StandardLastingAbility {

    public WallClimbAbility(ResourceLocation id) {
        super(id);
    }

    public static final EntityDimensions WALL_CLIMB_DIMENSIONS = new EntityDimensions(Player.STANDING_DIMENSIONS.width(), 0.6f, 0.6f, Player.STANDING_DIMENSIONS.attachments(), Player.STANDING_DIMENSIONS.fixed()).withEyeHeight(0.4f);
    public static final EntityDimensions UPSIDE_DOWN_DIMENSIONS = new EntityDimensions(Player.STANDING_DIMENSIONS.width(), Player.STANDING_DIMENSIONS.height(), 0.1f, Player.STANDING_DIMENSIONS.attachments(), Player.STANDING_DIMENSIONS.fixed());

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        activate(syntheticsPlayer);
        return true;
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        Player player = syntheticsPlayer.getEntity();
        player.resetFallDistance();
        Optional<BlockPos> posOpt = player.getLastClimbablePos();
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player);
        if(!player.level().isClientSide) return false;
        if(posOpt.isPresent() && !cache.onRoof && cache.onRoofTimer == 0) {
            AABB bounding = player.getBoundingBox();

            boolean collidesRoof = player.level().collidesWithSuffocatingBlock(player, bounding.setMaxY(bounding.maxY + 1f));
            if(cache.onWall && collidesRoof) {
                if(!cache.onRoof) {
                    cache.onWall = false;
                    cache.onRoofTimer = 30;
                    player.setPos(player.position().subtract(0, 1, 0));
                    player.refreshDimensions();
                }
                cache.onRoof = true;
                updateServerPlayer(player);
                return false;
            }
        }

        if(cache.onRoof && !player.isSuppressingSlidingDownLadder()) {
            Vec3 deltaMovement = player.getDeltaMovement();
            player.setDeltaMovement(deltaMovement.x, 0f, deltaMovement.z);
            AABB bounding = player.getBoundingBox();
            if(player.tickCount % 5 == 0) {
                AABB boundingTall = bounding.setMaxY(bounding.maxY + 7.5f).setMinY(bounding.minY - 0.5f);
                if(!player.level().collidesWithSuffocatingBlock(player, boundingTall)) {
                    cancelRoofClimb(player);
                    return false;
                }

            }
            AABB boundingShort = bounding.setMaxY(bounding.maxY + 1f);
            if(!player.level().collidesWithSuffocatingBlock(player, boundingShort)) {
                AABB boundingTall = bounding.setMaxY(bounding.maxY + 7.5f).setMinY(bounding.minY - 0.5f);
                if(!player.level().collidesWithSuffocatingBlock(player, boundingTall)) {
                    cancelRoofClimb(player);
                    return false;
                }
                player.setDeltaMovement(deltaMovement.x, 1f, deltaMovement.z);
            }
        } else {
            cancelRoofClimb(player);
        }
        cache.onRoofTimer = Math.max(cache.onRoofTimer - 1, 0);
        return false;
    }

    public static void cancelRoofClimb(Player player) {
        SyntheticsPlayerCache.get(player).onRoof = false;
        SyntheticsPlayerCache.get(player).onRoofTimer = 0;
        updateServerPlayer(player);
        player.refreshDimensions();
    }

    public static void updateServerPlayer(Player player) {
        if(player.level().isClientSide) {
            SyntheticsClientEventHandler.updateServerPlayerWallClimb(player);
        }
    }

    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player.getEntity());
        cache.hasWallClimb = true;
        if(player.getEntity().level() instanceof ServerLevel level) {
            ClientboundWallClimbPacket packet = new ClientboundWallClimbPacket(player.getEntity().getId(), true, false, false);
            ServerChunkCache serverchunkcache = level.getChunkSource();
            serverchunkcache.broadcastAndSend(player.getEntity(), packet);
        }
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        activate(syntheticsPlayer);
    }


    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(syntheticsPlayer.getEntity());
        cache.hasWallClimb = false;
        cache.onWall = false;
        cancelRoofClimb(syntheticsPlayer.getEntity());
        if(syntheticsPlayer.getEntity().level() instanceof ServerLevel level) {
            ClientboundWallClimbPacket packet = new ClientboundWallClimbPacket(syntheticsPlayer.getEntity().getId(), false, false, false);
            ServerChunkCache serverchunkcache = level.getChunkSource();
            serverchunkcache.broadcast(syntheticsPlayer.getEntity(), packet);
        }
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        activate(syntheticsPlayer);
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer player) {
        return true;
    }


}
