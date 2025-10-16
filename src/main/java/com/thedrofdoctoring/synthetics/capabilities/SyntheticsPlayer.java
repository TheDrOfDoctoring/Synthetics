package com.thedrofdoctoring.synthetics.capabilities;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.capabilities.interfaces.ISyntheticsEntity;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientManager;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttachments;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.AugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodySegment;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.core.Holder;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
@SuppressWarnings("unused")
public class SyntheticsPlayer implements ISyntheticsEntity, ISyncable {

    private static final String KEY = "synthetic_player";

    public static final ResourceLocation MANAGER_KEY = Synthetics.rl(KEY);

    private final List<AugmentInstance> augments;

    private final AbilityManager abilityManager;
    private final ComplexityManager complexityManager;
    private final PartManager partManager;
    private final PowerManager powerManager;
    private final ResearchManager researchManager;
    private final Player player;

    private boolean dirty;
    private boolean dirtyAll;


    public SyntheticsPlayer(Player player) {
        this.player = player;
        this.abilityManager = new AbilityManager(this);
        this.complexityManager = new ComplexityManager(this);
        this.partManager = new PartManager(this);
        this.researchManager = new ResearchManager(this);
        this.powerManager = new PowerManager(this);
        this.augments = new ArrayList<>();

    }
    public static SyntheticsPlayer get(Player player) {
        return player.getData(SyntheticsAttachments.SYNTHETICS_MANAGER);
    }


    public void markDirty() {
        this.dirty = true;
    }
    public void markDirtyAll() {
        this.dirty = true;
        this.dirtyAll = true;
    }

    public int getTotalStoredEnergy() {
        return 0;
    }

    @Override
    public boolean canAddAugment(AugmentInstance instance) {

        if(this.complexityManager.testComplexity(instance, null) != ComplexityManager.ComplexityResult.SUCCESS) {
            return false;
        }
        if(!this.partManager.augmentSupportsBodyPart(instance.augment(), instance.appliedPart())) {
            return false;
        }
        int onPart = 1;
        int total = 1;
        for(AugmentInstance installedInstances : augments) {

            if(installedInstances.augment().equals(instance.augment())) {
                total++;
                if(installedInstances.appliedPart().equals(instance.appliedPart())) {
                    onPart++;
                }

            }
            if(total > instance.augment().maxTotal() || onPart > instance.augment().maxPerPart()) {
                return false;
            }
        }

        return true;
    }



    @Override
    public void addAugment(@NotNull AugmentInstance augment, boolean sync) {
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
        this.augments.add(newInstance);

        int totalPowerCost = Math.max(0, this.powerManager.getTotalPowerCost() - old.augment().powerCost() + newInstance.augment().powerCost());
        this.powerManager.setTotalPowerCost(totalPowerCost);
    }

    private void addAugment(@NotNull AugmentInstance instance) {
        augments.add(instance);
        this.complexityManager.addPart(instance);
        this.abilityManager.addAbilities(instance.augment());
        this.powerManager.setTotalPowerCost(this.powerManager.getTotalPowerCost() + instance.augment().powerCost());
    }

    @Override
    public boolean canAddInstallable(IBodyInstallable<?> installable) {


        if(installable instanceof AugmentInstance instance) {
            return this.canAddAugment(instance);
        }

        if(installable instanceof SyntheticAugment augment) {
            return this.canAddAugment(new AugmentInstance(augment, this.partManager.getDefaultPartForAugment(augment)));
        }
        if(installable instanceof BodyPart part) {
            return this.complexityManager.getTotalPartComplexity(part) <= part.maxComplexity();
        }
        if(installable instanceof BodySegment segment) {
            if(this.complexityManager.getTotalSegmentComplexity(segment) > segment.maxComplexity()) {
                return false;
            }
            Holder<BodySegment> segmentHolder = Holder.direct(segment);
            return this.partManager.getInstalledParts().stream().allMatch(p -> p.segment().contains(segmentHolder));
        }

        return true;
    }

    @Override
    public boolean isInstalled(IBodyInstallable<?> installable) {
        switch (installable) {
            case AugmentInstance instance -> {
                return this.augments.stream().anyMatch(p -> p.augment().equals(instance.augment()));

            }
            case SyntheticAugment augment -> {
                return this.augments.stream().anyMatch(p -> p.augment().equals(augment));
            }
            case BodyPart part -> {
                return this.partManager.isPartInstalled(part);
            }
            case BodySegment segment -> {
                return this.partManager.isSegmentInstalled(segment);
            }
            default -> {
                return false;
            }
        }
    }


