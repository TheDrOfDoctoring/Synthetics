package com.thedrofdoctoring.synthetics.networking.from_client;

import com.mojang.datafixers.util.Function8;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import com.thedrofdoctoring.synthetics.entities.linkable.DummyCameraEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record ServerboundLinkedInputPacket(float sideways, float forward, float xRot, float yRot, float yRotHead, boolean jumping, boolean shift, boolean sprint) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundLinkedInputPacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("linked_input_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundLinkedInputPacket> CODEC = composite(
            ByteBufCodecs.FLOAT, ServerboundLinkedInputPacket::sideways,
            ByteBufCodecs.FLOAT, ServerboundLinkedInputPacket::forward,
            ByteBufCodecs.FLOAT, ServerboundLinkedInputPacket::xRot,
            ByteBufCodecs.FLOAT, ServerboundLinkedInputPacket::yRot,
            ByteBufCodecs.FLOAT, ServerboundLinkedInputPacket::yRotHead,
            ByteBufCodecs.BOOL, ServerboundLinkedInputPacket::jumping,
            ByteBufCodecs.BOOL, ServerboundLinkedInputPacket::shift,
            ByteBufCodecs.BOOL, ServerboundLinkedInputPacket::sprint,
            ServerboundLinkedInputPacket::new);


    @NotNull
    public CustomPacketPayload.@NotNull Type<ServerboundLinkedInputPacket> type() {
        return TYPE;
    }

    public static void handle(ServerboundLinkedInputPacket linkedPacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer serverPlayer && SyntheticsPlayerCache.get(serverPlayer).isNotViewingSelf) {
                Entity camera = serverPlayer.getCamera();
                if(camera instanceof DummyCameraEntity) {
                    camera.lerpHeadTo(linkedPacket.yRotHead, (int) linkedPacket.xRot);
                    camera.absRotateTo(linkedPacket.yRot(), linkedPacket.xRot());
                }
                if(camera instanceof DroneEntity drone) {
                    handleDroneInput(drone, linkedPacket);
                    serverPlayer.serverLevel().getChunkSource().chunkMap.updateChunkTracking(serverPlayer);
                }
            }

        });

    }

    private static void handleDroneInput(DroneEntity drone, ServerboundLinkedInputPacket inputPacket) {
        drone.absRotateTo(inputPacket.yRot(), inputPacket.xRot());
        drone.setYHeadRot(inputPacket.yRotHead());
        float verticalMotion = 0;
        float sideways = inputPacket.sideways;
        float forward = inputPacket.forward;
        if(drone.isFlying()) {
            if(inputPacket.sprint()) {
                verticalMotion = -1.0f;
            }
            if(inputPacket.jumping()) {
                verticalMotion = 1.0f;
            }
        } else {
            if(inputPacket.jumping()) {
                drone.jumpFromGround();
                drone.setFlying(true);
            }
            sideways *= 0.5f;
            forward *= 0.5f;
        }
        drone.travel(new Vec3(sideways, verticalMotion, forward));
    }



    private static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final StreamCodec<? super B, T8> pCodec8,
            final Function<C, T8> pGetter8,
            final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> pFactory
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(@NotNull B type) {
                T1 t1 = pCodec1.decode(type);
                T2 t2 = pCodec2.decode(type);
                T3 t3 = pCodec3.decode(type);
                T4 t4 = pCodec4.decode(type);
                T5 t5 = pCodec5.decode(type);
                T6 t6 = pCodec6.decode(type);
                T7 t7 = pCodec7.decode(type);
                T8 t8 = pCodec8.decode(type);
                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(@NotNull B codec, @NotNull C getter) {
                pCodec1.encode(codec, pGetter1.apply(getter));
                pCodec2.encode(codec, pGetter2.apply(getter));
                pCodec3.encode(codec, pGetter3.apply(getter));
                pCodec4.encode(codec, pGetter4.apply(getter));
                pCodec5.encode(codec, pGetter5.apply(getter));
                pCodec6.encode(codec, pGetter6.apply(getter));
                pCodec7.encode(codec, pGetter7.apply(getter));
                pCodec8.encode(codec, pGetter8.apply(getter));
            }
        };
    }
}
