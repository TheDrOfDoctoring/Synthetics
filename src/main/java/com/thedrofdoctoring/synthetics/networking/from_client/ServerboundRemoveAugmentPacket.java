package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.AppliedAugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundRemoveAugmentPacket(Holder<Augment> augment, Holder<BodyPart> part) implements CustomPacketPayload{

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRemoveAugmentPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.holder(SyntheticsData.AUGMENTS, Augment.STREAM_CODEC), ServerboundRemoveAugmentPacket::augment,
            ByteBufCodecs.holder(SyntheticsData.BODY_PARTS, BodyPart.STREAM_CODEC), ServerboundRemoveAugmentPacket::part,
            ServerboundRemoveAugmentPacket::new
    );
    public static ServerboundRemoveAugmentPacket create(Holder<Augment> augment, Holder<BodyPart> part) {
        return new ServerboundRemoveAugmentPacket(augment, part);
    }
    public static ServerboundRemoveAugmentPacket create(Augment augment, BodyPart part) {
        return new ServerboundRemoveAugmentPacket(Holder.direct(augment), Holder.direct(part));
    }
    public static ServerboundRemoveAugmentPacket create(AppliedAugmentInstance instance) {
        return create(instance.augment(), instance.appliedPart());
    }

    @Override
    public @NotNull CustomPacketPayload.Type<ServerboundRemoveAugmentPacket> type() {
        return TYPE;
    }

    public static final CustomPacketPayload.Type<ServerboundRemoveAugmentPacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("remove_augment"));

    public static void handle(ServerboundRemoveAugmentPacket augmentPacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SyntheticsPlayer player = SyntheticsPlayer.get(context.player());
            Augment augment = augmentPacket.augment().value();
            BodyPart part = augmentPacket.part().value();
            AppliedAugmentInstance instance = new AppliedAugmentInstance(augment, part);
            if(context.player().containerMenu instanceof AugmentationChamberMenu menu && player.isInstalled(instance)) {
                player.removeAugment(instance);
                ItemStack stack = augment.createDefaultItemStack(context.player().registryAccess());
                stack = menu.getResultsContainer().addItem(stack);
                if(stack != ItemStack.EMPTY) {
                    context.player().drop(stack, false);
                }
            }

        });
    }
}