    public List<IBodyInstallable<?>> addOrReplaceInstallable(@NotNull IBodyInstallable<?> installable) {

        switch (installable) {

            case AugmentInstance instance -> addAugment(instance, true);

            case SyntheticAugment augment -> {
                AugmentInstance instance = new AugmentInstance(augment, this.partManager.getDefaultPartForAugment(augment));
                addAugment(instance, true);
            }
            case BodyPart part -> {
                return this.partManager.replacePart(part, true);
            }
            case BodySegment segment -> {
                return this.partManager.replaceSegment(segment, true);
            }
            default -> {

            }
        }

        return Collections.emptyList();

    }


    @Override
    public void removeAugment(AugmentInstance instance) {
        augments.remove(instance);
        abilityManager.removeAbilities(instance.augment());
        complexityManager.removePart(instance);
        powerManager.setTotalPowerCost(Math.max(0, powerManager.getTotalPowerCost() - instance.augment().powerCost()));
        onUpdate(true);
    }

    public int installedInstanceCount(SyntheticAugment augment) {
        return (int) this.augments.stream().filter(p -> p.augment().equals(augment)).count();
    }
    public int installedInstanceCount(SyntheticAugment augment, BodyPart part) {
        return (int) this.augments.stream().filter(p -> p.augment().equals(augment) && p.appliedPart().equals(part)).count();
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
            if(powerManager.onTick()) {
                dirty = true;
                packet.put(powerManager.nbtKey(), powerManager.serialiseUpdateNBT(this.player.level().registryAccess()));
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
            powerManager.onTick();
            abilityManager.onTick();
        }
    }

    @Override
    public void onUpdate(boolean sync) {

        this.abilityManager.onUpdate();
        if(this.player.level().isClientSide) {
            SyntheticsClientManager.updateScreen();
        }

        if(sync || this.dirty) {
            sync(true);
        }
    }

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put("augments", serialiseAugments(provider));
        tag.put(powerManager.nbtKey(), powerManager.serialiseNBT(provider));

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
            tag.putString(String.valueOf(i), augments.get(i).createSerialisationID());
        }
        return tag;
    }

    private void deserialiseAugments(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        HolderGetter<SyntheticAugment> lookup = provider.lookupOrThrow(SyntheticsData.AUGMENTS);
        HolderGetter<BodyPart> partLookup = provider.lookupOrThrow(SyntheticsData.BODY_PARTS);

        this.augments.clear();

        if(nbt.contains("augments") && nbt.get("augments") instanceof CompoundTag tag && !tag.isEmpty()) {
            int size = tag.size();
            for(int i = 0; i < size; i++) {
                String instanceIDString = tag.getString(String.valueOf(i));

                Pair<ResourceLocation, ResourceLocation> augmentInstance = AugmentInstance.augmentPartSplitIdentifiers(instanceIDString);

                SyntheticAugment augment = Helper.retrieveDataObject(augmentInstance.getFirst(), SyntheticsData.AUGMENTS, lookup);
                BodyPart part = Helper.retrieveDataObject(augmentInstance.getSecond(), SyntheticsData.BODY_PARTS, partLookup);
                if(augment != null) {
                    if(part == null || !partManager.isPartInstalled(part)) {
                        addAugment(new AugmentInstance(augment, this.partManager.getDefaultPartForAugment(augment)));
                        continue;
                    }
                    addAugment(new AugmentInstance(augment, part));
                }
            }
        }
    }

    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        abilityManager.clear();
        partManager.deserialiseNBT(provider, nbt);

        this.deserialiseAugments(provider, nbt);

        abilityManager.deserialiseNBT(provider, nbt);
        complexityManager.deserialiseNBT(provider, nbt);
        researchManager.deserialiseNBT(provider, nbt);
        powerManager.deserialiseNBT(provider, nbt);


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
    public PowerManager getPowerManager() {
        return this.powerManager;
    }


    @Override
    public CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put(this.complexityManager.nbtKey(), this.complexityManager.serialiseUpdateNBT(provider));
        tag.put(this.abilityManager.nbtKey(), this.abilityManager.serialiseUpdateNBT(provider));
        tag.put(this.researchManager.nbtKey(), this.researchManager.serialiseUpdateNBT(provider));
        tag.put(this.powerManager.nbtKey(), this.powerManager.serialiseUpdateNBT(provider));


        return tag;
    }

    @Override
    public void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        this.complexityManager.deserialiseUpdateNBT(provider, nbt);
        this.abilityManager.deserialiseUpdateNBT(provider, nbt);
        this.researchManager.deserialiseUpdateNBT(provider, nbt);
        this.powerManager.deserialiseUpdateNBT(provider, nbt);
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
