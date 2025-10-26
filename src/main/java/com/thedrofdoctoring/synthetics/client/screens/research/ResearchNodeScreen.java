package com.thedrofdoctoring.synthetics.client.screens.research;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.capabilities.ResearchManager;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@SuppressWarnings({"unused", "FieldCanBeLocal"})

public class ResearchNodeScreen {
    private static final ResourceLocation RESEARCH_BACKGROUND_SPRITE = Synthetics.rl("textures/gui/research/node.png");
    private static final ResourceLocation RESEARCH_DESCRIPTION_SPRITE = Synthetics.rl("generic/description");

    private static final ResourceLocation TITLE_BLUE_SPRITE = Synthetics.rl("research/title_blue");
    private static final ResourceLocation TITLE_GREEN_SPRITE = Synthetics.rl("research/title_green");
    private static final ResourceLocation TITLE_RED_SPRITE = Synthetics.rl("generic/title_red");

    private static final ResourceLocation RIGHT_CLICK_SPRITE = Synthetics.rl("icons/right_click");


    private final ResearchNode node;
    private final ResearchManager manager;
    private final ResearchTabScreen screen;
    public boolean displayRequirements;

    private final List<ResearchNodeScreen> children = new ArrayList<>();
    private final int x;
    private final int y;
    private final Minecraft minecraft;
    private final FormattedCharSequence title;
    private final List<FormattedCharSequence> description;
    private List<FormattedCharSequence> parentRequirement;
    private FormattedCharSequence experienceCost;
    private final ResearchNodeScreen parent;

    private final Either<Ingredient, IBodyInstallable<?>> unlockedPrimary;

    private final ItemStack experience = new ItemStack(Items.EXPERIENCE_BOTTLE);
    private final List<Pair<ItemStack, Integer>> requirements = new ArrayList<>();

    private static final int WIDTH = 26;
    private static final int HEIGHT = 26;


    private final int size;



    public ResearchNodeScreen(ResearchNodeScreen parent, ResearchNode node, ResearchTabScreen screen, int x, int y, ResearchManager manager) {
        this.node = node;
        this.manager = manager;
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.minecraft = Minecraft.getInstance();
        this.parent = parent;
        List<ResearchNode> nodes = SyntheticsClient.getInstance().getManager().parentToChildrenMap.get(node);
        this.unlockedPrimary = this.node.unlocked().getDisplayed();
        if(nodes != null) {
            for(ResearchNode child : nodes) {
                this.children.add(new ResearchNodeScreen(this, child, screen, child.x(), child.y(), manager));
            }
        }
        this.experienceCost = null;
        if(this.node.requirements().experienceCost() > 0) {
            this.experienceCost = Language.getInstance().getVisualOrder(minecraft.font.substrByWidth(Component.translatable("research.synthetics.experience_required", this.node.requirements().experienceCost()), 163));
        }
        if(this.node.requirements().requiredItems().isPresent()) {
            List<Pair<Ingredient, Integer>> itemRequirements = this.node.requirements().requiredItems().get();
            for(Pair<Ingredient, Integer> pair : itemRequirements) {
                ItemStack stack = pair.getFirst().getItems()[0];
                int count = pair.getSecond();
                this.requirements.add(new Pair<>(stack, count));

            }
        }
        this.title = Language.getInstance().getVisualOrder(minecraft.font.substrByWidth(ComponentUtils.mergeStyles(Optional.of(node.title()).orElse(Component.empty()).copy(), Style.EMPTY.withColor(ChatFormatting.AQUA)), 163));
        this.size = Math.max(56 + minecraft.font.width(title), 120);
        this.description = Language.getInstance().getVisualOrder(Helper.findOptimalLines(minecraft, ComponentUtils.mergeStyles(Optional.of(node.description()).orElse(Component.empty()).copy(), Style.EMPTY.withColor(ChatFormatting.GRAY)), size - 30, TEST_SPLIT_OFFSETS));
        this.parentRequirement = null;
        if(this.parent != null) {
            this.parentRequirement = Language.getInstance().getVisualOrder(Helper.findOptimalLines(minecraft, ComponentUtils.mergeStyles(Component.translatable("text.synthetics.research.unlock_parent_first", this.parent.node.title()), Style.EMPTY.withColor(ChatFormatting.DARK_RED)), size - 30, TEST_SPLIT_OFFSETS));
        }

    }

