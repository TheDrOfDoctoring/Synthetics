package com.thedrofdoctoring.synthetics.client.renderers.world;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import it.unimi.dsi.fastutil.doubles.DoubleObjectPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Significant portions based off the Vampirism blood vision rendering,
 * <a href="https://github.com/TeamLapen/Vampirism/blob/version/1.21/latest/src/main/java/de/teamlapen/vampirism/client/renderer/RenderHandler.java">...</a>
 */
public class EntityHighlightingRenderer implements ResourceManagerReloadListener {
    private static final int FADE_TICKS = 200;


    private final Minecraft mc;
    private final OutlineBuffers highlightBuffers;

    private PostChain blurShader;

    private PostPass blur1, blur2, blit0;
    public boolean isDoingHighlight;
    private int lastHighlightTicks = 0;
    private int highlightTicks = 0;
    private int displayHeight, displayWidth;

    public EntityHighlightingRenderer(Minecraft minecraft) {
        this.mc = minecraft;
        this.highlightBuffers = new OutlineBuffers();
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        this.rebuildShaders();
    }

    @SuppressWarnings("unused")
    public void onClientTick(ClientTickEvent.Pre event) {
        if (mc.level == null || mc.player == null || !mc.player.isAlive()) return;
        lastHighlightTicks = highlightTicks;
        if(shouldRenderHighlighted(mc.player)) {
            if(highlightTicks < FADE_TICKS) {
                highlightTicks++;
            }

        } else {
            highlightTicks = 0;
        }
    }


