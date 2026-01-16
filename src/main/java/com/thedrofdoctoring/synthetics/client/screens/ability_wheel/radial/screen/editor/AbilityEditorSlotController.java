package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.editor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.AbilityWheel;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.WheelManager;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.AbilityRadialSlot;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbilityEditorSlotController implements IEditorSlotController<AbilityRadialSlot, Ability> {

    private static final int WIDTH = 137;
    private static final int HEIGHT = 150;
    private static final int ENTRY_HEIGHT = 20;

    @Override
    public List<Ability> getPossibleSlotEntries(Player player) {
        return SyntheticsPlayer.get(player).getAbilityManager().addedAbilities()
                .stream()
                .filter(p -> p.abilityType() instanceof ActiveAbilityType<?>).toList();
    }

    @Override
    public AbilityRadialSlot createEmptySlot(GenericRadialMenu<AbilityRadialSlot> owner) {
        return new AbilityRadialSlot(owner, AbilityWheel.EMPTY_SLOT);
    }

    @Override
    public void save(List<List<AbilityRadialSlot>> wheels, Player player) {
        List<AbilityWheel> wheelData = wheels
                .stream()
                .map(
                        slots -> new AbilityWheel(slots.stream()
                                .map(AbilityRadialSlot::slotData)
                                .toList()))
                .toList();
        WheelManager wheelManager = SyntheticsClient.getInstance().getWheelManager();
        wheelManager.setWheelForLevel(player.level(), wheelData);
        wheelManager.saveWheelsToFile(player.level());

    }

    @Override
    public ObjectSelectionList<?> createSelectionList(WheelEditorScreen<Ability, AbilityRadialSlot> parent, Player player) {
        return new AbilitySelectionList(Minecraft.getInstance(), parent, getPossibleSlotEntries(player), 3, 20, 140 - 3, parent.height - 70);
    }

    public static class AbilitySelectionList extends ObjectSelectionList<AbilityListEntry> {

        private final List<Ability> abilities;
        private final WheelEditorScreen<Ability, AbilityRadialSlot> parent;

        public AbilitySelectionList(
                Minecraft pMinecraft, WheelEditorScreen<Ability, AbilityRadialSlot> parent, List<Ability> abilities,
                int x, int y, int width,
                int height
        ) {
            super(pMinecraft, width, height, y, 20);
            this.setX(x);
            this.abilities = abilities;
            this.parent = parent;
            refresh();
        }

        public void refresh() {
            for(Ability ability : abilities) {
                this.addEntry(new AbilityListEntry(parent, ability));
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }
        @Override
        protected void renderDecorations(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
            graphics.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getBottom() + 4, -16777216, 0);
            graphics.fillGradient(this.getX(), this.getY() - 4, this.getRight() - 6, this.getBottom(), 0, -16777216);
            super.renderDecorations(graphics, pMouseX, pMouseY);
        }

        @Override
        protected void renderItem(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
            super.renderItem(graphics, pMouseX, pMouseY, pPartialTick, pIndex, pLeft, pTop, pWidth - 6, pHeight);
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int p_283242_, int p_282891_, float p_283683_) {

            guiGraphics.fillGradient(this.getX(), this.getY(), this.getRight() - 6, this.getBottom(), -1072689136, -804253680);
            super.renderWidget(guiGraphics, p_283242_, p_282891_, p_283683_);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 200);
            guiGraphics.pose().popPose();
        }


        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        public int getRowLeft() {
            return super.getRowLeft() - 2;
        }

        @Override
        protected int getRowTop(int pIndex) {
            return super.getRowTop(pIndex) - 4;
        }

        @Override
        public int getMaxScroll() {
            return Math.max(0, super.getMaxScroll() - 4);
        }

        // don't draw background / selection

        @Override
        protected void renderListBackground(@NotNull GuiGraphics guiGraphics) {}

        @Override
        protected void renderSelection(@NotNull GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {}
    }

    public static class AbilityListEntry extends ObjectSelectionList.Entry<AbilityListEntry> {

        protected static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"), ResourceLocation.withDefaultNamespace("widget/button_highlighted"));

        private final WheelEditorScreen<Ability, AbilityRadialSlot> parent;
        private final Ability ability;

        public AbilityListEntry(WheelEditorScreen<Ability, AbilityRadialSlot> parentScreen, Ability entry) {
            parent = parentScreen;
            ability = entry;
        }

        @Override
        public @NotNull Component getNarration() {
            return ability.abilityType().title(ability.abilityData());
        }

        public void playDownSound(SoundManager handler) {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            this.parent.selectEntry(ability);
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return super.mouseClicked(pMouseX, pMouseY, pButton);

        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            boolean isSelected = pIsMouseOver || this.parent.selectedEntry() == this.ability;
            pose.translate(0, 0, isSelected ? 2 : 1);
            guiGraphics.blitSprite(SPRITES.get(true, isSelected), pLeft, pTop, pWidth, pHeight + 5);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.drawCenteredString(minecraft.font, this.getNarration(), pLeft + pWidth / 2, pTop + 5, 0xFFFFFF);
            pose.popPose();
        }
    }

}
