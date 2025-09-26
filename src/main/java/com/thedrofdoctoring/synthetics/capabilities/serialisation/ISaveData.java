package com.thedrofdoctoring.synthetics.capabilities.serialisation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface ISaveData {

    CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider);

    void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt);

    String nbtKey();
}
