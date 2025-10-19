package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.AugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record ServerboundInstallableMenuPacket(Optional<BodyPart> part) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundInstallableMenuPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(BodyPart.STREAM_CODEC), ServerboundInstallableMenuPacket::part,
            ServerboundInstallableMenuPacket::new
    );


    public static final Type<ServerboundInstallableMenuPacket> TYPE = new Type<>(Synthetics.rl("request_menu_install"));

    @NotNull
    public Type<ServerboundInstallableMenuPacket> type() {
        return TYPE;
    }
    @SuppressWarnings("unused")
    public static void handle(ServerboundInstallableMenuPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player.containerMenu instanceof AugmentationChamberMenu menu) {
                SyntheticsPlayer synthetics = SyntheticsPlayer.get(player);
                ItemStack stack = menu.getInputContainer().getItem(0);
                if(stack.getItem() instanceof InstallableItem<?> item) {
                    IBodyInstallable<?> installable = item.getInstallableComponent(stack);
                    List<IBodyInstallable<?>> replaced = List.of();
                    boolean confirmed = false;
                    if(installable instanceof SyntheticAugment augment && packet.part.isPresent() && synthetics.getPartManager().isPartInstalled(packet.part.get())) {
                        AugmentInstance instance = new AugmentInstance(augment, packet.part.get());
                        if(synthetics.canAddAugment(instance)) {
                            synthetics.addAugment(instance, true);
                            confirmed = true;
                        }

                    } else {
                        if(synthetics.canAddInstallable(installable)) {
                            replaced = synthetics.addOrReplaceInstallable(installable);
                            confirmed = true;
                        }
                    }
                    if(confirmed) {
                        menu.getInputContainer().setItem(0, ItemStack.EMPTY);
                        int i = 0;
                        for(IBodyInstallable<?> replacedInstallable : replaced) {
                            ItemStack replace = replacedInstallable.createDefaultItemStack(context.player().registryAccess());
                            if(i >= menu.getResultsContainer().getContainerSize()) {
                                player.drop(replace, true, false);
                            } else {
                                menu.getResultsContainer().addItem(replace);
                            }

                            i++;
                        }
                        synthetics.markDirtyAll();
                        synthetics.getAbilityManager().rebuildAttributes();
                    }


                }
            }

        });
    }
}
