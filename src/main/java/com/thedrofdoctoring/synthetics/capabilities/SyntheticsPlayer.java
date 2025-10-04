package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttachments;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.AugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SyntheticsPlayer implements ISyntheticsEntity, ISyncable {

    private static final String KEY = "synthetic_player";

    public static final ResourceLocation MANAGER_KEY = Synthetics.rl(KEY);

    private final List<AugmentInstance> augments;
    private final AbilityManager abilityManager;
    private final ComplexityManager complexityManager;
    private final PartManager partManager;
    private final ResearchManager researchManager;
    private final Player player;

    private boolean dirty;
    private boolean dirtyAll;


    private int totalPowerCost;

    public SyntheticsPlayer(Player player) {
        this.player = player;
        this.abilityManager = new AbilityManager(this);
        this.complexityManager = new ComplexityManager(this);
        this.partManager = new PartManager(this);
        this.researchManager = new ResearchManager(this);
        this.augments = new ArrayList<>();

    }
    public static SyntheticsPlayer get(Player player) {
        return player.getData(SyntheticsAttachments.SYNTHETICS_MANAGER);
    }


    public void markDirty() {
        this.dirty = true;
    }
    public void markDirtyAll() {
        this.dirtyAll = true;
    }

    public int getTotalStoredEnergy() {
        return 0;
    }

    @Override
    public boolean canAddAugment(SyntheticAugment augment) {

        if(this.complexityManager.testComplexity(new AugmentInstance(augment, this.getPartManager().getPartForAugment(augment)), null) != ComplexityManager.ComplexityResult.SUCCESS) {
            return false;
        }

        return true;
    }

    @Override
    public void addAugment(@NotNull SyntheticAugment augment, boolean sync) {
        addAugment(augment);
        onUpdate(sync);
    }

    public void replaceAugmentInstance(AugmentInstance old, AugmentInstance newInstance) {
        this.augments.remove(old);
        this.complexityManager.removePart(old);
        if(old.augment() != newInstance.augment()) {
            abilityManager.removeAbilities(old.augment());
            abilityManager.removeAbilities(old.appliedPart());
            abilityManager.addAbilities(newInstance.augment());
            abilityManager.addAbilities(newInstance.appliedPart());
        }
        totalPowerCost = totalPowerCost - old.augment().powerCost() + newInstance.augment().powerCost();
    }

    private void addAugment(@NotNull SyntheticAugment augment) {
        AugmentInstance instance = new AugmentInstance(augment, this.partManager.getPartForAugment(augment));
        augments.add(instance);
        this.complexityManager.addPart(instance);
        this.abilityManager.addAbilities(augment);
        totalPowerCost += augment.powerCost();
    }


    public boolean isAugmentInstalled(@NotNull SyntheticAugment augment) {
        return this.augments.stream().anyMatch(p -> p.augment() == augment);
    }

    @Override
    public void removeAugment(SyntheticAugment augment) {
        augments.removeIf(p -> p.augment() == augment);
        abilityManager.removeAbilities(augment);
        totalPowerCost -= augment.powerCost();
        onUpdate(true);
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public List<AugmentInstance> getInstalledAugments() {
        return new ArrayList<>(augments);
    }


    @Override
    public void onTick() {
        if(!this.player.level().isClientSide) {

            CompoundTag packet = new CompoundTag();

            if(abilityManager.onTick()) {
                dirty = true;
                packet.put(abilityManager.nbtKey(), abilityManager.serialiseUpdateNBT(this.player.level().registryAccess()));
            }
            if(researchManager.isDirty()) {
                dirty = true;
                packet.put(researchManager.nbtKey(), researchManager.serialiseUpdateNBT(this.player.level().registryAccess()));
                researchManager.setDirty(false);
            }

            if(dirty) {
                if(!dirtyAll) {
                    this.sync(packet);
                } else {
                    this.sync(true);
                }
                dirty = false;
                dirtyAll = false;
            }
        } else {
            abilityManager.onTick();
        }
    }

    @Override
    public void onUpdate(boolean sync) {

        this.abilityManager.onUpdate();

        if(sync || this.dirty) {
            sync(true);
        }
    }

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put("augments", serialiseAugments(provider));
        tag.putInt("power_cost", totalPowerCost);

        tag.put(partManager.nbtKey(), partManager.serialiseNBT(provider));
        tag.put(abilityManager.nbtKey(), abilityManager.serialiseNBT(provider));
        tag.put(complexityManager.nbtKey(), complexityManager.serialiseNBT(provider));
        tag.put(researchManager.nbtKey(), researchManager.serialiseNBT(provider));

        return tag;
    }


    private CompoundTag serialiseAugments(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        int size = augments.size();
        if(size == 0) return tag;

        for(int i = 0; i < augments.size(); i++) {
            tag.putString(String.valueOf(i), augments.get(i).augment().augmentID().toString());
        }
        return tag;
    }

    private void deserialiseAugments(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        HolderGetter<SyntheticAugment> lookup = provider.lookupOrThrow(SyntheticsData.AUGMENTS);
        this.augments.clear();

        if(nbt.contains("augments") && nbt.get("augments") instanceof CompoundTag tag && !tag.isEmpty()) {
            int size = tag.size();
            for(int i = 0; i < size; i++) {
                String augmentIDString = tag.getString(String.valueOf(i));
                SyntheticAugment augment = Helper.retrieveDataObject(augmentIDString, SyntheticsData.AUGMENTS, lookup);
                if(augment != null) {
                    addAugment(augment);
                }
            }
        }
    }

    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        this.totalPowerCost = nbt.getInt("power_cost");
        abilityManager.clear();
        partManager.deserialiseNBT(provider, nbt);

        this.deserialiseAugments(provider, nbt);

        abilityManager.deserialiseNBT(provider, nbt);
        complexityManager.deserialiseNBT(provider, nbt);
        researchManager.deserialiseNBT(provider, nbt);


        this.onUpdate(false);
    }

    @Override
    public String nbtKey() {
        return KEY;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public ComplexityManager getComplexityManager() {
        return complexityManager;
    }

    public PartManager getPartManager() {
        return partManager;
    }
    public ResearchManager getResearchManager() {
        return researchManager;
    }


    @Override
    public CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put(this.complexityManager.nbtKey(), this.complexityManager.serialiseUpdateNBT(provider));
        tag.put(this.abilityManager.nbtKey(), this.abilityManager.serialiseUpdateNBT(provider));
        tag.put(this.researchManager.nbtKey(), this.researchManager.serialiseUpdateNBT(provider));


        return tag;
    }

    @Override
    public void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        this.complexityManager.deserialiseUpdateNBT(provider, nbt);
        this.abilityManager.deserialiseUpdateNBT(provider, nbt);
        this.researchManager.deserialiseUpdateNBT(provider, nbt);
    }



    public static class Serializer implements IAttachmentSerializer<CompoundTag, SyntheticsPlayer> {

        @Override
        public @NotNull SyntheticsPlayer read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            if (holder instanceof Player player) {
                SyntheticsPlayer handler = new SyntheticsPlayer(player);
                handler.deserialiseNBT(provider, tag);
                return handler;
            }
            throw new IllegalStateException("Cannot deserialize Synthetics Manager for non player entity");
        }

        @Override
        public CompoundTag write(SyntheticsPlayer attachment, HolderLookup.@NotNull Provider provider) {
            return attachment.serialiseNBT(provider);
        }
    }

    public static class Factory implements Function<IAttachmentHolder, SyntheticsPlayer> {

        @Override
        public SyntheticsPlayer apply(IAttachmentHolder holder) {
            if (holder instanceof Player player) {
                return new SyntheticsPlayer(player);
            }
            throw new IllegalArgumentException("Cannot create Synthetic Manager attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }

    @Override
    public void sync(boolean syncToAll) {
        if(player instanceof ServerPlayer serverPlayer) {
            ClientboundPlayerUpdatePacket self = ClientboundPlayerUpdatePacket.create(this.player, this.serialiseNBT(player.level().registryAccess()), true);
            serverPlayer.connection.send(self);
            if (syncToAll) {
                if (player.level() instanceof ServerLevel level) {
                    ClientboundPlayerUpdatePacket other = ClientboundPlayerUpdatePacket.create(this.player, this.serialiseUpdateNBT(player.level().registryAccess()), false);
                    ServerChunkCache serverchunkcache = level.getChunkSource();
                    serverchunkcache.broadcast(player, other);
                }
            }
        }
    }
    public void sync(CompoundTag data) {
        if(player instanceof ServerPlayer serverPlayer) {
            ClientboundPlayerUpdatePacket self = ClientboundPlayerUpdatePacket.create(this.player, data, false);
            serverPlayer.connection.send(self);
        }
    }
    public void sync(CompoundTag data, boolean syncToAll) {
        if(player instanceof ServerPlayer serverPlayer) {
            ClientboundPlayerUpdatePacket self = ClientboundPlayerUpdatePacket.create(this.player, data, true);
            serverPlayer.connection.send(self);
            if (syncToAll) {
                if (player.level() instanceof ServerLevel level) {
                    ClientboundPlayerUpdatePacket other = ClientboundPlayerUpdatePacket.create(this.player, data, false);
                    ServerChunkCache serverchunkcache = level.getChunkSource();
                    serverchunkcache.broadcast(player, other);
                }
            }
        }
    }
}
