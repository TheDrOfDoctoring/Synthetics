package com.thedrofdoctoring.synthetics.client.particles;

import com.thedrofdoctoring.synthetics.particles.GenericParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericParticleClient extends TextureSheetParticle {

    private GenericParticleClient(@NotNull ClientLevel world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, @NotNull ResourceLocation texture, int maxAge, int color, float speedModifier) {
        super(world, posX, posY, posZ, speedX, speedY, speedZ);
        this.lifetime = maxAge;
        this.xd *= speedModifier;
        this.yd *= speedModifier;
        this.zd *= speedModifier;
        this.bCol = (color & 255) / 255.0F;
        this.gCol = (color >> 8 & 255) / 255.0F;
        this.rCol = (color >> 16 & 255) / 255.0F;
        if ((color >> 24 & 255) != 0) {
            this.alpha = (color >> 24 & 255) / 255.0F;
        }
        this.setSprite(Minecraft.getInstance().particleEngine.textureAtlas.getSprite(texture));
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleProvider<GenericParticle.Options> {
        @Nullable
        @Override
        public Particle createParticle(@NotNull GenericParticle.Options typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GenericParticleClient(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.texture(), typeIn.maxAge(), typeIn.colour(), typeIn.speed());
        }
    }
}
