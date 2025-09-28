package com.thedrofdoctoring.synthetics.capabilities.serialisation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface ISyncable extends ISaveData {

    CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider);

    void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt);
}
