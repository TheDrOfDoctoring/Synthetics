package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.editor;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsKeys;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.ISlotEntry;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.AbilityWheelScreen;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.GenericWheelScreen;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.RadialMenuSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class WheelEditorScreen<T extends ISlotEntry<U>, U extends RadialMenuSlot> extends Screen {

    protected static final ResourceLocation BACKGROUND = Synthetics.rl("wheel/background");

    private final GenericWheelScreen<U> screen;
    private final LocalPlayer player;
    private final IEditorSlotController<U, T> controller;

    private ObjectSelectionList<?> list;
    private List<List<U>> currentWheels;
    private @Nullable T selectedEntry;

    protected WheelEditorScreen(IEditorSlotController<U, T> controller, GenericWheelScreen<U> screen, LocalPlayer player) {
        super(Component.translatable("menu.title.synthetics.wheel_editor"));
        this.screen = screen;
        this.player = player;
        this.controller = controller;
        this.currentWheels = screen.menu().getWheels();
    }

    public static void show() {
        if(Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            Minecraft.getInstance().setScreen(new WheelEditorScreen<>(new AbilityEditorSlotController(), new AbilityWheelScreen(player), player));

        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == SyntheticsKeys.WHEEL_ADD_WHEEL.getKey().getValue()) {
            List<List<U>> wheels = new LinkedList<>(screen.menu().getWheels());
            wheels.add(List.of(controller.createEmptySlot(screen.menu())));
            screen.menu().setWheels(wheels);
            this.currentWheels = wheels;
            return true;
        }
        if(keyCode == SyntheticsKeys.WHEEL_ADD_SLOT.getKey().getValue()) {
            if(screen.menu().selectedWheel() > -1) {
                GenericRadialMenu<U> menu = screen.menu();
                List<U> wheel = new LinkedList<>(menu.getWheels().get(menu.selectedWheel()));
                List<List<U>> wheels = new LinkedList<>(menu.getWheels());
                wheel.add(controller.createEmptySlot(menu));
                wheels.remove(menu.selectedWheel());
                wheels.add(menu.selectedWheel(), wheel);
                screen.menu().setWheels(wheels);
                this.currentWheels = wheels;
                return true;
            }
        }
        return screen.keyPressed(keyCode, scanCode, modifiers);
    }

    public void selectEntry(T entry) {
        this.deselectEntry();
        this.selectedEntry = entry;
    }

    public void deselectEntry() {
        if(this.selectedEntry != null) {
            this.selectedEntry = null;
            this.list.setSelected(null);
        }
    }

    public @Nullable T selectedEntry() {
        return selectedEntry;
    }

    @Override
    protected void init() {
        super.init();

        this.screen.init(Minecraft.getInstance(), width, height);
        this.addRenderableWidget(new ExtendedButton(3, this.height - 24, 140-4, 20, Component.translatable("synthetics.gui.save"), (context) -> this.onClose()));
        this.list = this.addRenderableWidget(this.controller.createSelectionList(this, player));
    }

    public void save() {
        this.controller.save(currentWheels, player);
    }

    @Override
    public void onClose() {
        this.save();
        super.onClose();
    }


    @Override
    public void removed() {
        this.save();
        super.removed();
    }

    @Override
    public void tick() {
        super.tick();
        screen.tick();
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int mouseButton) {

        if(mouseButton == 1) {
            return onRightClick();
        }
        if(mouseButton == 0) {
            return onLeftClick();
        }

        return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, mouseButton);
    }

    @SuppressWarnings("unchecked")
    public boolean onLeftClick() {
        if(this.selectedEntry == null || this.screen.menu().getHoveredItem() == null || this.currentWheels.isEmpty()) return false;
        RadialMenuSlot slot = this.screen.menu().getHoveredItem();
        RadialMenuSlot newSlot = selectedEntry.addToSlot(screen.menu(), (U) slot, player.level());

        this.replaceSlotInWheel(slot, newSlot);
        this.deselectEntry();

        return true;

    }

    @SuppressWarnings("unchecked")
    private void replaceSlotInWheel(RadialMenuSlot oldSlot, RadialMenuSlot newSlot) {
        int selectedWheel = screen.menu().selectedWheel();
        List<List<U>> wheels = new LinkedList<>(screen.menu().getWheels());
        List<RadialMenuSlot> slots = new LinkedList<>(screen.menu().getWheels().get(selectedWheel));
        int slotIndex = slots.indexOf(oldSlot);
        slots.remove(slotIndex);
        slots.add(slotIndex, newSlot);
        wheels.remove(selectedWheel);
        wheels.add(selectedWheel, (List<U>) slots);
        screen.menu().setWheels(wheels);
        this.currentWheels = wheels;
    }

    public boolean onRightClick() {
        RadialMenuSlot slot = screen.menu().getHoveredItem();
        if(slot != null) {
            if(slot.isEmpty() && !screen.menu().getWheels().isEmpty()) {
                int selectedWheel = screen.menu().selectedWheel();
                List<U> slots = new LinkedList<>(screen.menu().getWheels().get(selectedWheel));
                if(slots.size() == 1) {
                    List<List<U>> wheels = new LinkedList<>(screen.menu().getWheels());
                    wheels.remove(selectedWheel);
                    screen.menu().setWheels(wheels);
                    this.currentWheels = wheels;
                } else {
                    int hoveredSlot   = screen.menu().getHoveredIndex();
                    List<List<U>> wheels = new LinkedList<>(screen.menu().getWheels());
                    wheels.remove(selectedWheel);
                    slots.remove(hoveredSlot);
                    wheels.add(selectedWheel, slots);
                    screen.menu().setWheels(wheels);
                    this.currentWheels = wheels;
                }
                return true;

            } else if(!screen.menu().getWheels().isEmpty()){
                int selectedWheel = screen.menu().selectedWheel();
                RadialMenuSlot replace = slot.removeSelectedItem();
                this.replaceSlotInWheel(slot, replace);
                return true;

            }
            return false;

        }

        int selectedWheel = screen.menu().selectedWheel();
        if(selectedWheel > -1 && !currentWheels.isEmpty()) {
            List<U> slots = screen.menu().getWheels().get(selectedWheel);
            if(slots.isEmpty() || (slots.size() == 1 && slots.getFirst().isEmpty())) {
                List<List<U>> wheels = new LinkedList<>(screen.menu().getWheels());
                wheels.remove(selectedWheel);
                screen.menu().setWheels(wheels);
                this.currentWheels = wheels;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.screen.menu().getHoveredItem() != null) {
            return screen.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.setColor(0.5F, 0.5F, 0.5F, 1.0F);
        guiGraphics.blitSprite(BACKGROUND, 0,0, 143, this.height);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if(!this.currentWheels.isEmpty()) {
            screen.menu().draw(graphics, partialTicks, mouseX, mouseY);
        }
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }



}
