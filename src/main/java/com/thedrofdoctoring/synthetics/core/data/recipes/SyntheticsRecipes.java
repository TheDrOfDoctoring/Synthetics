package com.thedrofdoctoring.synthetics.core.data.recipes;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SyntheticsRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Synthetics.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Synthetics.MODID);

    public static final Supplier<RecipeType<SyntheticForgeRecipe>> SYNTHETIC_FORGE_RECIPE =
            RECIPE_TYPES.register(
                    "synthetic_forge",
                    () -> RecipeType.simple(Synthetics.rl("synthetic_forge"))
            );
    public static final Supplier<RecipeSerializer<SyntheticForgeRecipe>> SYNTHETIC_FORGE_SERIALISER =
            RECIPE_SERIALIZERS.register("synthetic_forge", SyntheticForgeRecipe.Serializer::new);



    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
