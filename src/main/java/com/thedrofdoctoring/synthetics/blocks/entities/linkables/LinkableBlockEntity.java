package com.thedrofdoctoring.synthetics.blocks.entities.linkables;

import com.thedrofdoctoring.synthetics.capabilities.linkable.BlockLinkingPlayer;
import com.thedrofdoctoring.synthetics.world.data.LinkableBlocksData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class LinkableBlockEntity extends BlockEntity {

    private static final String UUID_TAG = "linkable_uuid";
    private static final String LAST_LOCATION_TAG = "last_known_position";

    private UUID uuid;
    private BlockPos lastKnownPosition;

    public LinkableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.uuid = UUID.randomUUID();
        this.lastKnownPosition = pos;
    }


    public void onLinkWithPlayer(Player player) {
        player.displayClientMessage(Component.translatable("synthetics.text.successfully_linked").withStyle(ChatFormatting.BLUE), true);
        player.playNotifySound(SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 1f, 1.25f);
    }

    /**
     * Fired on both Client and Server
     */
    public void onLinkedInteract(Player player) {
        onLinkedInteract(player, null, InteractionHand.MAIN_HAND);
    }

    /**
     * Fired on both Client and Server
     */
    public abstract void onLinkedInteract(Player player, @Nullable ItemStack stack, @NotNull InteractionHand hand);

    public boolean isLinked(Player player) {
        if(player instanceof ServerPlayer serverPlayer) {
            return LinkableBlocksData.getData(serverPlayer.server).isPlayerLinkedToLinkable(serverPlayer, this.uuid);
        } else if(player instanceof AbstractClientPlayer && this.level != null) {
            return BlockLinkingPlayer.get(player).isLinkedToPos(GlobalPos.of(this.level.dimension(), this.getBlockPos()));
        }
        return false;
    }

    public ItemInteractionResult onLinkedUseItem(Player player, @NotNull ItemStack stack, @NotNull InteractionHand hand, BlockHitResult hitResult) {
        onLinkedInteract(player, stack, hand);
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public int getMaxLinkedPlayers() {
        return 1000;
    }

    public void onNonLinkedInteract(Player player) {
        if(player.isShiftKeyDown() && player instanceof ServerPlayer sp) {
            this.tryLinkPlayer(sp);
        }
    }

    public void tryLinkPlayer(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if(server != null && this.level != null) {
            LinkableBlocksData data = LinkableBlocksData.getData(server);
            if(data.isAlreadyLinked(this.uuid) && canLinkWithPlayer(player)) {
                linkPlayer(player, data);
            } else {
                data.addNewLinked(this.uuid, this.level, this.getBlockPos());
                if(canLinkWithPlayer(player)) {
                    linkPlayer(player, data);
                }
            }

        }
    }

    private void linkPlayer(ServerPlayer player, LinkableBlocksData data) {
        data.addPlayer(player, this.uuid);
        this.onLinkWithPlayer(player);
    }

    public void unlinkPlayer(ServerPlayer player) {
        LinkableBlocksData data = LinkableBlocksData.getData(player.server);
        data.removePlayer(player, uuid);
    }

    public int getLinkedPlayerCount() {

        if(this.level == null || this.level.getServer() == null) return -1;
        return LinkableBlocksData.getData(this.level.getServer()).getLinkedPlayerCount(this.uuid);
    }

    public abstract boolean canPlayerInteract(Player player);

    public boolean canLinkWithPlayer(Player player) {
        return this.getLinkedPlayerCount() < this.getMaxLinkedPlayers() && canPlayerInteract(player);
    }

    public void onRemoveLinkable(ServerLevel level) {
        List<ServerPlayer> affectedPlayers = LinkableBlocksData.getData(level.getServer()).removeLinked(level, this.uuid);
        affectedPlayers.forEach(this::removeLinkedForPlayer);
    }

    public void removeLinkedForPlayer(ServerPlayer player) {
        unlinkPlayer(player);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider lookupProvider) {
        return this.saveWithoutMetadata(lookupProvider);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.hasUUID(UUID_TAG)) {
            this.uuid = tag.getUUID(UUID_TAG);
        }
        if(tag.contains(LAST_LOCATION_TAG)) {
            Optional<BlockPos> posOpt =  NbtUtils.readBlockPos(tag, LAST_LOCATION_TAG);
            posOpt.ifPresent(lastKnown -> handleMoved(this.getBlockPos(), lastKnown));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putUUID(UUID_TAG, this.uuid);
        Tag lastKnownPosiiton = NbtUtils.writeBlockPos(this.lastKnownPosition);
        tag.put(LAST_LOCATION_TAG, lastKnownPosiiton);
    }
    // if the block is moved eg via quark pistons, hopefully we can catch that and move the linkable.
    private void handleMoved(BlockPos newPos, BlockPos lastKnownPosition) {
        if(!lastKnownPosition.equals(newPos) && level instanceof ServerLevel serverLevel) {
            this.lastKnownPosition = newPos;
            LinkableBlocksData data = LinkableBlocksData.getData(serverLevel.getServer());
            data.updateLinkedPosition(serverLevel, this.uuid, newPos);
        }
    }
}
