package com.thedrofdoctoring.synthetics.client.core.blocks;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.PermeableLinkableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class PermeableBlockModelLoader implements IGeometryLoader<PermeableBlockModelLoader.PermeableBlockGeometry> {

    public static final PermeableBlockModelLoader INSTANCE = new PermeableBlockModelLoader();
    public static final ResourceLocation ID = Synthetics.rl("permeable_linked_block");

    @Override
    public @NotNull PermeableBlockGeometry read(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext context) throws JsonParseException {
        jsonObject.remove("loader");
        BlockModel base = context.deserialize(jsonObject, BlockModel.class);
        return new PermeableBlockGeometry(base);
    }


    public record PermeableBlockGeometry(BlockModel base) implements IUnbakedGeometry<PermeableBlockGeometry> {

        @Override
        public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context,
                                        @NotNull ModelBaker modelBaker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter,
                                        @NotNull ModelState modelState, @NotNull ItemOverrides itemOverrides) {
            TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
            return new PermeableBlackModel(base.bake(modelBaker, base, spriteGetter, modelState, context.useAmbientOcclusion()), context.useAmbientOcclusion(),
                    context.isGui3d(),
                    context.useBlockLight(), particle, itemOverrides);
        }

        @Override
        public void resolveParents(@NotNull Function<ResourceLocation, UnbakedModel> modelGetter, @NotNull IGeometryBakingContext context) {
            base.resolveParents(modelGetter);
        }
    }

    public record PermeableBlackModel(BakedModel base, boolean isAmbientOcclusion, boolean isGui3d,
                                      boolean usesBlockLight, TextureAtlasSprite particle,
                                      ItemOverrides overrides) implements IDynamicBakedModel {


        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, @NotNull RandomSource randomSource,
                                                 @NotNull ModelData modelData, @Nullable RenderType renderType) {
            if (modelData.has(PermeableLinkableBlockEntity.MODEL_STATE)) {
                BlockState state = modelData.get(PermeableLinkableBlockEntity.MODEL_STATE);
                if (state != null) {
                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
                    return model.getQuads(state, direction, randomSource, modelData, renderType);
                }
            }

            return base.getQuads(blockState, direction, randomSource, modelData, renderType);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.isAmbientOcclusion;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public @NotNull TextureAtlasSprite getParticleIcon() {
            return this.particle;
        }

        @Override
        public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
            if (data.has(PermeableLinkableBlockEntity.MODEL_STATE)) {
                BlockState state = data.get(PermeableLinkableBlockEntity.MODEL_STATE);
                if (state != null) {
                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
                    return model.getParticleIcon(data);
                }
            }
            return base.getParticleIcon(data);
        }


        @Override
        public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            if (data.has(PermeableLinkableBlockEntity.MODEL_STATE)) {
                BlockState other = data.get(PermeableLinkableBlockEntity.MODEL_STATE);
                if (other != null) {
                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
                    return model.getRenderTypes(other, rand, data);
                }
            }
            return base.getRenderTypes(state, rand, data);
        }

        @Override
        public @NotNull ItemOverrides getOverrides() {
            return overrides;
        }
    }


}