    public List<ResearchNodeScreen> getChildren() {
        return children;
    }

    @Nullable
    public ResearchNode getSelectedNode(double mouseX, double mouseY, int scrollX, int scrollY) {
        if (!isMouseOver(mouseX, mouseY, scrollX, scrollY)) return null;
        return node;
    }
    @Nullable
    public ResearchNodeScreen getSelectedNodeScreen(double mouseX, double mouseY, int scrollX, int scrollY) {
        if (!isMouseOver(mouseX, mouseY, scrollX, scrollY)) return null;
        return this;
    }

    public void draw(@NotNull GuiGraphics graphics, int i, int j) {
        graphics.setColor(1, 1, 1, 1);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        ResearchNodeState state = getState();
        if (state == ResearchNodeState.HIDDEN) return;

        pose.translate(0f, ResearchScreen.SCREEN_HEIGHT, 80);

        int x = this.x;
        int y = this.y;


        if (state == ResearchNodeState.LOCKED || !this.manager.hasResearched(node)) {
            graphics.setColor(0.5f, 0.5f, 0.35f, 1f);
        } else {
            graphics.setColor(1, 1, 1, 1);
        }
        graphics.blit(RESEARCH_BACKGROUND_SPRITE, x, y, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

        RenderSystem.enableBlend();
        drawIcon(graphics, x + 5, y + 5);

        pose.popPose();
        graphics.setColor(1, 1, 1, 1);
        for (ResearchNodeScreen child : this.children) {
            child.draw(graphics, i, j);
        }
    }

    public void renderConnections(@NotNull GuiGraphics graphics, int startX, int startY, boolean outerLine) {


        ResearchNodeState state = getState();
        if (state == ResearchNodeState.HIDDEN) return;

        if (this.parent != null) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            int colour = state.pathColor(outerLine);

            pose.translate(0, ResearchScreen.SCREEN_HEIGHT, 0);
            if (state == ResearchNodeState.UNLOCKED) {
                pose.translate(0, 0, 10);
            }

            int xParentToChild = this.parent.x - x;
            int yParentToChild = this.parent.y - y;
            // Straight horizontal line
            if(yParentToChild == 0 && xParentToChild != 0) {
                int fromX = this.parent.x;
                int toX = this.x;
                if(xParentToChild < 0) {
                    // Parent is to the left of child;
                    fromX += 25;
                } else {
                    toX += 25;
                }

                if(outerLine) {
                    graphics.hLine(fromX, toX, this.parent.y + 1 + 13,colour);
                    graphics.hLine(fromX, toX, this.parent.y - 1 + 13,colour);
                } else {
                    graphics.hLine(fromX, toX, this.parent.y + 13,colour);
                }
            }
            // Straight vertical line
            if(xParentToChild == 0 && yParentToChild != 0) {
                int fromY = this.parent.y;
                int toY = this.y;
                if(yParentToChild < 0) {
                    // Parent is above child
                    fromY += 24;
                    toY += 1;
                } else {
                    toY += 24;
                    fromY += 1;
                }

                if(outerLine) {
                    graphics.vLine(this.parent.x + 13 + 1, fromY, toY, colour);
                    graphics.vLine(this.parent.x + 13 - 1, fromY, toY, colour);
                } else {
                    graphics.vLine(this.parent.x + 13, fromY, toY, colour);
                }
            }
            if(xParentToChild != 0 && yParentToChild != 0) {
                int fromY = this.parent.y;
                int toY = this.y;
                int fromX = this.parent.x;
                int toX = this.x;
                int fromToY;

                if(yParentToChild < 0) {
                    // Parent is above child
                    fromY += 24;
                    toY += 1;

                } else {
                    toY += 24;
                    fromY += 1;

                }
                fromToY = (toY - fromY) / 2;
                if(xParentToChild < 0) {
                    // Parent is to the left of child;
                    fromX += 13;
                    toX += 13;
                } else {
                    toX += 13;
                    fromX += 13;
                }

                if(outerLine) {
                    graphics.vLine(this.parent.x + 13 + 1, fromY, fromY + fromToY, colour);
                    graphics.vLine(this.parent.x + 13 - 1, fromY, fromY + fromToY, colour);
                    graphics.hLine(fromX, toX, fromY + fromToY + 1,colour);
                    graphics.hLine(fromX, toX, fromY + fromToY - 1,colour);
                    graphics.vLine(toX + 1, fromY + fromToY, toY, colour);
                    graphics.vLine(toX - 1, fromY + fromToY, toY, colour);
                } else {
                    graphics.vLine(this.parent.x + 13, fromY, fromY + fromToY, colour);
                    graphics.hLine(fromX, toX, fromY + fromToY,colour);
                    graphics.vLine(toX, fromY + fromToY, toY, colour);
                }
            }


            pose.popPose();
        }

        for (ResearchNodeScreen child : this.children) {
            child.renderConnections(graphics, startX, startY, outerLine);
        }


    }

