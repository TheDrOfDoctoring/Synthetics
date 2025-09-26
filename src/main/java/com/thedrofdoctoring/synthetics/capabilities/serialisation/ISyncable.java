package com.thedrofdoctoring.synthetics.capabilities.serialisation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface ISyncable extends ISaveData {

    // This is the data sent to other players. We don't differentiate between a player's own save data and a local player's data.

    CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider);

    void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt);
}
