package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.entities.AugmentationChamberBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.entities.OrganSkullBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class SyntheticsBlockEntities {


    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Synthetics.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AugmentationChamberBlockEntity>> AUGMENTATION_CHAMBER = BLOCK_ENTITY_TYPES.register("augmentation_chamber", () -> create(AugmentationChamberBlockEntity::new, SyntheticsBlocks.AUGMENTATION_CHAMBER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OrganSkullBlockEntity>> ORGAN_SKULL = BLOCK_ENTITY_TYPES.register("organ_skull", () -> create(OrganSkullBlockEntity::new, SyntheticsBlocks.ORGAN_SKULL.get()));

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> @NotNull BlockEntityType<T> create(BlockEntityType.@NotNull BlockEntitySupplier<T> factoryIn, Block... blocks) {
        return BlockEntityType.Builder.of(factoryIn, blocks).build(null);
    }
    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
