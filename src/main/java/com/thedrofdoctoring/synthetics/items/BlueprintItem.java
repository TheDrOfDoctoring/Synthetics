package com.thedrofdoctoring.synthetics.items;

import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class BlueprintItem extends Item {
    public BlueprintItem(Properties properties) {
        super(properties);
    }

    public Optional<ResearchNode> getResearch(ItemStack stack) {
        Holder<ResearchNode> research = stack.get(SyntheticsDataComponents.BLUEPRINT_RESEARCH);
        if(research == null) {
            return Optional.empty();
        }
        return Optional.of(research.value());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if(getResearch(stack).isPresent()) {
            ResearchNode node = getResearch(stack).get();
            Component component = Component.translatable("text.synthetics.blueprint_research", node.title()).withStyle(ChatFormatting.AQUA);
            tooltipComponents.add(component);
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