    @SuppressWarnings("ConstantConditions")
    public void renderHover(@NotNull GuiGraphics graphics, double mouseX, double mouseY, float fade, int scrollX, int scrollY) {
        ResearchNodeState state = getState();
        if (state == ResearchNodeState.HIDDEN) return;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0f, ResearchScreen.SCREEN_HEIGHT, 450);
        int x = this.x;
        int y = this.y;

        if (state == ResearchNodeState.LOCKED || state == ResearchNodeState.VISIBLE) {

            if (state == ResearchNodeState.VISIBLE && this.parentRequirement != null) {
                int offset = parentRequirement.size();
                graphics.blitSprite(RESEARCH_DESCRIPTION_SPRITE, scrollX + x - 5, scrollY + y - 3 - offset * 9, this.size, 30 + parentRequirement.size() * 9);
                for (int i = 0; i < offset; i++) {
                    graphics.drawString(this.minecraft.font, parentRequirement.get(offset - 1 - i), scrollX + x + 2, scrollY + this.y - 8 - i * 9, -1, true);
                }
            }
        }

        List<FormattedCharSequence> description = this.description;


        //draw description
        if (!description.isEmpty() && !displayRequirements) {
            graphics.blitSprite(RESEARCH_DESCRIPTION_SPRITE, scrollX + x - 5, scrollY + y + 3, this.size, 30 + description.size() * 9);
            for (int i = 0; i < description.size(); i++) {
                graphics.drawString(this.minecraft.font, description.get(i), scrollX + x + 2, scrollY + this.y + 3 + 24 + i * 9, -1, true);
            }
        }

        //draw title
        ResourceLocation nodeTitleTexture = state.sprite;
        if (state == ResearchNodeState.UNLOCKED && !this.manager.hasResearched(this.node)) {
            nodeTitleTexture = ResearchNodeState.LOCKED.sprite;
        }
        int titleLength = this.size;

        if(displayRequirements) {
            titleLength = drawResearchRequirements(graphics, scrollX, scrollY);
        }
        graphics.drawString(this.minecraft.font, this.title, scrollX + x + 30, scrollY + y + 9, -1, true);

        graphics.blitSprite(nodeTitleTexture, scrollX + x - 5, scrollY + y+3, titleLength, 20);
        graphics.blitSprite(RIGHT_CLICK_SPRITE, scrollX + x - 18, scrollY + y + 3, 15, 16);

