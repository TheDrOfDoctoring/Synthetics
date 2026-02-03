package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.AbilityWheelScreen;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.editor.AbilityWheelEditorScreen;
import it.unimi.dsi.fastutil.ints.Int2LongArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
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


// The key-ability hotkey toggle is based on Vampirism's similar system, licensed under GNU LGPL. https://github.com/TeamLapen/Vampirism/blob/version/1.21/latest/src/main/java/de/teamlapen/vampirism/client/core/ModKeys.java


public class SyntheticsKeys {

    private static final String CATEGORY = "keys.synthetics.category";

    public static final KeyMapping HIDE_ENERGY_UI = new KeyMapping("keys.synthetics.hide_energy", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_ADD, CATEGORY);
    public static final KeyMapping ABILITY_WHEEL = new KeyMapping("keys.synthetics.ability_wheel", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping ABILITY_WHEEl_EDITOR = new KeyMapping("keys.synthetics.ability_wheel_editor", KeyConflictContext.IN_GAME, KeyModifier.ALT,InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping WHEEL_RIGHT_WHEEL = new KeyMapping("keys.synthetics.wheel.right_wheel", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_E, CATEGORY);
    public static final KeyMapping WHEEL_LEFT_WHEEL = new KeyMapping("keys.synthetics.wheel.left_wheel", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Q, CATEGORY);
    public static final KeyMapping WHEEL_ADD_SLOT = new KeyMapping("keys.synthetics.wheel.add_slot", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    public static final KeyMapping WHEEL_ADD_WHEEL = new KeyMapping("keys.synthetics.wheel.add_wheel", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY);

    public static final KeyMapping ABILITY1 = new KeyMapping("keys.synthetics.ability_1", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_1, CATEGORY);
    public static final KeyMapping ABILITY2 = new KeyMapping("keys.synthetics.ability_2", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_2, CATEGORY);
    public static final KeyMapping ABILITY3 = new KeyMapping("keys.synthetics.ability_3", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_3, CATEGORY);
    public static final KeyMapping ABILITY4 = new KeyMapping("keys.synthetics.ability_4", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY5 = new KeyMapping("keys.synthetics.ability_5", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY6 = new KeyMapping("keys.synthetics.ability_6", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY7 = new KeyMapping("keys.synthetics.ability_7", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY8 = new KeyMapping("keys.synthetics.ability_8", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY9 = new KeyMapping("keys.synthetics.ability_9", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);

    public static final Int2ObjectArrayMap<KeyMapping> ABILITY_HOTKEYS = new Int2ObjectArrayMap<>(
            new int[]{1,2,3,4,5,6,7,8,9},
            new KeyMapping[]{ABILITY1, ABILITY2, ABILITY3, ABILITY4, ABILITY5, ABILITY6, ABILITY7, ABILITY8, ABILITY9});

    private static final Int2LongArrayMap abilityTriggerTime = new Int2LongArrayMap();
    private static final long ABILITY_PRESS_COOLDOWN = 400;

    public static void registerKeys(@NotNull RegisterKeyMappingsEvent event) {
        event.register(HIDE_ENERGY_UI);
        event.register(ABILITY_WHEEL);
        event.register(WHEEL_RIGHT_WHEEL);
        event.register(WHEEL_LEFT_WHEEL);
        event.register(WHEEL_ADD_SLOT);
        event.register(WHEEL_ADD_WHEEL);
        event.register(ABILITY_WHEEl_EDITOR);

        ABILITY_HOTKEYS.forEach((i, k) -> event.register(k));

    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsKeys::registerKeys);
        NeoForge.EVENT_BUS.addListener(SyntheticsKeys::handleKey);
    }


    @SubscribeEvent
    public static void handleKey(InputEvent.Key event) {
        int action = event.getAction();
        if(action == InputConstants.PRESS) {

            if(HIDE_ENERGY_UI.isDown()) {
                swapEnergyDisplay();
            }
            if(ABILITY_WHEEL.isDown()) {
                openAbilityWheel();
            }
            if(ABILITY_WHEEl_EDITOR.isDown()) {
                openAbilityWheelEditor();
            }
            ABILITY_HOTKEYS.int2ObjectEntrySet().fastForEach(entry -> {
                if(entry.getValue().isDown()) {
                    toggleAbility(entry.getIntKey());
                }
            });
        }
    }

    private static void toggleAbility(int i) {
        long t = System.currentTimeMillis();
        if (t - abilityTriggerTime.getOrDefault(i,0) > ABILITY_PRESS_COOLDOWN) {
            abilityTriggerTime.put(i, t);
            SyntheticsClient.getInstance().getAdvancedClientConfig().toggleAction(i);
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
            AbilityWheelEditorScreen.show();
        }
    }

    private static void swapEnergyDisplay() {
        var manager = SyntheticsClient.getInstance().getManager();
        manager.displayEnergy = !manager.displayEnergy;
    }


}
