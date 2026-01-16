package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.AbilityWheelScreen;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.editor.WheelEditorScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class SyntheticsKeys {

    private static final String CATEGORY = "keys.synthetics.category";

    public static final KeyMapping HIDE_ENERGY_UI = new KeyMapping("keys.synthetics.hide_energy", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_ADD, CATEGORY);
    public static final KeyMapping ABILITY_WHEEL = new KeyMapping("keys.synthetics.ability_wheel", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping ABILITY_WHEEl_EDITOR = new KeyMapping("keys.synthetics.ability_wheel_editor", KeyConflictContext.IN_GAME, KeyModifier.ALT,InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping WHEEL_RIGHT_WHEEL = new KeyMapping("keys.synthetics.wheel.right_wheel", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_E, CATEGORY);
    public static final KeyMapping WHEEL_LEFT_WHEEL = new KeyMapping("keys.synthetics.wheel.left_wheel", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Q, CATEGORY);
    public static final KeyMapping WHEEL_ADD_SLOT = new KeyMapping("keys.synthetics.wheel.add_slot", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    public static final KeyMapping WHEEL_ADD_WHEEL = new KeyMapping("keys.synthetics.wheel.add_wheel", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY);


    public static void registerKeys(@NotNull RegisterKeyMappingsEvent event) {
        event.register(HIDE_ENERGY_UI);
        event.register(ABILITY_WHEEL);
        event.register(WHEEL_RIGHT_WHEEL);
        event.register(WHEEL_LEFT_WHEEL);
        event.register(WHEEL_ADD_SLOT);
        event.register(WHEEL_ADD_WHEEL);
        event.register(ABILITY_WHEEl_EDITOR);

    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsKeys::registerKeys);
        NeoForge.EVENT_BUS.addListener(SyntheticsKeys::handleKey);
    }


    @SubscribeEvent
    public static void handleKey(InputEvent.Key event) {
        int action = event.getAction();
        if(HIDE_ENERGY_UI.isDown() && action == InputConstants.PRESS) {
            swapEnergyDisplay();
        }
        if(ABILITY_WHEEL.isDown() && action == InputConstants.PRESS) {
            openAbilityWheel();
        }
        if(ABILITY_WHEEl_EDITOR.isDown() && action == InputConstants.PRESS) {
            openAbilityWheelEditor();
        }
    }

    private static void openAbilityWheel() {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.isAlive() && !player.isSpectator()) {
            AbilityWheelScreen.show();
        }
    }
    private static void openAbilityWheelEditor() {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.isAlive() && !player.isSpectator()) {
            WheelEditorScreen.show();
        }
    }

    private static void swapEnergyDisplay() {
        var manager = SyntheticsClient.getInstance().getManager();
        manager.displayEnergy = !manager.displayEnergy;
    }


}
