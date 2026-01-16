package com.thedrofdoctoring.synthetics.world.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class IDSavedData extends SavedData {

    private static final String ID = "synthetics_level_uuid";


    private final UUID uuid;

    public static IDSavedData create() {
        return new IDSavedData(UUID.randomUUID());
    }

    public IDSavedData(UUID uuid) {
        this.uuid = uuid;
    }

    public static IDSavedData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if(tag.contains(ID)) {
            return new IDSavedData(tag.getUUID(ID));
        }
        return create();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        compoundTag.putUUID(ID, uuid);
        return compoundTag;
    }

    public static UUID getData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new Factory<>(IDSavedData::create, IDSavedData::load), ID).uuid;
    }

    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
