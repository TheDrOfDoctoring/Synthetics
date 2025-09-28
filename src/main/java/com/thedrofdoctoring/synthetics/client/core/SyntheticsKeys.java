package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticAbilityActiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticActiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundActivateAbilityPacket;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundRequestUpdatePacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class SyntheticsKeys {

    private static final String CATEGORY = "keys.synthetics.category";

    public static final KeyMapping ACTIVATE_ABILITY = new KeyMapping("keys.synthetics.activate_ability", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping SWAP_ABILITY_RIGHT = new KeyMapping("keys.synthetics.select_right", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, CATEGORY);
    public static final KeyMapping SWAP_ABILITY_LEFT = new KeyMapping("keys.synthetics.select_left", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, CATEGORY);


    public static void registerKeys(@NotNull RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_ABILITY);
        event.register(SWAP_ABILITY_RIGHT);
        event.register(SWAP_ABILITY_LEFT);
    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsKeys::registerKeys);
        NeoForge.EVENT_BUS.addListener(SyntheticsKeys::handleKey);
    }


    @SubscribeEvent
    public static void handleKey(InputEvent.Key event) {
        int action = event.getAction();
        if(ACTIVATE_ABILITY.isDown() && action == InputConstants.PRESS) {
            activateSelectedAbility();
        }
        if(SWAP_ABILITY_RIGHT.isDown() && action == InputConstants.PRESS) {
            incrementSelectedAction(1);
        }
        if(SWAP_ABILITY_LEFT.isDown() && action == InputConstants.PRESS) {
            incrementSelectedAction(-1);
        }

    }


    private static void activateSelectedAbility() {
        Player player = Minecraft.getInstance().player;
        if(player != null && Minecraft.getInstance().getConnection() != null) {
            AbilityManager manager = SyntheticsPlayer.get(player).getAbilityManager();
            SyntheticAbilityActiveInstance[] abilities = manager.getActiveAbilities().toArray(new SyntheticAbilityActiveInstance[0]);
            int selected = SyntheticsClient.getInstance().getManager().selectedAbility;
            if(selected >= 0 && selected < abilities.length) {
                SyntheticActiveAbilityType ability = abilities[selected].getAbility();
                Minecraft.getInstance().getConnection().send(new ServerboundActivateAbilityPacket(ability));

            }
        }

    }
    private static void incrementSelectedAction(int i) {
        Player player = Minecraft.getInstance().player;
        if(player != null) {
            SyntheticsClient.getInstance().getManager().selectedAbility = Math.clamp(SyntheticsClient.getInstance().getManager().selectedAbility + i, 0, SyntheticsPlayer.get(player).getAbilityManager().getActiveAbilities().size() - 1);
        }
    }


}