        //draw node
        graphics.setColor(1f, 1f, 1f, 1);
        graphics.blit(RESEARCH_BACKGROUND_SPRITE, scrollX + x, scrollY + y, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        drawIcon(graphics, x + scrollX + 5, y + scrollY + 5);
        pose.popPose();
    }
    public boolean isMouseOver(double mouseX, double mouseY, int scrollX, int scrollY) {
        return mouseX >= x + scrollX && mouseX < x + scrollX + (double) WIDTH - 1 && mouseY > scrollY + y + ResearchScreen.SCREEN_HEIGHT && mouseY < scrollY + this.y + 26 + ResearchScreen.SCREEN_HEIGHT;
    }

    @SuppressWarnings("DataFlowIssue")
    private int drawResearchRequirements(GuiGraphics graphics, int scrollX, int scrollY) {
        int size = 28;
        if(experienceCost != null) {
            graphics.drawString(this.minecraft.font, experienceCost, scrollX + x + 15, scrollY + y + 30, 0x90EE90, true);
            graphics.renderItem(experience, scrollX + x - 3, scrollY + y + 25);
            size = this.minecraft.font.width(this.experienceCost) + 28;
        }
        int j = 0, k = 1;
        if(!this.requirements.isEmpty()) {

            for (Pair<ItemStack, Integer> stackCountPair : requirements) {
                String count = stackCountPair.getSecond() + "x";
                int countWidth = this.minecraft.font.width(count);
                int totalWidth = countWidth + 25;

                if (j + totalWidth > size) {
                    j = 0;
                    k += 1;
                }
                graphics.renderItem(stackCountPair.getFirst(), j + x - 2, scrollY + y + k * 18 + 28);
                graphics.drawString(this.minecraft.font, count, j + x + 18, scrollY + y + k * 18 + 32, ChatFormatting.GRAY.getColor(), true);

                j = j + totalWidth;
            }


        }
        graphics.blitSprite(RESEARCH_DESCRIPTION_SPRITE, scrollX + x - 5, scrollY + y + 3, size, 30 + (k + 1) * 18);
        return size;
    }

    private void drawIcon(GuiGraphics graphics, int x, int y) {
        graphics.pose().pushPose();
        this.unlockedPrimary.ifLeft(ingredient -> {
            ItemStack stack = ingredient.getItems()[0];
            graphics.renderFakeItem(stack, x, y);
        });
        this.unlockedPrimary.ifRight(iBodyInstallable -> graphics.blit(iBodyInstallable.texture(), x, y, 0, 0, 16, 16, 16, 16));
        graphics.pose().popPose();

    }




    private ResearchNodeState getState() {
        if (this.manager.hasResearched(this.node)) {
            return ResearchNodeState.UNLOCKED;
        } else if (this.node.parent().isPresent()) {
            if(manager.hasResearched(this.node.parent().get().value())) {
                return ResearchNodeState.AVAILABLE;
            }
        } else {
            return ResearchNodeState.AVAILABLE;
        }
        return ResearchNodeState.VISIBLE;
    }
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};


    public void switchDisplay() {
        this.displayRequirements = !displayRequirements;
    }



    enum ResearchNodeState {
        /**
         * Rendered and unlockable
         */
        AVAILABLE(TITLE_BLUE_SPRITE, 0xffa7a7a7, -1),
        /**
         * Not rendered
         */
        HIDDEN(null, 0, 0),
        /**
         * Rendered and unlocked
         */
        UNLOCKED(TITLE_GREEN_SPRITE, 0xff008711, 0xff005304),
        /**
         * Rendered but not unlockable
         */
        VISIBLE(TITLE_BLUE_SPRITE, 0xff222222, 0xff3f3f3f),

        LOCKED(TITLE_RED_SPRITE, 0xffcf0000, 0xff6a0000);


        public final ResourceLocation sprite;
        /**
         * connectivity inner color
         */
        public final int innerColor;
        /**
         * connectivity outer color
         */
        public final int outerColor;

        ResearchNodeState(ResourceLocation sprite, int innerColor, int outerColor) {
            this.sprite = sprite;
            this.outerColor = outerColor;
            this.innerColor = innerColor;
        }

        int pathColor(boolean outer) {
            return outer ? this.outerColor : this.innerColor;
        }
    }

}
