package com.thedrofdoctoring.synthetics.entities;

import com.thedrofdoctoring.synthetics.core.SyntheticsEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.OptionalInt;
import java.util.UUID;

public class HarpoonProjectileEntity extends AbstractArrow {

    public static final EntityDataAccessor<OptionalInt> HIT_ID =
            SynchedEntityData.defineId(
                    HarpoonProjectileEntity.class,
                    EntityDataSerializers.OPTIONAL_UNSIGNED_INT
            );
    public static final EntityDataAccessor<Vector3f> RELATIVE_HIT =
            SynchedEntityData.defineId(
                    HarpoonProjectileEntity.class,
                    EntityDataSerializers.VECTOR3
            );

    private float speed = 0.5f;
    protected float contactDamage = 3f;
    private float force = 0.1f;
    private int maxConnectedTime = 500;

    private int connectedTime = maxConnectedTime;

    private LivingEntity hitEntity;
    private UUID hitEntityUUID;

    public HarpoonProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public HarpoonProjectileEntity(Level level, double x, double y, double z, ItemStack thrown) {
        super(SyntheticsEntities.HARPOON_PROJECTILE.get(), x, y, z, level, ItemStack.EMPTY, Items.BOW.getDefaultInstance());
        this.setPickupItemStack(Items.DIRT.getDefaultInstance());

    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public void setForce(float force) {
        this.force = force;
    }
    public void setMaxConnectedTime(int ticks) {
        this.maxConnectedTime = ticks;
    }

    public void setContactDamage(float damage) {
        this.contactDamage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HIT_ID, OptionalInt.empty());
        builder.define(RELATIVE_HIT, new Vector3f());
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void tick() {
        boolean reload = hitEntity == null;
        LivingEntity hitEntity = getHitEntity();
        if(hitEntity != null) {
            if(hitEntity.isDeadOrDying()) {
                this.discard();
                return;
            }
            if(reload) {
                this.updateEntityData(hitEntity);
            }

            Vec3 hitEntityPos = hitEntity.position();
            Entity owner = getOwner();
            Vec3 relative = new Vec3(this.entityData.get(RELATIVE_HIT));

            if(owner != null) {
                Vec3 toOwner = owner.position().subtract(hitEntity.position()).normalize().scale(force);
                hitEntity.addDeltaMovement(toOwner);
                this.addDeltaMovement(toOwner);
            }
            if(!this.level().isClientSide) {
                this.setPos(hitEntityPos.add(relative));
                connectedTime--;
                if(connectedTime <= 0) {
                    this.discard();
                }
            }

        } else {
            super.tick();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level().isClientSide && this.hitEntity == null) {
            if (result.getType() == HitResult.Type.BLOCK) {
                this.discard();
                return;
            }

            Entity target;
            if (result.getType() == HitResult.Type.ENTITY) {
                target = ((EntityHitResult) result).getEntity();
                if (target instanceof HarpoonProjectileEntity) {
                    return;
                }
                hitEntity(target);
            }
        }
    }

    private void setHitEntity(LivingEntity entity) {
        this.hitEntity = entity;
        this.hitEntityUUID = entity.getUUID();
    }


    private void hitEntity(@NotNull Entity entity) {
        if(entity instanceof LivingEntity living) {
            entity.hurt(entity.damageSources().mobProjectile(this, living), contactDamage);
            this.setHitEntity(living);
            this.connectedTime = maxConnectedTime;
            updateEntityData(living);
        }
    }

    private void updateEntityData(LivingEntity hitEntity) {
        Vec3 relative = hitEntity.position().subtract(this.position())
                .normalize()
                .scale(0.1f)
                .add(0, (this.position().y - this.hitEntity.getY()), 0);
        Vector3f relativeToHit = relative.toVector3f();
        this.getEntityData().set(HIT_ID, OptionalInt.of(hitEntity.getId()));
        this.getEntityData().set(RELATIVE_HIT, relativeToHit);
    }

    protected void onHitEntity(EntityHitResult result) {
        this.hitEntity(result.getEntity());
    }



    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Nullable
    public LivingEntity getHitEntity() {
        OptionalInt hitId = this.entityData.get(HIT_ID);
        if (this.hitEntity != null && !this.hitEntity.isRemoved()) {
            return this.hitEntity;
        } else if (this.hitEntityUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity e = serverLevel.getEntity(this.hitEntityUUID);
            if(e instanceof LivingEntity living) {
                this.setHitEntity(living);
            }
            return this.hitEntity;
        } else if (hitId.isPresent()) {
            Entity e = level().getEntity(hitId.getAsInt());
            if(e instanceof LivingEntity living) {
                this.setHitEntity(living);
            }
            return this.hitEntity;
        } else {
            return null;
        }
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.hitEntityUUID != null) {
            compound.putUUID("hit_entity", this.hitEntityUUID);
        }
        compound.putFloat("speed", speed);
        compound.putFloat("contact_damage", contactDamage);
        compound.putFloat("pull_force", force);
        compound.putInt("max_connected_time", maxConnectedTime);
        compound.putInt("connected_time", connectedTime);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("hit_entity")) {
            this.hitEntityUUID = compound.getUUID("hit_entity");
            this.hitEntity = null;
        }
        this.force = compound.getFloat("pull_force");
        this.contactDamage = compound.getFloat("contact_damage");
        this.speed = compound.getFloat("speed");
        this.connectedTime = compound.getInt("connected_time");
        this.maxConnectedTime = compound.getInt("max_connected_time");
    }
}
