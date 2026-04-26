package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.entities.FlameProjectileEntity;
import com.thedrofdoctoring.synthetics.entities.HarpoonProjectileEntity;
import com.thedrofdoctoring.synthetics.entities.OrganDisplayMob;
import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import com.thedrofdoctoring.synthetics.entities.linkable.DummyCameraEntity;
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

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SyntheticsEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Synthetics.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<OrganDisplayMob>> ORGAN_DISPLAY_MOB = prepareEntityType("organ_display", () -> EntityType.Builder.of(OrganDisplayMob::new, MobCategory.MONSTER).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<FlameProjectileEntity>> FLAME_PROJECTILE = prepareEntityType("flame_projectile", () -> EntityType.Builder.<FlameProjectileEntity>of(FlameProjectileEntity::new, MobCategory.MISC)
            .sized(0.3F, 0.3F)
            .fireImmune(), false);
    public static final DeferredHolder<EntityType<?>, EntityType<HarpoonProjectileEntity>> HARPOON_PROJECTILE = prepareEntityType("harpoon_projectile", () -> EntityType.Builder.<HarpoonProjectileEntity>of(HarpoonProjectileEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .fireImmune(), false);
    public static final DeferredHolder<EntityType<?>, EntityType<DummyCameraEntity>> DUMMY_CAMERA_ENTITY = ENTITY_TYPES.register("dummy_camera_entity", () -> EntityType.Builder.of(DummyCameraEntity::new, MobCategory.MISC)
            .sized(0.01F, 0.01F)
            .clientTrackingRange(32)
            .setTrackingRange(32)
            .canSpawnFarFromPlayer()
            .updateInterval(1)
            .fireImmune()
            .noSummon()
            .build(Synthetics.MODID + ":" + "dummy_camera_entity")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DroneEntity>> DRONE_ENTITY = prepareEntityType("drone", () -> EntityType.Builder.of(DroneEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 0.35f), true);


    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
        bus.addListener(SyntheticsEntities::onRegisterEntityTypeAttributes);
    }

    public static void onRegisterEntityTypeAttributes(@NotNull EntityAttributeCreationEvent event) {
        event.put(ORGAN_DISPLAY_MOB.get(), Zombie.createAttributes().build());
        event.put(DRONE_ENTITY.get(),      DroneEntity.createAttributes().build());
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

    public static @NotNull Set<EntityType<?>> getAllEntities() {
        return ENTITY_TYPES.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toSet());
    }

}
