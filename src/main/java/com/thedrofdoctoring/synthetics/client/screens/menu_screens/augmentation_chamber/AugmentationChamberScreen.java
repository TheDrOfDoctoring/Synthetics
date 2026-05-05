package com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.ComplexityManager;
import com.thedrofdoctoring.synthetics.capabilities.PartManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.config.ClientConfig;
import com.thedrofdoctoring.synthetics.core.SyntheticsSounds;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.*;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundInstallableMenuPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class AugmentationChamberScreen extends AbstractContainerScreen<AugmentationChamberMenu> {

    private static final ResourceLocation BACKGROUND = Synthetics.rl("textures/gui/container/augmentation_chamber.png");
    private static final ResourceLocation DESCRIPTION = Synthetics.rl("generic/description");
    private static final ResourceLocation RIGHT_CLICK_SPRITE = Synthetics.rl("icons/right_click");
    private static final ResourceLocation BODY_PART_NODE = Synthetics.rl("augmentation/node");

    private static final ResourceLocation COMPLEXITY_GEAR_GRADIENT = Synthetics.rl("augmentation/gear_gradient");
    private static final ResourceLocation COMPLEXITY_GEAR_OUTLINE = Synthetics.rl("augmentation/gear_outline");

    private static final int INSTALL_WIDTH = 70;
    private static final int INSTALL_HEIGHT = 14;
    private static final int GEAR_WIDTH = 16;
    private static final int GEAR_HEIGHT = 16;

    public static final int WIDTH = 176;
    public static final int HEIGHT = 194;

    private Vec3 mousePos;
    private Vec3 rightClickPos;
    private boolean clicked;
    private int selectedAbility;
    private boolean tryUpdateSelected;
    private boolean displayAbilities;
    private final @NotNull Player player;
    private final @NotNull SyntheticsPlayer synthetics;
    private final PlayerSyntheticDisplayScreen[] displayLayers = new PlayerSyntheticDisplayScreen[BodyPartType.Layer.values().length];
    private PlayerSyntheticDisplayScreen selectedLayer;
    private BodyPart selectedBodyPart;
    private float complexityPercentage;
    private int selectedLayerIndex;

    public boolean rightClicked;
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

        this.checkUpdate();
    }

    private void checkUpdate() {
        if(this.tryUpdateSelected) {
            ItemStack stack = this.menu.getSlot(0).getItem();
            if(!stack.isEmpty() && stack.getItem() instanceof InstallableItem<?> installableItem) {
                IBodyInstallable<?> installable = installableItem.getInstallableComponent(stack);
                if(installable instanceof Augment augment) {
                    Holder<BodyPartType> firstType = augment.validParts().get(0).value().type();
                    boolean onlyOneType = true;
                    for(Holder<BodyPart> validParts : augment.validParts()) {
                        Holder<BodyPartType> newType = validParts.value().type();
                        if(!newType.equals(firstType)) {
                            onlyOneType = false;
                            break;
                        }
                    }
                    if(onlyOneType) {
                        this.setSelectedBodyPart(synthetics.parts().getPartForType(firstType.value()));
                    }

                }
                this.tryUpdateSelected = false;
            }
        }
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.guiLeft, this.guiTop, 0, 0, this.imageWidth, this.imageHeight);
        float scaleFactor = 0.7f;
        int x = (int) ((this.guiLeft + 50) / scaleFactor);
        int y = (int) ((this.guiTop + 12) / scaleFactor);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scaleFactor, scaleFactor, 1);
        guiGraphics.blitSprite(BODY_PART_NODE,  x, y, 26, 26);
        if(this.selectedBodyPart != null) {
            if(ClientConfig.showComplexityGear.get()) {
                int gearX = x + 30;
                int gearY = y + 5;
                int filledIn = Math.min(GEAR_HEIGHT, (int) (GEAR_HEIGHT * this.complexityPercentage) + 1);
                guiGraphics.blitSprite(COMPLEXITY_GEAR_OUTLINE, gearX, gearY, GEAR_WIDTH, GEAR_HEIGHT);
                guiGraphics.blitSprite(COMPLEXITY_GEAR_GRADIENT, GEAR_WIDTH, GEAR_HEIGHT, 0, GEAR_HEIGHT-filledIn, gearX, gearY + GEAR_HEIGHT - filledIn, GEAR_WIDTH, filledIn);
            }
            guiGraphics.blit(selectedBodyPart.texture(), x + 5, y + 5, 0, 0, 16, 16, 16, 16);
        }

        guiGraphics.pose().popPose();

    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        for(PlayerSyntheticDisplayScreen screen : displayLayers) {
            screen.drawTab(guiGraphics, guiLeft - WIDTH / 2 + 12, this.guiTop + 5, screen == selectedLayer);
            screen.drawIcon(guiGraphics, guiLeft - WIDTH / 2 + 12, this.guiTop + 5);
        }
        guiGraphics.pose().popPose();

        this.selectedLayer.renderHover(guiGraphics, mouseX - guiLeft, mouseY - guiTop);
        if(this.hoveredSlot != null && this.hoveredSlot.index != 0) {
            super.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    protected void renderHover(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if(this.minecraft == null) return;
        int x = guiLeft + 8;
        int y = guiTop + 33;
        if(isMouseOverButton(mouseX, x, mouseY, y)) {
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + INSTALL_WIDTH, y + INSTALL_HEIGHT, 0x66ffffff, 0x77ffffff, 0);
        }
        int gearX = guiLeft + 71;
        int gearY = guiTop + 15;
        if(this.selectedBodyPart != null && isMouseOverGear(mouseX, gearX, mouseY, gearY)) {
            int partMaxComplexity = selectedBodyPart.maxComplexity();
            int complexityOnPart  = synthetics.getComplexityManager().getTotalPartComplexity(selectedBodyPart);
            if(complexityOnPart > 0) {
                guiGraphics.pose().pushPose();
                float scale = 0.7f;
                guiGraphics.pose().scale(scale, scale, 1);
                guiGraphics.pose().translate(0, 0, 450);
                guiGraphics.drawString(minecraft.font, Component.translatable("text.synthetics.augmentation.part_complexity", complexityOnPart, partMaxComplexity).withStyle(ChatFormatting.YELLOW), (int) (mouseX / scale) - 40, (int) (mouseY/ scale) - 6, -1, true);
                guiGraphics.pose().popPose();

            }
        }
        x = guiLeft + 3;
        y = guiTop + 15;
        if(this.hoveredSlot != null && this.hoveredSlot.index == 0) {
            ItemStack stack = this.menu.getInputContainer().getItem(0);
            if(stack.getItem() instanceof InstallableItem<?> item) {
                IBodyInstallable<?> installable = item.getInstallableComponent(stack);
                if(installable instanceof Augment augment && this.selectedBodyPart != null && augment.validParts().stream().anyMatch(p -> p.value().equals(selectedBodyPart))) {
                    installable = new AppliedAugmentInstance(augment, this.selectedBodyPart);
                }
                List<FormattedCharSequence> text = Language.getInstance().getVisualOrder(getTextForInstallable(installable, synthetics, displayAbilities, selectedAbility));

                PoseStack pose = guiGraphics.pose();
                guiGraphics.blitSprite(RIGHT_CLICK_SPRITE, x + 12, y - 3, 15, 16);
                pose.pushPose();

                int largest = text.stream().mapToInt(charSequence -> this.minecraft.font.width(charSequence)).max().orElse(0);

                float xScale = 0.5f;

                int size = Math.max(84, (int) (largest * xScale) + 3);
                float yScale = 0.5f;
                pose.translate(0, 0, 350);
                pose.scale(xScale, yScale, 1f);
                guiGraphics.blitSprite(DESCRIPTION, (int) (x / xScale), (int) ((y + 15) / yScale), (int) (size / xScale), 5 + (text.size() + 1) * 8);
                for(int i = 0; i < text.size(); i++) {
                    guiGraphics.drawString(Objects.requireNonNull(this.minecraft).font, text.get(i), ((x + 2) / xScale), ((y + 17 + (i * 8 * yScale)) / yScale), -1, true);
                }
                pose.popPose();

            }

        }
    }


    public static Component getAbilityTitle(Holder<Ability> ability, boolean isHovered) {
        MutableComponent title = Component.empty();
        if(isHovered) {
            ChatFormatting colour = ability.value().abilityNature() != Ability.AbilityNature.DETRIMENTAL ? ChatFormatting.WHITE : ChatFormatting.RED;
            title.append(ability.value().abilityType().title(ability.value().abilityData())).withStyle(colour).withStyle(ChatFormatting.UNDERLINE);
            return title;
        }
        ChatFormatting colour = ability.value().abilityNature() != Ability.AbilityNature.DETRIMENTAL ? ChatFormatting.GRAY : ChatFormatting.DARK_RED;
        title.append(ability.value().abilityType().title(ability.value().abilityData())).withStyle(colour);
        return title;
    }



    public static List<FormattedText> getTextForInstallable(IBodyInstallable<?> installable, SyntheticsPlayer synthetics, boolean displayAbilities, int selectedAbility) {
        List<FormattedText> text = new ArrayList<>();

        if (synthetics.isInstalled(installable) && !(installable instanceof AppliedAugmentInstance || installable instanceof Augment)) {
            text.add(Component.translatable("text.synthetics.augmentation.already_installed").withStyle(ChatFormatting.AQUA));
        }
        if (displayAbilities && installable.abilities().isPresent()) {
            addAbilityText(installable.abilities().get(), selectedAbility, text);
            return text;
        }
        text.add(Component.translatable("text.synthetics.augmentation.complexity").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.UNDERLINE));
        text.add(Component.empty());
        switch (installable) {
            case AppliedAugmentInstance instance -> addTextForAugmentInst(synthetics, instance, text);
            case Augment augment     -> addTextForAugment(augment, text);
            case BodyPart part       -> addTextForPart(synthetics, part, text);
            case BodySegment segment -> addTextForSegment(synthetics, segment, text);
            default -> {
                return Collections.emptyList();
            }
        }
        return text;
    }

    private static void addAbilityText(HolderSet<Ability> abilitiesSet, int selectedAbility, List<FormattedText> text) {
        List<Holder<Ability>> abilities = abilitiesSet.stream()
                .filter(p -> p.value().abilityNature() != Ability.AbilityNature.HIDDEN)
                .toList();
        int maxSize = abilities.size();
        if (selectedAbility >= maxSize) {
            selectedAbility = 0;
        }
        if (selectedAbility < maxSize) {
            if (selectedAbility > 0) {
                text.add(getAbilityTitle(abilities.get(selectedAbility - 1), false));
            }
            text.add(getAbilityTitle(abilities.get(selectedAbility), true));
            if (selectedAbility < maxSize - 1) {
                text.add(getAbilityTitle(abilities.get(selectedAbility + 1), false));
            }
            text.add(Component.empty());
            Ability ability = abilities.get(selectedAbility).value();

            ArrayList<Component> abilityDescription = new ArrayList<>();
            ability.abilityType().addDescriptionInfo(ability, abilityDescription);
            text.addAll(abilityDescription);
        }
    }

    private static void addTextForSegment(SyntheticsPlayer synthetics, BodySegment segment, List<FormattedText> text) {
        BodySegment currentSegment = synthetics.parts().getSegmentForType(segment.type().value());
        int oldSegmentComplexity = synthetics.getComplexityManager().getTotalSegmentComplexity(currentSegment);
        text.add(Component.translatable("text.synthetics.augmentation.segment_complexity", oldSegmentComplexity, segment.maxComplexity()).withStyle(getColourForMax(oldSegmentComplexity, currentSegment.maxComplexity())));
        text.add(Component.translatable("text.synthetics.augmentation.new_segment_complexity", oldSegmentComplexity, segment.maxComplexity()).withStyle(getColourForMax(oldSegmentComplexity, segment.maxComplexity())));
    }

    private static void addTextForPart(SyntheticsPlayer synthetics, BodyPart part, List<FormattedText> text) {
        BodySegment segment = synthetics.parts().getSegmentForPart(part);
        BodyPart currentPart = synthetics.parts().getPartForType(part.type().value());
        int oldPartComplexity = synthetics.getComplexityManager().getTotalPartComplexity(part);
        int oldSegmentComplexity = synthetics.getComplexityManager().getTotalSegmentComplexity(segment);

        text.add(Component.translatable("text.synthetics.augmentation.old_part_complexity", oldPartComplexity, currentPart.maxComplexity()).withStyle(getColourForMax(oldPartComplexity, currentPart.maxComplexity())));
        text.add(Component.translatable("text.synthetics.augmentation.new_part_complexity", oldPartComplexity, part.maxComplexity()).withStyle(getColourForMax(oldPartComplexity, part.maxComplexity())));
        text.add(Component.translatable("text.synthetics.augmentation.segment_complexity", oldSegmentComplexity, segment.maxComplexity()).withStyle(getColourForMax(oldSegmentComplexity, segment.maxComplexity())));
    }

    private static void addTextForAugment(Augment augment, List<FormattedText> text) {
        text.add(Component.translatable("text.synthetics.augmentation.no_selected_part").withStyle(ChatFormatting.RED));
        text.add(Component.translatable("text.synthetics.augmentation.select_part").withStyle(ChatFormatting.RED));
        text.add(Component.empty());

        text.add(Component.translatable("text.synthetics.augmentation.max_total", augment.maxTotal()).withStyle(ChatFormatting.BLUE));
        text.add(Component.translatable("text.synthetics.augmentation.max_per_part", augment.maxPerPart()).withStyle(ChatFormatting.BLUE));


        text.add(Component.translatable("text.synthetics.augmentation.added_complexity", augment.complexity()).withStyle(ChatFormatting.RED));
        if(augment.powerCost() > 0) {
            text.add(Component.translatable("text.synthetics.augmentation.power_draw", augment.powerCost()).withStyle(ChatFormatting.RED));
        }
    }

    private static void addTextForAugmentInst(SyntheticsPlayer synthetics, AppliedAugmentInstance instance, List<FormattedText> text) {
        BodyPart part = instance.appliedPart();
        BodySegment segment = synthetics.parts().getSegmentForPart(part);
        ComplexityManager.ComplexityPairs newComplexity = synthetics.getComplexityManager().getNewComplexity(new AppliedAugmentInstance(instance.augment(), part), null);

        int maxPartComplexity = part.maxComplexity();
        int maxSegmentComplexity = segment.maxComplexity();

        int installedInstanceCount = synthetics.installedInstanceCount(instance.augment());
        int installedInstanceCountPart = synthetics.installedInstanceCount(instance.augment(), instance.appliedPart());


        text.add(Component.translatable("text.synthetics.augmentation.max_total_fraction", installedInstanceCount, instance.augment().maxTotal()).withStyle(getColourForMax(installedInstanceCountPart, instance.augment().maxTotal())));
        text.add(Component.translatable("text.synthetics.augmentation.max_per_part_fraction", installedInstanceCountPart, instance.augment().maxPerPart()).withStyle(getColourForMax(installedInstanceCountPart, instance.augment().maxPerPart())));

        text.add(Component.translatable("text.synthetics.augmentation.added_complexity", instance.augment().complexity()).withStyle(ChatFormatting.RED));
        text.add(Component.translatable("text.synthetics.augmentation.new_part_complexity", newComplexity.partComplexity(), maxPartComplexity).withStyle(getColourForMax(newComplexity.partComplexity(), maxPartComplexity)));
        text.add(Component.translatable("text.synthetics.augmentation.new_segment_complexity", newComplexity.segmentComplexity(), maxSegmentComplexity).withStyle(getColourForMax(newComplexity.segmentComplexity(), maxSegmentComplexity)));
    }

    private static ChatFormatting getColourForMax(int value, int maxValue) {
        if(value > maxValue) {
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


    @Override
    protected void slotClicked(@NotNull Slot slot, int slotId, int mouseButton, @NotNull ClickType type) {

        if(slotId == 0 && slot.getItem().isEmpty()) {
            tryUpdateSelected = true;
        }

        super.slotClicked(slot, slotId, mouseButton, type);
    }

    private boolean isMouseOverButton(double mouseX, int buttonX, double mouseY, int buttonY) {
        return (mouseX >= buttonX && mouseX < buttonX + INSTALL_WIDTH && mouseY > buttonY && mouseY < buttonY + INSTALL_HEIGHT);
    }

    private boolean isMouseOverGear(double mouseX, int buttonX, double mouseY, int buttonY) {
        return (mouseX >= buttonX && mouseX < buttonX + (GEAR_WIDTH * 0.7) && mouseY > buttonY && mouseY < buttonY + (GEAR_HEIGHT * 0.7));
    }

    private void installButton(double mouseX, double mouseY) {
        if (isMouseOverButton(mouseX, guiLeft + 9, mouseY, guiTop + 33)) {
            ItemStack itemStack = this.menu.getInputContainer().getItem(0);
            if (itemStack.getItem() instanceof InstallableItem<?> item) {
                IBodyInstallable<?> installable = item.getInstallableComponent(itemStack);
                ClientPacketListener connection = Minecraft.getInstance().getConnection();

                if (installable instanceof Augment augment) {
                    if (selectedBodyPart != null) {
                        installable = new AppliedAugmentInstance(augment, selectedBodyPart);
                    }
                }
                SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);
                if (!(installable instanceof Augment) && syntheticsPlayer.canAddInstallable(installable) && connection != null) {
                    if (shouldPlayAlternativeSound(syntheticsPlayer, installable)) {
                        playSoundEffect(SyntheticsSounds.SQUELCH.get(), 0.25f, 1f);
                    } else {
                        playSoundEffect(SoundEvents.BEACON_ACTIVATE, 1f, 2f);
                        playSoundEffect(SoundEvents.SCULK_BLOCK_CHARGE, 1f, 1f);
                    }
                    connection.send(new ServerboundInstallableMenuPacket(Optional.ofNullable(this.selectedBodyPart)));

                } else {
                    playSoundEffect(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5f, 1f);
                }
            }
        }
    }

    private boolean shouldPlayAlternativeSound(SyntheticsPlayer syntheticsPlayer, IBodyInstallable<?> installable) {
        return switch(installable) {
            case BodyPart part ->
                    PartManager.isDefault(syntheticsPlayer.parts().getPartForType(part.type().value()))
                            || PartManager.isDefault(installable);
            case BodySegment segment ->
                    PartManager.isDefault(syntheticsPlayer.parts().getSegmentForType(segment.type().value()))
                            || PartManager.isDefault(installable);
            default -> false;
        };
    }



    @SuppressWarnings("SameParameterValue")
    private void playSoundEffect(@NotNull SoundEvent event, float pitch, float volume) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
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

    public void setSelectedBodyPart(BodyPart selectedBodyPart) {
        this.selectedBodyPart = selectedBodyPart;
        this.complexityPercentage = (float) this.synthetics.getComplexityManager().getTotalPartComplexity(selectedBodyPart) / selectedBodyPart.maxComplexity();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.clicked) {
                if ((this.mousePos != null && this.mousePos.distanceTo(new Vec3(mouseX, mouseY, 0)) < 5)) {
                    installButton(mouseX, mouseY);
                    int i = 0;
                    for (PlayerSyntheticDisplayScreen tab : this.displayLayers) {
                        if (tab != this.selectedLayer && tab.isMouseOver((guiLeft - WIDTH / 2 + 12), this.guiTop, mouseX, mouseY)) {
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
        if(this.selectedBodyPart != null) {
            this.setSelectedBodyPart(this.synthetics.parts().getPartForType(this.selectedBodyPart.type().value()));
        }
        this.selectedLayer = this.displayLayers[selectedLayerIndex];
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 30, 4210752, false);

    }
}
