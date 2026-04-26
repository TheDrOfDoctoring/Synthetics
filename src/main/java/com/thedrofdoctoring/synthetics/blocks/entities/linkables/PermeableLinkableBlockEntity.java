package com.thedrofdoctoring.synthetics.blocks.entities.linkables;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.linkables.PermeableLinkableBlock;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PermeableLinkableBlockEntity extends LinkableBlockEntity {

    private static final String ID = "linked_players";
    public static final ModelProperty<BlockState> MODEL_STATE = new ModelProperty<>();
    private final Set<UUID> linkedPlayers;
    private static final String DISGUISED_BLOCKSTATE = "disguised_blockstate";


    private @Nullable BlockState disguisedAs;

    public PermeableLinkableBlockEntity(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.PERMEABLE_LINKABLE.get(), pos, blockState);
        this.linkedPlayers = new HashSet<>();
    }

    @Override
    public void onLinkedInteract(Player player, @Nullable ItemStack stack, @NotNull InteractionHand hand) {
        if(hand == InteractionHand.OFF_HAND) return;
        if(level == null || player.level().isClientSide) return;
        if(stack != null && disguisedAs == null && stack.getItem() instanceof BlockItem block) {
            if(isValidBlock(block.getBlock().defaultBlockState())) {
                this.disguisedAs = block.getBlock().defaultBlockState();
                stack.shrink(1);
                SoundType type = block.getBlock().getSoundType(block.getBlock().defaultBlockState(), level, this.getBlockPos(), player);
                player.playNotifySound(type.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                this.setChanged();
            }
        } else if(player.distanceToSqr(this.getBlockPos().getCenter()) <= Mth.square(player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE))) {
            if(player.getMainHandItem().isEmpty() && this.disguisedAs != null) {
                Item item = this.disguisedAs.getBlock().asItem();
                ItemStack asBlockStack = new ItemStack(item, 1);
                this.disguisedAs = null;
                player.addItem(asBlockStack);
                player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                this.setChanged();
            }
        }
    }

    public ItemInteractionResult onLinkedUseItem(Player player, @NotNull ItemStack stack, @NotNull InteractionHand hand, BlockHitResult hitResult) {
        if(hand == InteractionHand.OFF_HAND) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(level == null || player.level().isClientSide) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(disguisedAs == null && stack.getItem() instanceof BlockItem block) {
            if(isValidBlock(block.getBlock().defaultBlockState())) {
                this.disguisedAs = block.getBlock().getStateForPlacement(new BlockPlaceContext(player, hand, stack, hitResult));
                stack.shrink(1);
                SoundType type = block.getBlock().getSoundType(block.getBlock().defaultBlockState(), level, this.getBlockPos(), player);
                player.playNotifySound(type.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                this.setChanged();
                return ItemInteractionResult.sidedSuccess(false);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean isValidBlock(BlockState state) {
        Block block = state.getBlock();
        return !(block instanceof BaseEntityBlock || block instanceof DoorBlock || block instanceof DoublePlantBlock);
    }

    @Override
    public boolean canPlayerInteract(Player player) {
        return true;
    }

    @Override
    public int getLinkedPlayerCount() {
        return linkedPlayers.size();
    }

    @Override
    public void tryLinkPlayer(ServerPlayer player) {
         if(canLinkWithPlayer(player) && !linkedPlayers.contains(player.getUUID())) {
            linkedPlayers.add(player.getUUID());
            this.onLinkWithPlayer(player);
            this.setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if(level != null) {
            BlockState state = this.getBlockState();
            BlockState newState = state.setValue(PermeableLinkableBlock.HAS_DISGUISE, disguisedAs != null);
            if(!newState.equals(state)) {
                level.setBlock(getBlockPos(), newState, Block.UPDATE_ALL);
            }
            level.sendBlockUpdated(worldPosition, state, newState, Block.UPDATE_ALL);
            this.requestModelDataUpdate();
        }
    }




    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider lookupProvider) {
        CompoundTag tag = this.saveWithoutMetadata(lookupProvider);
        saveAdditional(tag, lookupProvider);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
        super.handleUpdateTag(tag, lookupProvider);
        this.setChanged();
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        if(level != null) {
            this.setChanged();
        }
        this.requestModelDataUpdate();
    }


    @Override
    public void unlinkPlayer(ServerPlayer player) {
        linkedPlayers.remove(player.getUUID());
    }

    @Override
    public void onRemoveLinkable(ServerLevel level) {
        if(disguisedAs != null) {
            Item item = this.disguisedAs.getBlock().asItem();
            ItemStack asBlockStack = new ItemStack(item, 1);
            this.disguisedAs = null;
            Vec3 pos = this.getBlockPos().getCenter();
            ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, asBlockStack);
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public boolean canLinkWithPlayer(Player player) {
        return super.canLinkWithPlayer(player) && SyntheticsPlayer.get(player).getAbilityManager().hasAbilityType(SyntheticAbilities.PERMEABLE_LINK.get());
    }

    @Override
    public void onLinkWithPlayer(Player player) {
        super.onLinkWithPlayer(player);
        if(player instanceof ServerPlayer serverPlayer) {
            this.linkWithNearbyPermeable(serverPlayer);
        }
    }

    private void linkWithNearbyPermeable(ServerPlayer player) {
        if(this.level == null)return;
        BlockPos pos = this.getBlockPos();
        BlockPos.betweenClosedStream(new AABB(pos).inflate(1))
                .map(BlockPos::immutable)
                .forEach(nearbyPos -> {
                    BlockEntity be = level.getBlockEntity(nearbyPos);
                    if(be instanceof PermeableLinkableBlockEntity linkable) {
                        linkable.linkedPlayers.add(player.getUUID());
                        linkable.setChanged();
                    }
                });
    }

    @Override
    public boolean isLinked(Player player) {
        return this.linkedPlayers.contains(player.getUUID());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        Set<UUID> uuids =
                UUIDUtil.CODEC_SET
                        .decode(NbtOps.INSTANCE, getOrEmpty(tag))
                        .result()
                        .map(Pair::getFirst)
                        .orElseGet(Set::of);
        this.linkedPlayers.clear();
        this.linkedPlayers.addAll(uuids);
        if(tag.contains(DISGUISED_BLOCKSTATE, CompoundTag.TAG_COMPOUND)) {
            CompoundTag stateData = tag.getCompound(DISGUISED_BLOCKSTATE);
            HolderGetter<Block> lookup = this.level == null ? BuiltInRegistries.BLOCK.asLookup() : this.level.holderLookup(Registries.BLOCK);
            this.disguisedAs = NbtUtils.readBlockState(lookup, stateData);
        } else {
            this.disguisedAs = null;
        }
        this.setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        UUIDUtil.CODEC_SET
                .encodeStart(NbtOps.INSTANCE, linkedPlayers)
                .ifSuccess(result -> tag.put(ID, result))
                .ifError( (err) -> Synthetics.LOGGER.warn("Failed to serialise linked players, {}", err));
        if(this.disguisedAs != null) {
            tag.put(DISGUISED_BLOCKSTATE, NbtUtils.writeBlockState(this.disguisedAs));
        }
    }


    @Override
    public @NotNull ModelData getModelData() {
        if(disguisedAs != null) {
            return ModelData.of(MODEL_STATE, disguisedAs);
        }
        return ModelData.EMPTY;
    }




    public @Nullable BlockState disguisedAs() {
        return disguisedAs;
    }

    private static Tag getOrEmpty(CompoundTag tag) {
        Tag t = tag.get(ID);
        if(t instanceof ListTag) {
            return t;
        } else {
            return new ListTag();
        }
    }
}
