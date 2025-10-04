package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.SyntheticResearchTable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SyntheticsBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Synthetics.MODID);

    public static final DeferredBlock<SyntheticResearchTable> RESEARCH_TABLE = registerWithItem("research_table", () -> new SyntheticResearchTable(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(0.5f)
            .destroyTime(1f)
            .sound(SoundType.WOOD)
            .noOcclusion()
    ));

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.@NotNull Properties properties) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        SyntheticsItems.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