    public void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (mc.player == null) return;
        if (!isDoingHighlight && shouldRenderHighlighted(mc.player)) {
            if (!isEntityHighlighted(mc.player, event.getEntity())) return;
            float progress = this.getProgress(event.getPartialTick());
            int r = 10 + (int) (40  * progress);
            int g = 10 + (int) (140 * progress);
            int b = 10 + (int) (240 * progress);
            int a = 255;
            LivingEntity entity = event.getEntity();

            EntityRenderDispatcher renderManager = mc.getEntityRenderDispatcher();
            var highlightBuffers = this.highlightBuffers.getBuffer(event.getMultiBufferSource());

            highlightBuffers.setColor(r, g, b, a);
            float f = Mth.lerp(event.getPartialTick(), entity.yRotO, entity.getYRot());
            isDoingHighlight = true;


            EntityRenderer<? super Entity> entityrenderer = renderManager.getRenderer(entity);
            boolean isInvisible = entity.isInvisible();
            entity.setInvisible(false);
            boolean hideGui = Minecraft.getInstance().options.hideGui;
            Minecraft.getInstance().options.hideGui = true;
            entityrenderer.render(entity, f, event.getPartialTick(), event.getPoseStack(), highlightBuffers, event.getPackedLight());

            entity.setInvisible(isInvisible);
            Minecraft.getInstance().options.hideGui = hideGui;
            mc.getMainRenderTarget().bindWrite(false);
            isDoingHighlight = false;
        }
    }

    public void onComputeCamera(ViewportEvent.ComputeCameraAngles event) {
        if (shouldRenderHighlighted(mc.player)) {
            if (displayHeight != mc.getWindow().getHeight() || displayWidth != mc.getWindow().getWidth()) {
                this.displayHeight = mc.getWindow().getHeight();
                this.displayWidth = mc.getWindow().getWidth();
                this.updateFramebufferSize(this.displayWidth, this.displayHeight);
            }
            adjustHighlightShader(getProgress((float) event.getPartialTick()));

        }
    }

    private void updateFramebufferSize(int width, int height) {
        if (this.blurShader != null) {
            this.blurShader.resize(width, height);
        }
    }

    private void adjustHighlightShader(float progress) {
        if (blit0 == null || blur1 == null || blur2 == null) return;
        progress = Mth.clamp(progress, 0, 1);
        float modulation = 1 - (0.5f * progress);
        blit0.getEffect().safeGetUniform("ColorModulate").set(modulation, modulation, modulation, 1);
        blur1.getEffect().safeGetUniform("Radius").set((float) Math.round(10 * progress));
        blur2.getEffect().safeGetUniform("Radius").set((float) Math.round(10 * progress));

    }

    public void onRenderLevelEvent(RenderLevelStageEvent event) {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            this.endHighlightBatch();
        }
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            if (shouldRenderHighlighted(mc.player)) {
                this.blurShader.process(mc.getTimer().getGameTimeDeltaTicks());
                this.mc.getMainRenderTarget().bindWrite(false);
            }
        }
    }

    private boolean shouldRenderHighlighted(Player player) {
        return SyntheticsPlayerCache.get(player).entityHighlightcache.shouldHighlightEntities;
    }

    public static boolean isEntityHighlighted(Player player, LivingEntity entity) {
        Collection<DoubleObjectPair<HolderSet<EntityType<?>>>> highlightData = SyntheticsPlayerCache.get(player).entityHighlightcache.blacklistedInRadii();
        float distanceToPlayer = entity.distanceTo(player);
        for(var radiusBlacklistPair : highlightData) {
            if(distanceToPlayer <= radiusBlacklistPair.keyDouble()) {
                if(isValidEntity(radiusBlacklistPair.right(), entity)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean isValidEntity(HolderSet<EntityType<?>> inBlacklist, LivingEntity entity) {
        return !entity.getType().is(inBlacklist);
    }
    
    private void rebuildShaders() {
        if (this.blurShader != null) {
            this.blurShader.close();
        }
        ResourceLocation blurLocation = Synthetics.rl("shaders/blank.json");
        try {
            this.blurShader = new PostChain(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getMainRenderTarget(), blurLocation);
            RenderTarget swap = this.blurShader.getTempTarget("swap");
            blit0 = blurShader.addPass("blit", swap, this.mc.getMainRenderTarget(), false);
            blur1 = blurShader.addPass("box_blur", this.mc.getMainRenderTarget(), swap, false);
            blur1.getEffect().safeGetUniform("BlurDir").set(1F, 0F);
            blur2 = blurShader.addPass("box_blur", swap, this.mc.getMainRenderTarget(), false);
            blur2.getEffect().safeGetUniform("BlurDir").set(0F, 1F);

            this.blurShader.resize(this.mc.getWindow().getWidth(), this.mc.getWindow().getHeight());

        } catch (Exception e) {
            Synthetics.LOGGER.warn("Failed to load entity highlighting blur shader", e);
            this.blurShader = null;
        }
    }

    private void endHighlightBatch() {
        if (shouldRenderHighlighted(mc.player)) {
            this.highlightBuffers.endOutlineBatch();
        }
    }

    private float getProgress(float partialTick) {
        return (highlightTicks + (highlightTicks - lastHighlightTicks) * partialTick) / (float) FADE_TICKS;
    }

    private static class OutlineBuffers {

        private final Map<MultiBufferSource, OutlineBuffer> buffers = new HashMap<>();

        public OutlineBuffer getBuffer(MultiBufferSource original) {
            return buffers.computeIfAbsent(original, OutlineBuffer::new);
        }

        public void endOutlineBatch() {
            buffers.values().forEach(OutlineBuffer::endOutlineBatch);
            buffers.clear();
        }
    }

    private static class OutlineBuffer implements MultiBufferSource {
        private final MultiBufferSource bufferSource;
        private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
        private int teamR = 255;
        private int teamG = 255;
        private int teamB = 255;
        private int teamA = 255;

        public OutlineBuffer(MultiBufferSource pBufferSource) {
            this.bufferSource = pBufferSource;
        }

        @Override
        public @NotNull VertexConsumer getBuffer(RenderType pRenderType) {
            if (pRenderType.isOutline()) {
                VertexConsumer consumer2 = this.outlineBufferSource.getBuffer(pRenderType);
                return new EntityOutlineGenerator(consumer2, this.teamR, this.teamG, this.teamB, this.teamA);
            } else {
                VertexConsumer consumer = this.bufferSource.getBuffer(pRenderType);
                Optional<RenderType> optional = pRenderType.outline();
                if (optional.isPresent()) {
                    VertexConsumer consumer1 = this.outlineBufferSource.getBuffer(optional.get());
                    EntityOutlineGenerator outlinebuffersource$entityoutlinegenerator = new EntityOutlineGenerator(
                            consumer1, this.teamR, this.teamG, this.teamB, this.teamA
                    );
                    return VertexMultiConsumer.create(outlinebuffersource$entityoutlinegenerator, consumer);
                } else {
                    return consumer;
                }
            }
        }

        public void setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
            this.teamR = pRed;
            this.teamG = pGreen;
            this.teamB = pBlue;
            this.teamA = pAlpha;
        }

        public void endOutlineBatch() {
            this.outlineBufferSource.endBatch();
        }

        record EntityOutlineGenerator(VertexConsumer delegate, int color) implements VertexConsumer {
            public EntityOutlineGenerator(VertexConsumer p_109943_, int p_109944_, int p_109945_, int p_109946_, int p_109947_) {
                this(p_109943_, FastColor.ARGB32.color(p_109947_, p_109944_, p_109945_, p_109946_));
            }

            @Override
            public @NotNull VertexConsumer addVertex(float p_350357_, float p_350369_, float p_350557_) {
                this.delegate.addVertex(p_350357_, p_350369_, p_350557_).setColor(this.color);
                return this;
            }

            @Override
            public @NotNull VertexConsumer setColor(int r, int g, int b, int a) {
                return this;
            }

            @Override
            public @NotNull VertexConsumer setUv(float uv1, float uv2) {
                this.delegate.setUv(uv1, uv2);
                return this;
            }

            @Override
            public @NotNull VertexConsumer setUv1(int _1, int _2) {
                return this;
            }

            @Override
            public @NotNull VertexConsumer setUv2(int _1, int _2) {
                return this;
            }

            @Override
            public @NotNull VertexConsumer setNormal(float _1, float _2, float _3) {
                return this;
            }
        }
    }
}
