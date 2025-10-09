package com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.capabilities.ComplexityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.*;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundInstallableMenuPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class AugmentationChamberScreen extends AbstractContainerScreen<AugmentationChamberMenu> {

    private static final ResourceLocation BACKGROUND = Synthetics.rl("textures/gui/container/augmentation_chamber.png");
    private static final ResourceLocation DESCRIPTION = Synthetics.rl("generic/description");
    private static final ResourceLocation RIGHT_CLICK_SPRITE = Synthetics.rl("icons/right_click");

    public static final int WIDTH = 176;
    public static final int HEIGHT = 194;

    private static final int INSTALL_WIDTH = 70;
    private static final int INSTALL_HEIGHT = 14;


    private Vec3 mousePos;
    private Vec3 rightClickPos;
    public boolean rightClicked;
    private boolean clicked;
    private int selectedAbility;

    private boolean displayAbilities;
    private final @NotNull Player player;
    private final @NotNull SyntheticsPlayer synthetics;
    private final PlayerSyntheticDisplayScreen[] displayLayers = new PlayerSyntheticDisplayScreen[BodyPartType.Layer.values().length];
    private PlayerSyntheticDisplayScreen selectedLayer;
    private int selectedLayerIndex;
    public int guiLeft;
    public int guiTop;


    public AugmentationChamberScreen(AugmentationChamberMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
        this.minecraft = Minecraft.getInstance();
        assert minecraft.player != null;
        this.player = minecraft.player;
        this.synthetics = SyntheticsPlayer.get(player);
        int i = 0;
        for(var layer : BodyPartType.Layer.values()) {
            this.displayLayers[i] = new PlayerSyntheticDisplayScreen(this, layer, i);
            i++;
        }
        this.selectedLayer = displayLayers[0];
        this.selectedLayerIndex = 0;
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;
        this.leftPos = guiLeft;
        this.topPos = guiTop;

    }
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.selectedLayer.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderHover(guiGraphics, partialTick, mouseX, mouseY);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.guiLeft, this.guiTop, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        for(PlayerSyntheticDisplayScreen screen : displayLayers) {
            screen.drawTab(guiGraphics, 22 + WIDTH / 2, this.guiTop + 5, screen == selectedLayer);
            screen.drawIcon(guiGraphics, 22 + WIDTH / 2, this.guiTop + 5);
        }
        guiGraphics.pose().popPose();

        this.selectedLayer.renderHover(guiGraphics, mouseX - guiLeft, mouseY - guiTop);
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void renderHover(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = guiLeft + 8;
        int y = guiTop + 33;
        if(isMouseOverButton(mouseX, x, mouseY, y)) {
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + INSTALL_WIDTH, y + INSTALL_HEIGHT, 0x66ffffff, 0x77ffffff, 0);
        }
        x = guiLeft + 3;
        y = guiTop + 15;
        if(this.hoveredSlot != null && this.hoveredSlot.index == 0) {
            ItemStack stack = this.menu.getInputContainer().getItem(0);
            if(stack.getItem() instanceof InstallableItem<?> item) {
                IBodyInstallable<?> installable = item.getInstallableComponent(stack);
                List<FormattedCharSequence> text = Language.getInstance().getVisualOrder(getTextForInstallable(installable, synthetics, displayAbilities, selectedAbility));
                int size = 84;
                PoseStack pose = guiGraphics.pose();
                guiGraphics.blitSprite(RIGHT_CLICK_SPRITE, x + 12, y - 3, 15, 16);
                pose.pushPose();
                float xScale = 0.5f;
                float yScale = 0.5f;
                pose.scale(xScale, yScale, 1f);
                guiGraphics.blitSprite(DESCRIPTION, (int) (x / xScale), (int) ((y + 15) / yScale), (int) (size / xScale), 5 + (text.size() + 1) * 8);
                for(int i = 0; i < text.size(); i++) {
                    guiGraphics.drawString(Objects.requireNonNull(this.minecraft).font, text.get(i), ((x + 2) / xScale), ((y + 17 + (i * 8 * yScale)) / yScale), -1, true);
                }
                pose.popPose();

            }

        }
    }
    public static Component getAbilityTitle(Holder<SyntheticAbility> ability) {
        return ability.value().abilityType().title();
    }

    public static List<FormattedText> getTextForInstallable(IBodyInstallable<?> installable, SyntheticsPlayer synthetics, boolean displayAbilities, int selectedAbility) {
        List<FormattedText> text = new ArrayList<>();

        if(synthetics.isInstalled(installable)) {
            text.add(Component.translatable("text.synthetics.augmentation.already_installed").withStyle(ChatFormatting.AQUA));
            return text;
        }
        if(displayAbilities && installable.abilities().isPresent()) {
            HolderSet<SyntheticAbility> abilities = installable.abilities().get();
            int maxSize = abilities.size();
            if(selectedAbility >= maxSize) {
                selectedAbility = 0;
            }
            if(selectedAbility < maxSize) {
                if(selectedAbility > 0) {
                    MutableComponent component = Component.empty();
                    component.append(getAbilityTitle(abilities.get(selectedAbility - 1))).withStyle(ChatFormatting.GRAY);
                    text.add(component);
                }
                MutableComponent component = Component.empty();
                component.append(getAbilityTitle(abilities.get(selectedAbility))).withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.UNDERLINE);
                text.add(component);
                if(selectedAbility < maxSize - 1) {
                    MutableComponent after = Component.empty();
                    after.append(getAbilityTitle(abilities.get(selectedAbility + 1))).withStyle(ChatFormatting.GRAY);
                    text.add(after);
                }
                text.add(Component.empty());
                SyntheticAbility ability = abilities.get(selectedAbility).value();
                if(ability.options().isPresent()) {
                    ActiveAbilityOptions options = ability.options().get();
                    text.add(Component.translatable("text.synthetics.augmentation_ability_cooldown", options.cooldown()).withStyle(ChatFormatting.BLUE));
                    if(options.duration() > 0) {
                        text.add(Component.translatable("text.synthetics.augmentation_ability_duration", options.duration()).withStyle(ChatFormatting.BLUE));
                    }
                    if(options.powerCost() > 0) {
                        text.add(Component.translatable("text.synthetics.augmentation_ability_power_cost", options.powerCost()).withStyle(ChatFormatting.BLUE));
                    }
                    if(options.powerDrain() > 0) {
                        text.add(Component.translatable("text.synthetics.augmentation_ability_power_drain", options.powerCost()).withStyle(ChatFormatting.BLUE));
                    }
                }
                text.add(Component.translatable("text.synthetics.augmentation_ability_factor", ability.factor()).withStyle(ChatFormatting.BLUE));
                if(ability.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    text.add(Component.translatable("text.synthetics.augmentation_ability_operation_add").withStyle(ChatFormatting.BLUE));
                } else {
                    text.add(Component.translatable("text.synthetics.augmentation_ability_operation_mult").withStyle(ChatFormatting.BLUE));
                }


            }

            return text;
        }
        text.add(Component.translatable("text.synthetics.augmentation.complexity").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.UNDERLINE));
        text.add(Component.empty());
        switch (installable) {
            case SyntheticAugment augment -> {
                BodyPart part = synthetics.getPartManager().getPartForAugment(augment);
                BodySegment segment = synthetics.getPartManager().getSegmentForPart(part);
                ComplexityManager.ComplexityPairs newComplexity = synthetics.getComplexityManager().getNewComplexity(new AugmentInstance(augment, part), null);

                int maxPartComplexity = part.maxComplexity();
                int maxSegmentComplexity = segment.maxComplexity();

                text.add(Component.translatable("text.synthetics.augmentation.added_complexity", augment.complexity()).withStyle(ChatFormatting.RED));
                text.add(Component.translatable("text.synthetics.augmentation.new_part_complexity", newComplexity.partComplexity(), maxPartComplexity).withStyle(getColourForComplexity(newComplexity.partComplexity(), maxPartComplexity)));
                text.add(Component.translatable("text.synthetics.augmentation.new_segment_complexity", newComplexity.segmentComplexity(), maxSegmentComplexity).withStyle(getColourForComplexity(newComplexity.segmentComplexity(), maxSegmentComplexity)));

            }
            case BodyPart part -> {
                BodySegment segment = synthetics.getPartManager().getSegmentForPart(part);
                BodyPart currentPart = synthetics.getPartManager().getPartForType(part.type().value());
                int oldPartComplexity = synthetics.getComplexityManager().getTotalPartComplexity(part);
                int oldSegmentComplexity = synthetics.getComplexityManager().getTotalSegmentComplexity(segment);

                text.add(Component.translatable("text.synthetics.augmentation.old_part_complexity", oldPartComplexity, currentPart.maxComplexity()).withStyle(getColourForComplexity(oldPartComplexity, currentPart.maxComplexity())));
                text.add(Component.translatable("text.synthetics.augmentation.new_part_complexity", oldPartComplexity, part.maxComplexity()).withStyle(getColourForComplexity(oldPartComplexity, part.maxComplexity())));
                text.add(Component.translatable("text.synthetics.augmentation.segment_complexity", oldSegmentComplexity, segment.maxComplexity()).withStyle(getColourForComplexity(oldSegmentComplexity, segment.maxComplexity())));
            }
            case BodySegment segment -> {
                BodySegment currentSegment = synthetics.getPartManager().getSegmentForType(segment.type().value());
                int oldSegmentComplexity = synthetics.getComplexityManager().getTotalSegmentComplexity(currentSegment);
                text.add(Component.translatable("text.synthetics.augmentation.segment_complexity", oldSegmentComplexity, segment.maxComplexity()).withStyle(getColourForComplexity(oldSegmentComplexity, currentSegment.maxComplexity())));
                text.add(Component.translatable("text.synthetics.augmentation.new_segment_complexity", oldSegmentComplexity, segment.maxComplexity()).withStyle(getColourForComplexity(oldSegmentComplexity, segment.maxComplexity())));

            }
            default -> {
            }
        }
        return text;
    }

    private static ChatFormatting getColourForComplexity(int complexity, int maxComplexity) {
        if(complexity > maxComplexity) {
            return ChatFormatting.RED;
        } else {
            return ChatFormatting.BLUE;
        }
    }





    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.clicked = true;
            this.mousePos = new Vec3(mouseX, mouseY, 0);

        }
        if (button == 1) {
            this.rightClicked = true;
            this.rightClickPos = new Vec3(mouseX, mouseY, 0);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverButton(double mouseX, int buttonX, double mouseY, int buttonY) {
        return (mouseX >= buttonX && mouseX < buttonX + INSTALL_WIDTH && mouseY > buttonY && mouseY < buttonY + INSTALL_HEIGHT);
    }
    private void installButton(double mouseX, double mouseY) {
        if(isMouseOverButton(mouseX, guiLeft + 9, mouseY, guiTop + 33)) {
            ItemStack itemStack = this.menu.getInputContainer().getItem(0);
            if(itemStack.getItem() instanceof InstallableItem<?> item) {
                IBodyInstallable<?> installable = item.getInstallableComponent(itemStack);
                if(SyntheticsPlayer.get(player).canAddInstallable(installable)) {
                    Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(ServerboundInstallableMenuPacket.getInstance());
                    playSoundEffect(SoundEvents.BEACON_ACTIVATE, 0.5F);
                } else {
                    playSoundEffect(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5F);
                }
            }
        }
    }



    @SuppressWarnings("SameParameterValue")
    private void playSoundEffect(@NotNull SoundEvent event, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
        }
    }

    private void switchNodeDisplay() {
        if (this.hoveredSlot != null && this.hoveredSlot.index == 0) {
            this.displayAbilities = !this.displayAbilities;
        }
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.hoveredSlot != null && this.hoveredSlot.index == 0) {
            ItemStack stack = this.menu.getInputContainer().getItem(0);
            if(stack.getItem() instanceof InstallableItem<?> item) {
                IBodyInstallable<?> installable = item.getInstallableComponent(stack);
                if(installable.abilities().isPresent()) {
                    int max = installable.abilities().get().size();
                    int increment;
                    if(scrollY < 0) {
                        increment = 1;
                    } else {
                        increment = -1;
                    }

                    this.selectedAbility = Math.clamp(this.selectedAbility + increment, 0, max - 1);
                }
            }
        }
        if(this.selectedLayer.isMouseOverScreen(mouseX, mouseY)) {
            this.selectedLayer.mouseScrolled(mouseX - guiLeft, mouseY - guiTop, scrollX, scrollY);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.clicked) {
                if ((this.mousePos != null && this.mousePos.distanceTo(new Vec3(mouseX, mouseY, 0)) < 5)) {
                    installButton(mouseX, mouseY);
                    int i = 0;
                    for (PlayerSyntheticDisplayScreen tab : this.displayLayers) {
                        if (tab != this.selectedLayer && tab.isMouseOver((22 + WIDTH / 2), this.guiTop, mouseX, mouseY)) {
                            this.selectedLayerIndex = i;
                            this.selectedLayer = tab;
                            break;
                        }
                        i++;
                    }
                }
            }
            this.clicked = false;
        }
        if (button == 1) {
            if (this.rightClicked) {
                if ((this.rightClickPos != null && this.rightClickPos.distanceTo(new Vec3(mouseX, mouseY, 0)) < 5)) {
                    switchNodeDisplay();
                }
            }

            this.rightClicked = false;
        }

        if(this.selectedLayer.isMouseOverScreen(mouseX, mouseY)) {
            this.selectedLayer.mouseReleased(mouseX - guiLeft, mouseY - guiTop, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    public void update() {
        int i = 0;
        for(var layer : BodyPartType.Layer.values()) {
            this.displayLayers[i] = new PlayerSyntheticDisplayScreen(this, layer, i);
            i++;
        }
        this.selectedLayer = this.displayLayers[selectedLayerIndex];
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 30, 4210752, false);

    }
}
