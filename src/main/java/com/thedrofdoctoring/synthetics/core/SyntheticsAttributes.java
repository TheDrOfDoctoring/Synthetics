package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class SyntheticsAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Synthetics.MODID);

    public static final DeferredHolder<Attribute, RangedAttribute> TRADE_PRICES_MULTIPLIER = ATTRIBUTES.register("trading_prices_multiplier", () -> new RangedAttribute("synthetics.trade_prices_multiplier", 1.0D, 0.0D, 1000D));

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
        bus.addListener(SyntheticsAttributes::onModifyEntityTypeAttributes);
    }

    private static void onModifyEntityTypeAttributes(@NotNull EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, TRADE_PRICES_MULTIPLIER);
    }
}
