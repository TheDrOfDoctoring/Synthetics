package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientEventHandler;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.editor.WheelEditorScreen;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.RadialMenuSlot;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class GenericWheelScreen<T extends RadialMenuSlot> extends Screen {

    private final @NotNull LocalPlayer player;
    protected GenericRadialMenu<T> menu;

    protected GenericWheelScreen(@NotNull LocalPlayer player) {
        super(Component.translatable("menu.title.synthetics.ability_wheel"));
        this.player = player;
    }

    public GenericRadialMenu<T> menu() {
        return menu;
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(menu.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override // tick
    public void tick() {
        super.tick();

        menu.tick();

        if (menu.isClosed()) {
            this.onClose();
        }
    }

    @Override // mouseReleased
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int mouseButton) {
        processClick(true);
        return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, mouseButton);
    }

    public void processClick(boolean triggeredByMouse) {
        menu.clickItem();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if(minecraft == null || minecraft.player == null) return;
        var poseStack = graphics.pose();
        poseStack.pushPose();
        super.render(graphics, mouseX, mouseY, partialTicks);
        poseStack.popPose();


        menu.draw(graphics, partialTicks, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void processInputEvent(MovementInputUpdateEvent event) {
        Options settings = Minecraft.getInstance().options;
        Input eInput = event.getInput();
        eInput.up = isKeyDown0(settings.keyUp);
        eInput.down = isKeyDown0(settings.keyDown);
        eInput.left = isKeyDown0(settings.keyLeft);
        eInput.right = isKeyDown0(settings.keyRight);

        eInput.forwardImpulse = eInput.up == eInput.down ? 0.0F : (eInput.up ? 1.0F : -1.0F);
        eInput.leftImpulse = eInput.left == eInput.right ? 0.0F : (eInput.left ? 1.0F : -1.0F);
        eInput.jumping = isKeyDown0(settings.keyJump);
        eInput.shiftKeyDown = isKeyDown0(settings.keyShift);
        if (player.isMovingSlowly()) {
            eInput.leftImpulse = (float) ((double) eInput.leftImpulse * 0.3D);
            eInput.forwardImpulse = (float) ((double) eInput.forwardImpulse * 0.3D);
        }
    }

    private static boolean isKeyDown0(KeyMapping keybind)
    {
        if (keybind.isUnbound())
            return false;

        return switch (keybind.getKey().getType())
        {
            case KEYSYM ->
                    InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue());
            case MOUSE ->
                    GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue()) == GLFW.GLFW_PRESS;
            default -> false;
        };
    }

    @EventBusSubscriber(modid = Synthetics.MODID)
    public static class WheelClientEvents {

        @SubscribeEvent
        public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
            if (!event.getName().equals(VanillaGuiLayers.CROSSHAIR))
                return;
            if (Minecraft.getInstance().screen instanceof GenericWheelScreen<?> || Minecraft.getInstance().screen instanceof WheelEditorScreen) {
                event.setCanceled(true);
            }
        }
        @SubscribeEvent
        public static void updateInputEvent(MovementInputUpdateEvent event) {
            if (Minecraft.getInstance().screen instanceof GenericWheelScreen<?> screen) {
                if(SyntheticsClientEventHandler.handlePlayerInput(Minecraft.getInstance(), event)) {
                    screen.processInputEvent(event);
                }

            }
        }


    }
}
