package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundRemoveAugmentPacket(Holder<SyntheticAugment> augment) implements CustomPacketPayload{

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRemoveAugmentPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.holder(SyntheticsData.AUGMENTS, SyntheticAugment.STREAM_CODEC), ServerboundRemoveAugmentPacket::augment,
            ServerboundRemoveAugmentPacket::new
    );

    public static ServerboundRemoveAugmentPacket create(SyntheticAugment augment) {
        return new ServerboundRemoveAugmentPacket(Holder.direct(augment));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<ServerboundRemoveAugmentPacket> type() {
        return TYPE;
    }

    public static final CustomPacketPayload.Type<ServerboundRemoveAugmentPacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("remove_augment"));

    public static void handle(ServerboundRemoveAugmentPacket augmentPacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SyntheticsPlayer player = SyntheticsPlayer.get(context.player());
            SyntheticAugment augment = augmentPacket.augment().value();
            if(context.player().containerMenu instanceof AugmentationChamberMenu menu && player.isAugmentInstalled(augment)) {
                player.removeAugment(augment);
                ItemStack stack = new ItemStack(SyntheticsItems.AUGMENT_INSTALLABLE.get());
                stack.set(SyntheticsDataComponents.AUGMENT, augmentPacket.augment);
                stack = menu.getResultsContainer().addItem(stack);
                if(stack != ItemStack.EMPTY) {
                    context.player().drop(stack, false);
                }
            }

        });
    }
}
