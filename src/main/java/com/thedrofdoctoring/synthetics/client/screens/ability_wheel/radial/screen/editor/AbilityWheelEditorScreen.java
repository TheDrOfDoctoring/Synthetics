package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.editor;

import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.client.config.AbilityKeyManager;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsKeys;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.AbilityWheelScreen;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.AbilityRadialSlot;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AbilityWheelEditorScreen extends WheelEditorScreen<Ability, AbilityRadialSlot> {

    private KeyBindingList list;

    protected AbilityWheelEditorScreen(LocalPlayer player) {
        super(new AbilityEditorSlotController(), new AbilityWheelScreen(player), player);
    }

    @Override
    protected void init() {
        super.init();
        this.list = this.addRenderableWidget(new KeyBindingList(this.width - 150, 20, 140-8, this.height - 60));
    }

    public static void show() {
        if(Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            Minecraft.getInstance().setScreen(new AbilityWheelEditorScreen(player));
        }
    }

    public Map<Integer, Holder<Ability>> getAbilityBindings() {
        return list.abilityBindings;
    }

    public class KeyBindingList extends ContainerObjectSelectionList<KeyBindingList.KeyBindingSetting> {

        private final AbilityKeyManager keys;
        private final Map<Integer, Holder<Ability>> abilityBindings = new HashMap<>();

        public KeyBindingList(int x, int y, int pWidth, int pHeight) {
            super(Minecraft.getInstance(), pWidth, pHeight, y, 20);
            this.setX(x);
            this.keys = SyntheticsClient.getInstance().getAdvancedClientConfig().currentKeyManager();
            if (keys != null) {
                replaceEntries(SyntheticsKeys.ABILITY_HOTKEYS.int2ObjectEntrySet()
                        .stream()
                        .map(pair -> new KeyBindingSetting(
                                this.abilityBindings,
                                pair.getIntKey(),
                                pair.getValue(),
                                keys.getBoundAbilityHolder(pair.getIntKey())))
                        .sorted(Comparator.comparingInt((KeyBindingSetting o) -> o.index))
                        .toList());
            }

        }

        @Override
        protected void renderListBackground(@NotNull GuiGraphics graphics) {
        }


        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public @NotNull Optional<GuiEventListener> getChildAt(double pMouseX, double pMouseY) {
            return super.getChildAt(pMouseX, pMouseY);
        }


        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
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
            return super.getRowTop(pIndex);
        }

        public void clearAbilities() {
            this.children().forEach(entry -> entry.switchAbility(null));
        }

        public Map<Integer, Holder<Ability>> abilityBindings() {
            return abilityBindings;
        }

        private class KeyBindingSetting extends ContainerObjectSelectionList.Entry<KeyBindingSetting> {

            private final int index;
            private Holder<Ability> ability;
            private final StringWidget stringWidget;
            private ImageWidget imageWidget;

            private final Map<Integer, Holder<Ability>> abilityBindings;

            public KeyBindingSetting(Map<Integer, Holder<Ability>> abilityBindings, int index, KeyMapping keyMapping, Holder<Ability> entry) {
                this.index = index;
                this.stringWidget = new StringWidget(0, 2, 80, 20, keyMapping.getTranslatedKeyMessage(), Minecraft.getInstance().font);
                this.ability = entry;
                this.abilityBindings = abilityBindings;
                if(entry != null) {
                    this.abilityBindings.put(index, entry);
                }
                applyAbility(ability);
            }

            @Override
            public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
                if (selectedEntry != null && Minecraft.getInstance().level != null) {
                    Minecraft.getInstance().level.registryAccess()
                            .lookup(SyntheticsData.ABILITIES)
                            .flatMap(lookup -> lookup.get(ResourceKey.create(SyntheticsData.ABILITIES, selectedEntry.id())))
                            .ifPresent(this::switchAbility);

                    selectedEntry = null;
                    return true;
                }
                return false;
            }

            private void switchAbility(@Nullable Holder<Ability> ability) {
                applyAbility(ability);
                this.abilityBindings.put(index, ability);
            }

            private void applyAbility(@Nullable Holder<Ability> ability) {
                this.ability = ability;
                if (ability != null) {
                    this.imageWidget = ImageWidget.texture(16, 16, ability.value().textureLocation(), 16, 16);
                    this.imageWidget.setPosition(90, 2);
                } else {
                    //noinspection DataFlowIssue
                    this.imageWidget = ImageWidget.texture(16, 16, null, 16, 16);
                    this.imageWidget.setPosition(90, 2);
                    this.imageWidget.visible = false;
                }
            }

            @Override
            public @NotNull List<? extends NarratableEntry> narratables() {
                return List.of(stringWidget, imageWidget);
            }

            @Override
            public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean p_93531_, float pPartialTick) {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(pLeft, pTop, 0);
                stringWidget.render(pGuiGraphics, pMouseX - pLeft, pMouseY - pTop, pPartialTick);
                imageWidget.render(pGuiGraphics, pMouseX - pLeft, pMouseY - pTop, pPartialTick);
                pGuiGraphics.pose().popPose();
            }

            @Override
            public void renderBack(@NotNull GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
                {}
            }

            @Override
            public @NotNull List<? extends GuiEventListener> children() {
                return List.of(stringWidget, imageWidget);
            }
        }
    }


    }
