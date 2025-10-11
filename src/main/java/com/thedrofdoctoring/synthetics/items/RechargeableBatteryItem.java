package com.thedrofdoctoring.synthetics.items;

import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.components.BatteryComponentOptions;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RechargeableBatteryItem extends Item {

    public RechargeableBatteryItem(Properties properties) {
        super(properties);
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        SyntheticsPlayer synthetics = SyntheticsPlayer.get(player);
        ItemStack stack = player.getItemInHand(usedHand);
        if(stack.has(SyntheticsDataComponents.BATTERY_OPTIONS) && synthetics.getPowerManager().getStoredPower() < synthetics.getPowerManager().getMaxPower()) {
            int difference = synthetics.getPowerManager().getMaxPower() - synthetics.getPowerManager().getStoredPower();
            BatteryComponentOptions options = stack.get(SyntheticsDataComponents.BATTERY_OPTIONS);
            IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(cap != null && options != null) {
                int amount = cap.extractEnergy(Math.min(options.maxExtract(), difference), false);
                cap.receiveEnergy(amount - synthetics.getPowerManager().addPower(amount), false);
                synthetics.getPowerManager().markDirty();

            }

        }

        return super.use(level, player, usedHand);
    }


    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {

        if (stack.has(SyntheticsDataComponents.ENERGY_COMPONENT) && stack.has(SyntheticsDataComponents.BATTERY_OPTIONS)) {
            int energy = stack.get(SyntheticsDataComponents.ENERGY_COMPONENT);
            int max = stack.get(SyntheticsDataComponents.BATTERY_OPTIONS.get()).capacity();
            return Math.round( ((float) energy / max) * 13);
        }

        return super.getBarWidth(stack);
    }



    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return Mth.hsvToRgb(0.3333f, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        if (stack.has(SyntheticsDataComponents.ENERGY_COMPONENT) && stack.has(SyntheticsDataComponents.BATTERY_OPTIONS)) {
            tooltipComponents.add(Component.translatable("text.synthetics.battery_storage", stack.get(SyntheticsDataComponents.ENERGY_COMPONENT), Objects.requireNonNull(stack.get(SyntheticsDataComponents.BATTERY_OPTIONS)).capacity()).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public int getDefaultMaxStackSize() {
        return 1;
    }
}
