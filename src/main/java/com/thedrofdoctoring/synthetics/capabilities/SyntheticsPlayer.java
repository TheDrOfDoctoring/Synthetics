package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.interfaces.ISyntheticsEntity;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientManager;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttachments;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.*;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import net.minecraft.core.Holder;
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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SyntheticsPlayer implements ISyntheticsEntity, ISyncable {

    private static final String KEY = "synthetic_player";

    public static final ResourceLocation MANAGER_KEY = Synthetics.rl(KEY);


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
        this.powerManager = new PowerManager();

    }
    public static SyntheticsPlayer get(Player player) {
        return player.getData(SyntheticsAttachments.SYNTHETICS_MANAGER);
    }

    /**
     * Be careful not to mark anything data in the process of deserialising
     */
    public void markDirty() {
        this.dirty = true;
    }
    /**
     * Be careful not to mark anything data in the process of deserialising
     */
    public void markDirtyAll() {
        this.dirty = true;
        this.dirtyAll = true;
    }

    @Override
    public boolean canAddInstallable(IBodyInstallable<?> installable) {

        switch (installable) {
            case AppliedAugmentInstance instance -> {
                return this.parts().canAddAugment(instance, this);
            }
            case Augment augment -> {
                return this.parts().canAddAugment(new AppliedAugmentInstance(augment, this.partManager.getDefaultPartForAugment(augment)), this);
            }
            case BodyPart part -> {
                return this.complexityManager.getTotalPartComplexity(part) <= part.maxComplexity();
            }
            case BodySegment segment -> {
                if (this.complexityManager.getTotalSegmentComplexity(segment) > segment.maxComplexity()) {
                    return false;
                }
                Holder<BodySegment> segmentHolder = Holder.direct(segment);
                return this.parts()
                            .installedBodyParts()
                            .stream()
                            .allMatch(part -> part.validSegments().contains(segmentHolder));
            }
            default -> throw new IllegalStateException("Unexpected installable type: " + installable);
        }
    }

    @Override
    public boolean isInstalled(IBodyInstallable<?> installable) {
        switch (installable) {
            case AppliedAugmentInstance instance -> {
                return this.partManager.isAugmentInstalled(instance);
            }
            case Augment augment -> {
                return this.partManager.installedAugments().stream().anyMatch(augmentInstance -> augmentInstance.augment().equals(augment));
            }
            case BodyPart part -> {
                return this.partManager.isBodyPartInstalled(part);
            }
            case BodySegment segment -> {
                return this.partManager.isSegmentInstalled(segment);
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public List<IBodyInstallable<?>> addOrReplaceInstallable(@NotNull IBodyInstallable<?> installable) {

        switch (installable) {

            case AppliedAugmentInstance instance -> this.parts().addAugment(instance, true);

            case Augment augment -> {
                AppliedAugmentInstance instance = new AppliedAugmentInstance(augment, this.partManager.getDefaultPartForAugment(augment));
                this.parts().addAugment(instance, true);
            }
            case BodyPart part -> {
                return this.partManager.replaceBodyPart(part, true);
            }
            case BodySegment segment -> {
                return this.partManager.replaceSegment(segment, true);
            }
            default -> {
                return Collections.emptyList();
            }
        }

        return Collections.emptyList();

    }

    private boolean removeAugment(AppliedAugmentInstance instance) {
        this.parts().removeAugment(instance);
        abilityManager.removeAbilities(instance.augment());
        complexityManager.removePart(instance);
        powerManager.setTotalPowerCost(Math.max(0, powerManager.getTotalPowerCost() - instance.augment().powerCost()));
        onUpdate(true);
        return true;
    }

    /**
     * @param installable Installable to remove, currently only supports augments & augment instances
     * @return Returns true if installable was successfully removed
     */
    @Override
    public boolean removeInstallable(IBodyInstallable<?> installable) {
        return switch(installable) {
            case AppliedAugmentInstance instance -> removeAugment(instance);
            case Augment augment -> removeAugment(new AppliedAugmentInstance(augment, this.parts().getDefaultPartForAugment(augment)));
            default -> false;
        };
    }

    public void removeNoUpdate(AppliedAugmentInstance inst) {
        this.parts().removeAugment(inst);
        complexityManager.removePart(inst);
        abilityManager.removeAbilities(inst.augment());
    }

    private Stream<AppliedAugmentInstance> instancesOfAugmentType(Augment augment) {
        List<Augment> group = augment.groupedWithList();
        return this.parts().installedAugments()
                .stream()
                .filter(
                        p -> p.augment().equals(augment) || group.contains(p.augment())
                );
    }

    public int installedInstanceCount(Augment augment) {
        return (int) instancesOfAugmentType(augment).count();
    }
    public int installedInstanceCount(Augment augment, BodyPart part) {
        return (int) instancesOfAugmentType(augment)
                .filter(p -> p.appliedPart().equals(part))
                .count();
    }

    @Override
    public Player getEntity() {
        return player;
    }


    @Override
    public void onTick() {
        if(!this.player.isAlive()) return;
        if(!this.player.level().isClientSide) {

            CompoundTag packet = new CompoundTag();

            if(abilityManager.onTick()) {
                dirty = true;
                packet.put(abilityManager.nbtKey(), abilityManager.serialiseUpdateNBT(this.player.level().registryAccess()));
            }
            if(powerManager.onTick()) {
                dirty = true;
                packet.put(powerManager.nbtKey(), powerManager.serialiseUpdateNBT(this.player.level().registryAccess()));
            }
            if(researchManager.isDirty()) {
                dirty = true;
                packet.put(researchManager.nbtKey(), researchManager.serialiseUpdateNBT(this.player.level().registryAccess()));
                researchManager.setDirty(false);
            }

            if(dirty) {
                boolean synced;
                if(!dirtyAll) {
                    synced = this.sync(packet, false, false);
                } else {
                    synced = this.sync(true);
                }
                if(synced) {
                    dirty = false;
                    dirtyAll = false;
                }
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
            this.sync(true);
        }
    }


    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public ComplexityManager getComplexityManager() {
        return complexityManager;
    }

    public PartManager parts() {
        return partManager;
    }
    public ResearchManager getResearchManager() {
        return researchManager;
    }
    public PowerManager getPowerManager() {
        return this.powerManager;
    }

    // ---------------------------------
    //          Serialisation
    // ---------------------------------

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

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put(powerManager.nbtKey(), powerManager.serialiseNBT(provider));

        tag.put(partManager.nbtKey(), partManager.serialiseNBT(provider));
        tag.put(abilityManager.nbtKey(), abilityManager.serialiseNBT(provider));
        tag.put(complexityManager.nbtKey(), complexityManager.serialiseNBT(provider));
        tag.put(researchManager.nbtKey(), researchManager.serialiseNBT(provider));

        return tag;
    }




    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        abilityManager.clear();

        powerManager.deserialiseNBT(provider, nbt);
        partManager.deserialiseNBT(provider, nbt);

        abilityManager.deserialiseNBT(provider, nbt);
        complexityManager.deserialiseNBT(provider, nbt);
        researchManager.deserialiseNBT(provider, nbt);


        this.onUpdate(false);
    }

    @Override
    public String nbtKey() {
        return KEY;
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
    public boolean sync(boolean syncToAll) {
        if(player instanceof ServerPlayer serverPlayer) {
            CompoundTag data = this.serialiseNBT(player.level().registryAccess());
            ClientboundPlayerUpdatePacket self = ClientboundPlayerUpdatePacket.create(this.player, data, true, true);
            serverPlayer.connection.send(self);
            if (syncToAll) {
                if (player.level() instanceof ServerLevel level) {
                    ClientboundPlayerUpdatePacket other = ClientboundPlayerUpdatePacket.create(this.player, data, false, true);
                    ServerChunkCache serverchunkcache = level.getChunkSource();
                    serverchunkcache.broadcast(player, other);
                }
            }
            return true;
        }
        return false;
    }

    public boolean sync(CompoundTag data, boolean syncToAll, boolean fullUpdate) {
        if(player instanceof ServerPlayer serverPlayer) {
            ClientboundPlayerUpdatePacket self = ClientboundPlayerUpdatePacket.create(this.player, data, true, fullUpdate);
            serverPlayer.connection.send(self);
            if (syncToAll) {
                if (player.level() instanceof ServerLevel level) {
                    ClientboundPlayerUpdatePacket other = ClientboundPlayerUpdatePacket.create(this.player, data, false, fullUpdate);
                    ServerChunkCache serverchunkcache = level.getChunkSource();
                    serverchunkcache.broadcast(player, other);
                }
            }
            return true;
        }
        return false;
    }
}
