package com.thedrofdoctoring.synthetics.entities;

import com.thedrofdoctoring.synthetics.core.SyntheticsEntities;
import com.thedrofdoctoring.synthetics.particles.GenericParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FlameProjectileEntity extends AbstractHurtingProjectile {

    private int maxTicks = 20;
    private float speed = 0.5f;
    protected float contactDamage = 3f;
    private int fireTicks = 40;

    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "flame");

    public FlameProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlameProjectileEntity(@NotNull Level worldIn, double x, double y, double z, Vec3 accel) {
        super(SyntheticsEntities.FLAME_PROJECTILE.get(), x, y, z, accel, worldIn);
    }

    public void setMaxTimeAlive(int ticks) {
        this.maxTicks = ticks;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setContactDamage(float damage) {
        this.contactDamage = damage;
    }
    public void setFireTicks(int ticks) {
        this.fireTicks = ticks;
    }

    @Override
    protected float getInertia() {
        return speed;
    }


    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            Vec3 center = this.position();
            this.level().addParticle(GenericParticle.of(texture, 10, 0xFFFFFFFF, speed), center.x, center.y, center.z, 0f, 0f, 0f);
            this.level().addParticle(GenericParticle.of(texture, 10, 0xFFFFFFFF, speed), center.x + 0.15f, center.y, center.z + 0.15f, 0f, 0f, 0f);
            this.level().addParticle(GenericParticle.of(texture, 10, 0xFFFFFFFF, speed), center.x - 0.15f, center.y, center.z - 0.15f, 0f, 0f, 0f);
        }

        if (this.tickCount > this.maxTicks) {
            this.discard();
        }

    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.BLOCK) {
                return;
            }

            Entity target;
            if (result.getType() == HitResult.Type.ENTITY) {
                target = ((EntityHitResult) result).getEntity();
                if (target instanceof FlameProjectileEntity) {
                    return;
                }
                hitEntity(target);
            }
        }
    }

    @Override
    protected boolean shouldBurn() {
        return true;
    }


    private void hitEntity(@NotNull Entity entity) {
        if(entity instanceof Creeper creeper) {
            creeper.ignite();
        }
        entity.hurt(entity.damageSources().onFire(), contactDamage);
        int remainingFire = entity.getRemainingFireTicks();
        entity.igniteForTicks(fireTicks);
    }

    @Override
    public float getPickRadius() {
        return 0.5f;
    }

    @NotNull
    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.fireTicks = compound.getInt("fire_ticks");
        this.maxTicks = compound.getInt("max_ticks");
        this.contactDamage = compound.getFloat("contact_damage");
        this.speed = compound.getFloat("speed");
    }
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putInt("max_ticks", maxTicks);
        compound.putInt("fire_ticks", fireTicks);
        compound.putFloat("speed", speed);
        compound.putFloat("contact_damage", contactDamage);
    }

}
