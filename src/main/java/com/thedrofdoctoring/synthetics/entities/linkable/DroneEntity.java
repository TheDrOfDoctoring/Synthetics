package com.thedrofdoctoring.synthetics.entities.linkable;

import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.client.sounds.DroneFlyingSoundInstance;
import com.thedrofdoctoring.synthetics.core.SyntheticsEntities;
import com.thedrofdoctoring.synthetics.core.SyntheticsSounds;
import com.thedrofdoctoring.synthetics.core.data.collections.tags.SyntheticsItemTags;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundViewLinkPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DroneEntity extends LivingEntity implements ILinkableEntity {

    private final NonNullList<ItemStack> handItems;
    private final NonNullList<ItemStack> armorItems;
    private Player currentlyLinked;
    private FakePlayer currentlyLinkedFake;

    public static final EntityDataAccessor<Boolean> IN_FLIGHT =
            SynchedEntityData.defineId(
                    DroneEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    public DroneEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
        this.handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    }

    public static DroneEntity create(Level level) {
        return new DroneEntity(SyntheticsEntities.DRONE_ENTITY.get(), level);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IN_FLIGHT, false);
    }

    public void useItemInHand(ServerPlayer controllingPlayer, InteractionHand hand) {
        if(currentlyLinkedFake.getGameProfile() != controllingPlayer.getGameProfile()) return;
        ItemStack stack = this.getItemBySlot(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        if(this.isItemValidForDrone(stack)) {
            HitResult result = this.pick(controllingPlayer.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0, false);

            currentlyLinkedFake.setItemInHand(hand, stack);
            if(result.getType() == HitResult.Type.BLOCK) {
                InteractionResult interactionResult = currentlyLinkedFake.gameMode.useItemOn(currentlyLinkedFake, level(), stack, hand, (BlockHitResult) result);
                if(interactionResult == InteractionResult.PASS) {
                    currentlyLinkedFake.gameMode.useItem(currentlyLinkedFake, level(), stack, hand);
                }
            } else {
                currentlyLinkedFake.gameMode.useItem(currentlyLinkedFake, level(), stack, hand);
            }
        }
    }

    private boolean isItemValidForDrone(ItemStack stack) {
        return stack.getItem() instanceof BlockItem  || stack.is(SyntheticsItemTags.VALID_DRONE);
    }


    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == IN_FLIGHT && isFlying()) {
            playDroneSound();
        }
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot equipmentSlot) {
        ItemStack stack;
        if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
            stack = this.handItems.get(equipmentSlot.getIndex());
        } else {
            stack = ItemStack.EMPTY;
        }

        return stack;
    }



    @Override
    public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
        this.verifyEquippedItem(stack);
        switch (slot.getType()) {
            case HAND:
                this.onEquipItem(slot, this.handItems.set(slot.getIndex(), stack), stack);
                break;
            case HUMANOID_ARMOR:
                this.onEquipItem(slot, this.armorItems.set(slot.getIndex(), stack), stack);
                break;
            case ANIMAL_ARMOR:
                break;
        }
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0d).add(Attributes.FLYING_SPEED, 0.05d).add(Attributes.MOVEMENT_SPEED, 0.1d).add(Attributes.ATTACK_DAMAGE, 2.0d).add(Attributes.FOLLOW_RANGE, 48.0d);
    }

    @Override
    protected void tickDeath() {
        if (!this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {

        if(player instanceof ServerPlayer serverPlayer) {
            if(canLinkWithPlayer() && player.getMainHandItem().isEmpty() && !player.isShiftKeyDown()) {
                boolean canLink = SyntheticsPlayer.get(player).getAbilityManager().hasAbilityType(SyntheticAbilities.DRONE_LINK.get());
                if(canLink) {
                    this.linkWithPlayer(serverPlayer);
                }
            }
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
                ItemStack mainHand = serverPlayer.getMainHandItem();
                if(mainHand.isEmpty()) {
                    ItemStack removed = tryRemoveItem();
                    if(!removed.isEmpty()) {
                        this.level().playSound(null, this.position().x, this.position().y, this.position().z, SoundEvents.ITEM_PICKUP, this.getSoundSource());
                        player.addItem(removed);
                    }

                } else {
                    ItemStack added = tryAddItem(mainHand);
                    if(!added.isEmpty()) {
                        this.level().playSound(null, this.position().x, this.position().y, this.position().z, SoundEvents.ITEM_PICKUP, this.getSoundSource());
                    }
                    mainHand.shrink(added.getCount());
                }
            }

        }

        return super.interact(player, hand);
    }

    private ItemStack tryAddItem(ItemStack input) {
        ItemStack left = this.handItems.getFirst();
        ItemStack right = this.handItems.getLast();
        if(left.isEmpty()) {
            this.handItems.set(0, input.copy());
            return input;
        } else if(right.isEmpty()) {
            this.handItems.set(1, input.copy());
            return input;
        }

        if(ItemStack.isSameItemSameComponents(input, left)) {
            int canTakeAmount = left.getMaxStackSize() - left.getCount();
            if(canTakeAmount <= 0) {
                return left;
            }
            return input.getCount() > canTakeAmount ? input.copyWithCount(input.getCount() - canTakeAmount) : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;

    }
    private ItemStack tryRemoveItem() {
        ItemStack first = ContainerHelper.takeItem(handItems, 0);

        return first.isEmpty() ? ContainerHelper.takeItem(handItems, 1) : first;
    }

    private boolean canLinkWithPlayer() {
        return this.currentlyLinked == null;
    }

    private void linkWithPlayer(ServerPlayer player) {
        SyntheticsPlayerCache.get(player).isNotViewingSelf = true;
        player.setCamera(this);
        this.setCurrentlyLinkedPlayer(player);
        sendViewPacket(player, true);
        player.displayClientMessage(Component.translatable("synthetics.text.drone_controls"), true);
    }

    public void unlinkWithPlayer(ServerPlayer player) {
        SyntheticsPlayerCache.get(player).isNotViewingSelf = false;
        player.setCamera(player);
        this.setCurrentlyLinkedPlayer(null);
        sendViewPacket(player, false);
        this.setFlying(false);
    }

    public void setCurrentlyLinkedPlayer(@Nullable ServerPlayer player) {
        if(player == null) {
            this.currentlyLinked = null;
            this.currentlyLinkedFake = null;
        } else {
            this.currentlyLinked = player;
            this.currentlyLinkedFake = FakePlayerFactory.get(player.serverLevel(), player.getGameProfile());
        }
    }

    private static void sendViewPacket(ServerPlayer player, boolean isNotViewingSelf) {
        player.connection.send(new ClientboundViewLinkPacket(isNotViewingSelf));
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {}

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isControlledByLocalInstance()) {

            if(isFlying()) {
                this.inFlightTravel(travelVector);
            } else {
                super.travel(travelVector);
            }
        }

        this.calculateEntityAnimation(false);
    }

    public boolean isFlying() {
        return entityData.get(IN_FLIGHT);
    }

    public void setFlying(boolean inFlight) {
        entityData.set(IN_FLIGHT, inFlight);
    }

    private void inFlightTravel(Vec3 travelVector) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        } else {
            BlockPos ground = getBlockPosBelowThatAffectsMyMovement();

            float f = 0.91F;
            if (this.onGround()) {
                f = this.level().getBlockState(ground).getFriction(this.level(), ground, this);
            }
            float flightSpeed = (float) this.getAttributeValue(Attributes.FLYING_SPEED);

            this.moveRelative(flightSpeed, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(f));
            if(verticalCollisionBelow && travelVector.y < 0) {
                this.setFlying(false);
            }
        }
    }

    @Override
    public boolean onGround() {
        return super.onGround() && !this.isFlying();
    }


    private void playDroneSound() {
        DroneFlyingSoundInstance sound = new DroneFlyingSoundInstance(this, SyntheticsSounds.DRONE_BUZZ.get(), SoundSource.NEUTRAL);
        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (!CommonHooks.onLivingDeath(this, damageSource)) {
            if (!this.isRemoved() && !this.dead) {
                Entity entity = damageSource.getEntity();
                this.dead = true;
                Level var5 = this.level();
                if (var5 instanceof ServerLevel serverlevel) {
                    if (entity == null || entity.killedEntity(serverlevel, this)) {
                        this.gameEvent(GameEvent.ENTITY_DIE);
                        this.dropAllDeathLoot(serverlevel, damageSource);
                    }

                    this.level().broadcastEntityEvent(this, (byte)3);
                }

            }

        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Items")) {
            ContainerHelper.loadAllItems(compound, handItems, this.registryAccess());
        }

    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ContainerHelper.saveAllItems(compound, handItems, this.registryAccess());
    }

    @Override
    protected void dropAllDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource source) {
        super.dropAllDeathLoot(level, source);
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public @Nullable Player getLinkedTo() {
        return this.currentlyLinked;
    }
}
