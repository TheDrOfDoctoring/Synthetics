package com.thedrofdoctoring.synthetics.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import com.thedrofdoctoring.synthetics.capabilities.linkable.BlockLinkingPlayer;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundLinkedInteractPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LinkedInteractScreen extends Screen {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 256;
    private static final int BLOCKS_PER_PAGE = 6;



    private static final ResourceLocation WINDOW = Synthetics.rl("textures/gui/remote_interaction/background.png");


    private int guiLeft;
    private int guiTop;
    private int currentPage = 0;
    private final int maxPages;
    private final @NotNull Player player;
    private final Int2ObjectMap<List<LinkableButton>> linkedForPage;
    private List<LinkableButton> viewedLinkables;



    public LinkedInteractScreen() {
        super(Component.translatable("screens.synthetics.linked_interact_screen"));
        player = Objects.requireNonNull(Minecraft.getInstance().player);
        List<LinkableButton> allLinked = BlockLinkingPlayer.get(player).getLinkedPositionsInDimension(player.level().dimension())
                .stream()
                .flatMap(pos -> Optional.ofNullable(player.level().getBlockEntity(pos)).stream())
                .filter(LinkableBlockEntity.class::isInstance)
                .map(LinkableBlockEntity.class::cast)
                .map(LinkableButton::from)
                .toList();
        this.linkedForPage = IntStream.range(0, allLinked.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> Math.floorDiv(i, BLOCKS_PER_PAGE),
                        Int2ObjectArrayMap::new,
                        Collectors.mapping(i -> {
                            LinkableButton b = allLinked.get(i);
                            b.setYOffset(i % BLOCKS_PER_PAGE);
                            return b;
                            }, Collectors.toList())
                ));
        this.maxPages = linkedForPage.size();
        this.viewedLinkables = linkedForPage.getOrDefault(0, List.of());
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;
        if (this.maxPages > 1) {
            addRenderableWidget(Button.builder(Component.literal("<"), b -> setCurrentPage(Math.max(0, currentPage - 1)))
                    .pos(guiLeft,  guiTop - 22)
                    .size(20, 20)
                    .build());
            addRenderableWidget(Button.builder(Component.literal(">"), b -> setCurrentPage(Math.min(maxPages - 1, currentPage + 1)))
                    .pos(guiLeft + WIDTH - 20, guiTop - 22)
                    .size(20, 20)
                    .build());
        }
    }

    private void setCurrentPage(int page) {
        this.currentPage = page;
        this.viewedLinkables = linkedForPage.getOrDefault(currentPage, List.of());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(LinkableButton linkable : viewedLinkables) {
            if(linkable.onClick(mouseX - guiLeft, mouseY - guiTop, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderWindow(guiGraphics, guiLeft, guiTop);
        this.renderLinkables(guiGraphics, guiLeft, guiTop, mouseX, mouseY);

        for(Renderable renderable : this.renderables) {
            if(renderable instanceof Button button) {
                button.setFocused(false);
            }
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public void renderWindow(@NotNull GuiGraphics guiGraphics, int x, int y) {
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        RenderSystem.enableBlend();

        if (this.maxPages > 1) {
            Component page = Component.literal(String.format("%d / %d", this.currentPage + 1, this.maxPages));
            guiGraphics.pose().pushPose();
            guiGraphics.drawString(font, page.getVisualOrderText(), x + (WIDTH / 2) - (font.width(page) / 2), y - 17, -1);
            guiGraphics.pose().popPose();
        }

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(WINDOW, x, y, 0, 0, WIDTH, HEIGHT);

        RenderSystem.disableBlend();
        stack.popPose();
    }

    public void renderLinkables(@NotNull GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        List<LinkableButton> buttons = viewedLinkables;
        for(int i = 0; i < buttons.size(); i++) {
            buttons.get(i).render(graphics, x, y, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    private static class LinkableButton {

        private final LinkableBlockEntity linkable;
        private final Component title;
        private final ItemStack asItem;
        private final Button button;

        private int yOffset;

        public LinkableButton(LinkableBlockEntity linkable, Component title, ItemStack asItem) {
            this.linkable = linkable;
            this.title = title;
            this.asItem = asItem;
            this.button = Button.builder(Component.translatable("text.synthetics.linkable.interact"), (b) -> this.onInteract())
                    .size(60, 20)
                    .build();
        }


        // 42 = ( HEIGHT - 4 ) / 6
        private static final int INTERACT_OFFSET = 30;
        private static final int WIDGET_HEIGHT = 42;
        private static final int WIDGET_WIDTH = 169;
        private static final ResourceLocation WIDGET = Synthetics.rl("textures/gui/remote_interaction/linked_block_button.png");

        public void setYOffset(int index) {
            this.yOffset = 4 + (index * 41);
        }

        public void render(@NotNull GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
            PoseStack stack = guiGraphics.pose();
            stack.pushPose();
            stack.translate(x + 4, y + yOffset, 250);
            guiGraphics.blit(WIDGET, 0, 0, 0, 0, WIDGET_WIDTH, WIDGET_HEIGHT);
            Font font = Minecraft.getInstance().font;
            BlockPos blockPos = this.linkable.getBlockPos();
            Component pos = Component.literal(String.format("x:%d, y:%d, z:%d", blockPos.getX(), blockPos.getY(), blockPos.getZ())).withStyle(ChatFormatting.BLACK);
            float titleWidth = (float) -font.width(title) / 2;
            float posWidth = (float) -font.width(pos) / 2;
            guiGraphics.drawString(font, title, (int) (titleWidth + 95), 4, 0xFFFFFFFF, true);
            guiGraphics.renderFakeItem(asItem, 9, 13);

            stack.pushPose();
            stack.scale(0.65f, 0.75f, 1f);
            guiGraphics.drawString(font, pos, (int) (posWidth + 126 / 0.65), (int) (22 / 0.75f), 0xFFFFFFFF, false);
            stack.popPose();

            stack.pushPose();
            stack.translate(INTERACT_OFFSET, 16, 0);
            mouseX = mouseX - x - 4 - INTERACT_OFFSET;
            mouseY = mouseY - y - yOffset - 16;
            button.render(guiGraphics, mouseX, mouseY, 1);
            stack.popPose();

            stack.popPose();
        }

        public boolean onClick(double mouseX, double mouseY, int action) {
            return button.mouseClicked(mouseX - 4 - INTERACT_OFFSET, mouseY - yOffset - 16, action);
        }

        private void onInteract() {
            if(Minecraft.getInstance().getConnection() != null) {
                Minecraft.getInstance().getConnection().send(new ServerboundLinkedInteractPacket(linkable.getBlockPos()));
            }
        }

        public static LinkableButton from(LinkableBlockEntity linkable) {
            Component customTitle = linkable.components().get(DataComponents.CUSTOM_NAME);
            MutableComponent title =  customTitle != null ?
                    MutableComponent.create(customTitle.getContents()) :
                    linkable.getBlockState().getBlock().getName();
            return new LinkableButton(linkable, title.withStyle(ChatFormatting.UNDERLINE), new ItemStack(linkable.getBlockState().getBlock()));
        }

    }
}
