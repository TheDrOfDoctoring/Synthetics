package com.thedrofdoctoring.synthetics.client.core.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SyntheticItemModelLoader {


    public static class SyntheticGeometryLoader implements IGeometryLoader<SyntheticGeometry> {
        public static final SyntheticGeometryLoader INSTANCE = new SyntheticGeometryLoader();
        public static final ResourceLocation ID = Synthetics.rl("synthetic_item_model");

        private SyntheticGeometryLoader() {}

        @Override
        public @NotNull SyntheticGeometry read(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
            jsonObject.remove("loader");
            BlockModel base = context.deserialize(jsonObject, BlockModel.class);

            return new SyntheticGeometry(base);
        }
    }

    public static class SyntheticGeometry implements IUnbakedGeometry<SyntheticGeometry> {
        private final BlockModel base;

        public SyntheticGeometry(BlockModel base) {
            this.base = base;
        }

        @Override
        public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelState, @NotNull ItemOverrides overrides) {

            TextureAtlasSprite particle = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()));

            InstallableOverrideHandler installable = new InstallableOverrideHandler(context, modelState);
            return CompositeModel.Baked.builder(context, particle, installable, context.getTransforms())
                    .build();
        }


        @Override
        public void resolveParents(@NotNull Function<ResourceLocation, UnbakedModel> modelGetter, @NotNull IGeometryBakingContext context) {
            base.resolveParents(modelGetter);
        }
    }

    private static final class InstallableOverrideHandler extends ItemOverrides {

        private final Map<ResourceLocation, BakedModel> cache = new ConcurrentHashMap<>();

        private final IGeometryBakingContext owner;
        private final ModelState state;

        private InstallableOverrideHandler(IGeometryBakingContext owner, ModelState state) {
            this.owner = owner;
            this.state = state;
        }

        @Override
        public BakedModel resolve(@NotNull BakedModel originalModel, @NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            if(stack.getItem() instanceof InstallableItem<?> item) {
                ResourceLocation texture = item.getInstallableComponent(stack).itemTexture();

                return cache.computeIfAbsent(texture, this::bakeDynamic);
            }
            return originalModel;
        }
        private BakedModel bakeDynamic(ResourceLocation installable) {
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, installable);
            return bakeInternal(owner, p -> material.sprite(), material, this, state);
        }

    }



    private static BakedModel bakeInternal(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, Material installable, ItemOverrides overrides, ModelState state) {
        TextureAtlasSprite particle = spriteGetter.apply(installable);

        RenderTypeGroup normalRenderTypes = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(owner, particle, overrides, owner.getTransforms());

        List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, particle, null);
        List<BakedQuad> quads = UnbakedGeometryHelper.bakeElements(unbaked, ($) -> particle, state);
        ResourceLocation renderTypeName = owner.getRenderTypeHint();
        RenderTypeGroup renderTypes = renderTypeName != null ? owner.getRenderType(renderTypeName) : null;
        builder.addQuads(renderTypes != null ? renderTypes : normalRenderTypes, quads);
        return builder.build();
    }


    public static RenderTypeGroup getDefaultRenderType(IGeometryBakingContext context) {
        ResourceLocation renderTypeHint = context.getRenderTypeHint();
        if (renderTypeHint != null) {
            return context.getRenderType(renderTypeHint);
        } else {
            return new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        }
    }

}
