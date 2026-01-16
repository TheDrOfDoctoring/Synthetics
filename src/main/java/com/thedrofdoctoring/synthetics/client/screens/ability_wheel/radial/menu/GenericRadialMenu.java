package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsKeys;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.DrawingContext;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.IRadialMenuHost;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.RadialMenuSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.util.TriConsumer;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public abstract class GenericRadialMenu<T extends RadialMenuSlot> {

    public static final float OPEN_ANIMATION_LENGTH = 2.5f;

    public  final IRadialMenuHost host;
    private final Minecraft minecraft;

    protected List<T> visibleItems;
    protected int selectedWheel;

    public static final int backgroundColor = 0x3F000000;
    public static final int backgroundColorHover = 0x3FFFFFFF;


    public enum State {
        INITIALIZING,
        OPENING,
        NORMAL,
        CLOSING,
        CLOSED
    }

    private State state = State.INITIALIZING;
    public double startAnimation;
    public float animProgress;
    public float radiusIn;
    public float radiusOut;
    public float itemRadius;
    public float animTop;

    private Component centralText;

    public GenericRadialMenu(Minecraft minecraft, IRadialMenuHost host) {
        this.minecraft = minecraft;
        this.host = host;
        this.visibleItems = Collections.emptyList();

    }

    public int selectedWheel() {
        return selectedWheel;
    }

    public void setCentralText(@Nullable Component centralText) {
        this.centralText = centralText;
    }

    public Component getCentralText() {
        return centralText;
    }

    public abstract List<List<T>> getWheels();


    public abstract void setWheels(List<List<T>> wheels);

    public int getHoveredIndex() {
        for (int i = 0; i < visibleItems.size(); i++) {
            if (visibleItems.get(i).isHovered())
                return i;
        }
        return -1;
    }


    public @Nullable RadialMenuSlot getHoveredItem() {
        for (RadialMenuSlot item : visibleItems) {
            if (item.isHovered())
                return item;
        }
        return null;
    }



    public void setHovered(int which) {
        for (int i = 0; i < visibleItems.size(); i++) {
            visibleItems.get(i).setHovered(i == which);
        }
    }

    public int getVisibleItemCount() {
        return visibleItems.size();
    }

    public void clickItem() {
        if (Objects.requireNonNull(state) == State.NORMAL) {
            RadialMenuSlot item = getHoveredItem();
            if (item != null) {
                item.onClick();
                return;
            }
        }
        onClickOutside();
    }




    public abstract void onClickOutside();

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    public boolean isReady() {
        return state == State.NORMAL;
    }


    public void add(T item) {
        visibleItems.add(item);
    }

    public void addAll(Collection<T> cachedMenuItems) {
        visibleItems.addAll(cachedMenuItems);
    }

    public void clear() {
        visibleItems.clear();
    }

    public void close() {
        state = State.CLOSING;
        startAnimation = minecraft.level.getGameTime() + (double) minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        animProgress = 1.0f;
        setHovered(-1);
    }

    public void tick() {
        if (state == State.INITIALIZING) {
            startAnimation = minecraft.level.getGameTime() + (double) minecraft.getTimer().getGameTimeDeltaPartialTick(false);
            state = State.OPENING;
            animProgress = 0;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == SyntheticsKeys.WHEEL_RIGHT_WHEEL.getKey().getValue()) {
            swapWheel(1);
            return true;
        } else if(keyCode == SyntheticsKeys.WHEEL_LEFT_WHEEL.getKey().getValue()) {
            swapWheel(-1);
            return true;
        }
        return false;
    }

    public abstract void swapWheel(int directionAmount);

    public void draw(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        updateAnimationState(partialTicks);

        if (isClosed())
            return;

        if (isReady())
            processMouse(mouseX, mouseY);

        Screen owner = host.getScreen();
        Font font = host.getFontRenderer();

        boolean animated = state == State.OPENING || state == State.CLOSING;
        radiusIn = animated ? Math.max(0.1f, 30 * animProgress) : 30;
        radiusOut = radiusIn * 2;
        itemRadius = (radiusIn + radiusOut) * 0.5f;
        animTop = animated ? (1 - animProgress) * owner.height / 2.0f : 0;

        int x = owner.width / 2;
        int y = owner.height / 2;
        float z = 0;

        var poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, animTop, 0);

        drawBackground(poseStack, x, y, z, radiusIn, radiusOut);

        poseStack.popPose();

        if (isReady()) {
            poseStack.pushPose();
            drawItems(graphics, x, y, z, owner.width, owner.height, font);
            poseStack.popPose();

            Component currentCentralText = centralText;
            int slotIndex = 0, slotSize = 0;
            for (RadialMenuSlot slot : visibleItems) {
                if (slot.isHovered()) {
                    if (slot.getCentralText() != null) {
                        currentCentralText = slot.getCentralText();
                        slotIndex = slot.selectedIndex() + 1;
                        slotSize = slot.size();
                    }
                    break;
                }
            }

            if (currentCentralText != null) {
                String text = currentCentralText.getString();
                String selectedSlot = slotIndex + " / " + slotSize;
                float textWidth = font.width(text);
                float selectedWidth = font.width(selectedSlot);
                float textX = (owner.width - textWidth) / 2.0f;
                float textY = (owner.height - font.lineHeight) / 2.0f;
                graphics.drawString(font, text, textX, textY, 0xFFFFFFFF, true);
                graphics.drawString(font, selectedSlot, (owner.width - selectedWidth) / 2.0f, textY + font.lineHeight, 0xFF808080, true);
            }
            drawWheelCounter(graphics, font, radiusIn);

            poseStack.pushPose();
            drawTooltips(graphics, mouseX, mouseY);
            poseStack.popPose();
        }
    }

    public abstract int totalWheels();

    private void drawWheelCounter(GuiGraphics graphics, Font font, float radius) {
        Screen owner = host.getScreen();
        String text = "Wheel: " + (this.selectedWheel + 1) + " / " + this.totalWheels();
        float textWidth = font.width(text);
        graphics.drawString(font, text, (owner.width - textWidth) / 2.0f, (owner.height - font.lineHeight - radius) / 1.1f, 0xFF808080, true);
    }

    private void updateAnimationState(float partialTicks) {
        float openAnimation = 0;
        Screen owner = host.getScreen();
        switch (state) {
            case OPENING:
                openAnimation = (float) ((minecraft.level.getGameTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH);
                if (openAnimation >= 1.0 || getVisibleItemCount() == 0) {
                    openAnimation = 1;
                    state = State.NORMAL;
                }
                break;
            case CLOSING:
                openAnimation = 1 - (float) ((minecraft.level.getGameTime() + partialTicks - startAnimation) / OPEN_ANIMATION_LENGTH);
                if (openAnimation <= 0 || getVisibleItemCount() == 0) {
                    openAnimation = 0;
                    state = State.CLOSED;
                }
                break;
        }
        animProgress = openAnimation; // MathHelper.clamp(openAnimation, 0, 1);
    }

    private void drawTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        Screen owner = host.getScreen();
        Font fontRenderer = host.getFontRenderer();
        for (RadialMenuSlot slot : visibleItems) {
            if (slot.isHovered()) {
                DrawingContext context = new DrawingContext(graphics, owner.width, owner.height, mouseX, mouseY, 0, fontRenderer);
                slot.drawTooltips(context);
            }
        }
    }

    private void drawItems(GuiGraphics graphics, int x, int y, float z, int width, int height, Font font) {
        iterateVisible((item, s, e) -> {
            float middle = (s + e) * 0.5f;
            float posX = x + itemRadius * (float) Math.cos(middle);
            float posY = y + itemRadius * (float) Math.sin(middle);

            DrawingContext context = new DrawingContext(graphics, width, height, posX, posY, z, font);
            item.draw(context);
        });
    }

    private void iterateVisible(TriConsumer<T, Float, Float> consumer) {
        int numItems = visibleItems.size();
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            T item = visibleItems.get(i);
            consumer.accept(item, s, e);
        }
    }

    private void drawBackground(PoseStack matrixStack, float x, float y, float z, float radiusIn, float radiusOut) {
        if (!visibleItems.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            iterateVisible((slot, s, e) -> {
                int colour = getSliceColour(slot);
                drawPieArc(slot, builder, x, y, z, radiusIn, radiusOut, s, e, colour);
            });
            BufferUploader.drawWithShader(builder.buildOrThrow());
            RenderSystem.disableBlend();
        }
    }

    protected int getSliceColour(RadialMenuSlot slot) {
        return slot.isHovered() ? backgroundColorHover : backgroundColor;
    }

    private static final float PRECISION = 2.5f / 360.0f;

    protected void drawPieArc(T slot, BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int colour) {
        float angle = endAngle - startAngle;
        int sections = Math.max(1, Mth.ceil(angle / PRECISION));

        angle = endAngle - startAngle;

        int b = colour         & 0xFF;
        int g = (colour >> 8)  & 0xFF;
        int r = (colour >> 16) & 0xFF;
        int a = (colour >> 24) & 0xFF;

        float slice = angle / sections;

        for (int i = 0; i < sections; i++) {
            float angle1 = startAngle + i * slice;
            float angle2 = startAngle + (i + 1) * slice;

            float pos1InX = x + radiusIn * (float) Math.cos(angle1);
            float pos1InY = y + radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
            float pos2InX = x + radiusIn * (float) Math.cos(angle2);
            float pos2InY = y + radiusIn * (float) Math.sin(angle2);

            buffer.addVertex(pos1OutX, pos1OutY, z).setColor(r, g, b, a);
            buffer.addVertex(pos1InX, pos1InY, z).setColor(r, g, b, a);
            buffer.addVertex(pos2InX, pos2InY, z).setColor(r, g, b, a);
            buffer.addVertex(pos2OutX, pos2OutY, z).setColor(r, g, b, a);
        }
    }

    public void cyclePrevious() {
        int numItems = getVisibleItemCount();
        int which = getHoveredIndex();
        which--;
        if (which < 0)
            which = numItems - 1;
        setHovered(which);

        moveMouseToItem(which, numItems);
    }

    public void cycleNext() {
        int numItems = getVisibleItemCount();
        int which = getHoveredIndex();
        if (which < 0)
            which = 0;
        else {
            which++;
            if (which >= numItems)
                which = 0;
        }
        moveMouseToItem(which, numItems);
        setHovered(which);
    }

    private void moveMouseToItem(int which, int numItems) {
        Screen owner = host.getScreen();
        int x = owner.width / 2;
        int y = owner.height / 2;
        float angle = (float) getAngleFor(which, numItems);
        setMousePosition(
                x + itemRadius * Math.cos(angle),
                y + itemRadius * Math.sin(angle)
        );
    }

    private void setMousePosition(double x, double y) {
        Screen owner = host.getScreen();
        Window mainWindow = minecraft.getWindow();
        GLFW.glfwSetCursorPos(mainWindow.getWindow(), (int) (x * mainWindow.getScreenWidth() / owner.width), (int) (y * mainWindow.getScreenHeight() / owner.height));
    }

    private static final double TWO_PI = 2.0 * Math.PI;

    private void processMouse(int mouseX, int mouseY) {
        if (!isReady())
            return;

        int numItems = getVisibleItemCount();

        Screen owner = host.getScreen();
        int x = owner.width / 2;
        int y = owner.height / 2;
        double a = Math.atan2(mouseY - y, mouseX - x);
        double d = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        if (numItems > 0) {
            double s0 = getAngleFor(0 - 0.5, numItems);
            double s1 = getAngleFor(numItems - 0.5, numItems);
            while (a < s0) {
                a += TWO_PI;
            }
            while (a >= s1) {
                a -= TWO_PI;
            }
        }

        int hovered = -1;
        for (int i = 0; i < numItems; i++) {
            float s = (float) getAngleFor(i - 0.5, numItems);
            float e = (float) getAngleFor(i + 0.5, numItems);

            if (a >= s && a < e && d >= radiusIn && d < radiusOut) {
                hovered = i;
                break;
            }
        }
        setHovered(hovered);

    }

    private double getAngleFor(double i, int numItems) {
        if (numItems == 0)
            return 0;
        return ((i / numItems) + 0.25) * TWO_PI + Math.PI;
    }


}
