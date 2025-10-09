package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServerboundInstallableMenuPacket implements CustomPacketPayload {
    private static ServerboundInstallableMenuPacket INSTANCE;

    private ServerboundInstallableMenuPacket() {
    }
    public static ServerboundInstallableMenuPacket getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ServerboundInstallableMenuPacket();
        }
        return INSTANCE;
    }
    public static final CustomPacketPayload.Type<ServerboundInstallableMenuPacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("request_menu_install"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundInstallableMenuPacket> CODEC = StreamCodec.unit(getInstance());

    @NotNull
    public Type<ServerboundInstallableMenuPacket> type() {
        return TYPE;
    }
    @SuppressWarnings("unused")
    public static void handle(ServerboundInstallableMenuPacket __, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player.containerMenu instanceof AugmentationChamberMenu menu) {
                SyntheticsPlayer synthetics = SyntheticsPlayer.get(player);
                ItemStack stack = menu.getInputContainer().getItem(0);
                if(stack.getItem() instanceof InstallableItem<?> item) {
                    IBodyInstallable<?> installable = item.getInstallableComponent(stack);
                    if(synthetics.canAddInstallable(installable)) {
                        List<IBodyInstallable<?>> replaced = synthetics.addOrReplaceInstallable(installable);
                        menu.getInputContainer().setItem(0, ItemStack.EMPTY);
                        int i = 0;
                        for(IBodyInstallable<?> replacedInstallable : replaced) {
                            ItemStack replace = replacedInstallable.createDefaultItemStack();
                            if(i >= menu.getResultsContainer().getContainerSize()) {
                                player.drop(replace, true, false);
                            } else {
                                menu.getResultsContainer().setItem(i, replace);
                            }

                            i++;
                        }


                    }
                }
            }

        });
    }
}
