package com.thedrofdoctoring.synthetics.blocks.entities.linkables;

import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.core.SyntheticsEntities;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.entities.linkable.DummyCameraEntity;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundViewLinkPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CameraLinkableBlockEntity extends LinkableBlockEntity {
    public CameraLinkableBlockEntity(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.CAMERA_LINKABLE.get(), pos, blockState);
    }

    private DummyCameraEntity camera;

    @Override
    public void onLinkedInteract(Player player, @Nullable ItemStack stack, @NotNull InteractionHand hand) {
        if(player instanceof ServerPlayer sp) {
            startPlayerCameraView(sp);
        }
    }

    @Override
    public void onRemoveLinkable(ServerLevel level) {
        super.onRemoveLinkable(level);
        DummyCameraEntity camera = findDummyCameraEntity();
        if(camera != null) {
            Player player = camera.getLinkedTo();
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.setCamera(serverPlayer);
            }
            camera.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    public void removeLinkedForPlayer(ServerPlayer player) {
        super.removeLinkedForPlayer(player);
        endPlayerCameraView(player);
    }

    private void endPlayerCameraView(ServerPlayer player) {
        DummyCameraEntity camera = findDummyCameraEntity();
        if(player.getCamera() == camera) {
            SyntheticsPlayerCache.get(player).isNotViewingSelf = false;
            player.setCamera(player);
            camera.setLinkedTo(null);
            sendViewPacket(player, false);
        }
    }

    private void startPlayerCameraView(ServerPlayer player) {
        DummyCameraEntity camera = findDummyCameraEntity();
        if(camera != null) {
            SyntheticsPlayerCache.get(player).isNotViewingSelf = true;
            player.setCamera(camera);
            camera.setLinkedTo(player);
            sendViewPacket(player, true);
        } else if(this.level instanceof ServerLevel serverLevel){
            Vec3 pos = this.worldPosition.getCenter();
            DummyCameraEntity dummyCamera = SyntheticsEntities.DUMMY_CAMERA_ENTITY.get().spawn(serverLevel, this.worldPosition, MobSpawnType.MOB_SUMMONED);
            if(dummyCamera != null) {
                dummyCamera.setPos(pos);
                dummyCamera.setLinkedTo(player);
                serverLevel.addFreshEntity(dummyCamera);
                startPlayerCameraView(player);
            }
        }
    }

    public void onPlace() {
        if(findDummyCameraEntity() == null && level instanceof ServerLevel serverLevel) {
            Vec3 pos = this.worldPosition.getCenter();
            DummyCameraEntity dummyCamera = SyntheticsEntities.DUMMY_CAMERA_ENTITY.get().spawn(serverLevel, this.worldPosition, MobSpawnType.MOB_SUMMONED);
            if(dummyCamera != null) {
                dummyCamera.setPos(pos);

                serverLevel.addFreshEntity(dummyCamera);
            }
        }
    }

    private @Nullable DummyCameraEntity findDummyCameraEntity() {
        if(this.camera != null) {
            return camera;
        }
        if(this.level != null) {
            List<Entity> nearby = this.level.getEntities((Entity) null, new AABB(this.worldPosition), (entity -> entity instanceof DummyCameraEntity));
            if(!nearby.isEmpty()) {
                this.camera = (DummyCameraEntity) nearby.getFirst();
                return camera;
            }
        }
        return null;
    }

    public static void sendViewPacket(ServerPlayer player, boolean isNotViewingSelf) {
        player.connection.send(new ClientboundViewLinkPacket(isNotViewingSelf));
    }

    @Override
    public boolean canPlayerInteract(Player player) {
        if(player instanceof ServerPlayer serverPlayer && serverPlayer.getCamera().equals(findDummyCameraEntity())) {
            return false;
        }
        return SyntheticsPlayer.get(player).getAbilityManager().hasAbility(Abilities.CYBERNETIC_CAMERA_LINK);
    }
}
