package com.thedrofdoctoring.synthetics.core.data.types.body.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.AbilityWheel;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.ISlotEntry;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.AbilityRadialSlot;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

public record Ability(AbilityType abilityType, AbilityData abilityData, ResourceLocation id) implements ISlotEntry<AbilityRadialSlot> {

    public static final MapCodec<Ability> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityType.CODEC.fieldOf("ability_type").forGetter(Ability::abilityType),
            AbilityData.DISPATCH_CODEC.fieldOf("ability_data").forGetter(Ability::abilityData),
            ResourceLocation.CODEC.fieldOf("id").forGetter(Ability::id)
    ).apply(instance, Ability::new));

    public static Ability create(AbilityType abilityType, AbilityData data, ResourceLocation id) {
        return new Ability(abilityType, data, id);
    }

    public static final Codec<HolderSet<Ability>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.ABILITIES, CODEC.codec());


    @Override
    public AbilityRadialSlot addToSlot(GenericRadialMenu<AbilityRadialSlot> menu, AbilityRadialSlot slot, Level level) {

        LinkedList<Holder<Ability>> slotData = slot.slotData().abilities().stream().collect(Collectors.toCollection(LinkedList::new));

        var lookupOpt = level.registryAccess().lookup(SyntheticsData.ABILITIES);
        if(lookupOpt.isPresent()) {
            ResourceKey<Ability> selfKey = ResourceKey.create(SyntheticsData.ABILITIES, id);
            Optional<Holder.Reference<Ability>> self = lookupOpt.get().get(selfKey);
            if(self.isPresent()) {
                slotData.add(self.get());
                return new AbilityRadialSlot(menu, new AbilityWheel.Slot(HolderSet.direct(slotData)));
            }
            return slot;
        }
        Synthetics.LOGGER.warn("Failed to add ability {} to slot", id);
        return slot;
    }

    public ResourceLocation textureLocation() {
        ResourceLocation id = abilityType.getAbilityID();
        return id.withPath("textures/abilities/" + id.getPath() + ".png");
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Ability> STREAM_CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, Ability::abilityType,
            AbilityData.DISPATCH_STREAM, Ability::abilityData,
            ResourceLocation.STREAM_CODEC, Ability::id,
            Ability::new);
}
