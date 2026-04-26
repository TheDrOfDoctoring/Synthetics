package com.thedrofdoctoring.synthetics.mixin;

import com.thedrofdoctoring.synthetics.entities.linkable.ILinkableEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PlayerList.class)
public class PlayerListMixin {

    @Shadow
    @Final
    private List<ServerPlayer> players;

    // for linked entities like the drone, if the drone flies too far away things like sounds played won't be broadcast to the linked player
    // linked views like that use the same system that spectator mode does to set camera, but spectator mode constantly teleports the spectator ontop of the camera position, which we can't do.
    // inject head instead of invoke for incompatibility with some mods
    @Inject(method = "broadcast", at = @At(value = "HEAD"))
    public void broadcastToLinked(Player except, double x, double y, double z,
                                  double radius, ResourceKey<Level> dimension, Packet<?> packet, CallbackInfo ci) {
        for (ServerPlayer serverplayer : this.players) {
            if (serverplayer != except && serverplayer.level().dimension() == dimension) {
                double d0 = x - serverplayer.getX();
                double d1 = y - serverplayer.getY();
                double d2 = z - serverplayer.getZ();
                if (d0 * d0 + d1 * d1 + d2 * d2 > radius * radius && serverplayer.getCamera() instanceof ILinkableEntity ) {
                    Entity camera = serverplayer.getCamera();
                    double e0 = x - camera.getX();
                    double e1 = y - camera.getY();
                    double e2 = z - camera.getZ();
                    if (e0 * e0 + e1 * e1 + e2 * e2 < radius * radius) {
                        serverplayer.connection.send(packet);
                    }
                }
            }
        }
    }
}
