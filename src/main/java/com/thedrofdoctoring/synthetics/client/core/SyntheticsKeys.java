package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.abilities.active.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundActivateAbilityPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
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
    public static final KeyMapping HIDE_ABILITY_UI = new KeyMapping("keys.synthetics.hide_abilities", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_SUBTRACT, CATEGORY);
    public static final KeyMapping HIDE_ENERGY_UI = new KeyMapping("keys.synthetics.hide_energy", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_ADD, CATEGORY);


    public static void registerKeys(@NotNull RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_ABILITY);
        event.register(SWAP_ABILITY_RIGHT);
        event.register(SWAP_ABILITY_LEFT);
        event.register(HIDE_ABILITY_UI);
        event.register(HIDE_ENERGY_UI);

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
        if(HIDE_ABILITY_UI.isDown() && action == InputConstants.PRESS) {
            swapAbilityDisplay();
        }
        if(HIDE_ENERGY_UI.isDown() && action == InputConstants.PRESS) {
            swapEnergyDisplay();
        }
    }


    private static void activateSelectedAbility() {
        Player player = Minecraft.getInstance().player;
        if(player != null && Minecraft.getInstance().getConnection() != null) {
            AbilityManager manager = SyntheticsPlayer.get(player).getAbilityManager();
            AbilityActiveInstance<?>[] abilities = manager.getActiveAbilities().toArray(new AbilityActiveInstance[0]);
            int selected = SyntheticsClient.getInstance().getManager().selectedAbility;
            if(selected >= 0 && selected < abilities.length) {
                ActiveAbilityType ability = abilities[selected].getAbility();
                Minecraft.getInstance().getConnection().send(new ServerboundActivateAbilityPacket(ability));

            }
        }

    }
    private static void incrementSelectedAction(int i) {
        Player player = Minecraft.getInstance().player;
        SyntheticsClientManager manager = SyntheticsClient.getInstance().getManager();
        int selected = manager.selectedAbility;
        if(player != null) {
            int maxSize = SyntheticsPlayer.get(player).getAbilityManager().getActiveAbilities().size() - 1;

            if(selected + i > maxSize) {
                selected = 0;
            } else if(selected + i < 0) {
                selected = maxSize;
            } else {
                selected = selected + i;
            }


           manager.selectedAbility = selected;
        }
    }
    private static void swapAbilityDisplay() {
        var manager = SyntheticsClient.getInstance().getManager();
        manager.displayAbilities = !manager.displayAbilities;
    }
    private static void swapEnergyDisplay() {
        var manager = SyntheticsClient.getInstance().getManager();
        manager.displayEnergy = !manager.displayEnergy;
    }


}
