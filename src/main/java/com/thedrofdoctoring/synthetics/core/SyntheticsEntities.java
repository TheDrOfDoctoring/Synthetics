package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.entities.OrganDisplayMob;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SyntheticsEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Synthetics.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<OrganDisplayMob>> ORGAN_DISPLAY_MOB = prepareEntityType("organ_display", () -> EntityType.Builder.of(OrganDisplayMob::new, MobCategory.MONSTER).sized(0.6F, 1.95F), true);

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
        bus.addListener(SyntheticsEntities::onRegisterEntityTypeAttributes);
    }

    public static void onRegisterEntityTypeAttributes(@NotNull EntityAttributeCreationEvent event) {
        event.put(ORGAN_DISPLAY_MOB.get(), Zombie.createAttributes().build());
    }
    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> prepareEntityType(String id, @NotNull Supplier<EntityType.Builder<T>> builder, boolean spawnable) {
        return ENTITY_TYPES.register(id, () -> {
            EntityType.Builder<T> type = builder.get().setTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true);
            if (!spawnable) {
                type.noSummon();
            }
            return type.build(Synthetics.MODID + ":" + id);
        });
    }

}
