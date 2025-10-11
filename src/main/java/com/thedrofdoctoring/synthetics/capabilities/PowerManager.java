package com.thedrofdoctoring.synthetics.capabilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class PowerManager implements IPowerManager {

    private static final String KEY = "power_manager";
    public static final int BATTERY_STORAGE_BASE = 100;

    private int consumedPowerPerTick;
    private int storedPower;
    private int maxPower;
    private final SyntheticsPlayer player;

    private boolean dirty;

    public PowerManager(SyntheticsPlayer player) {
        this.player = player;
    }

    @Override
    public void setTotalPowerCost(int cost) {
        this.consumedPowerPerTick = cost;
    }

    @Override
    public int getTotalPowerCost() {
        return consumedPowerPerTick;
    }

    @Override
    public int getStoredPower() {
        return storedPower;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = Math.max(0, this.maxPower = maxPower);
    }

    @Override
    public void drainPower(int amount) {
        this.storedPower = Math.max(0, storedPower - amount);
    }

    @Override
    public int addPower(int amount) {
        int receive = Mth.clamp(this.maxPower - amount, 0, amount);
        this.storedPower = Math.min(receive + storedPower, maxPower);
        return receive;
    }

    public void markDirty() {
        this.dirty = true;
    }
    @Override
    public CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("stored_power", storedPower);
        tag.putInt("total_power_cost", consumedPowerPerTick);
        tag.putInt("max_stored_power", maxPower);

        return tag;
    }

    public boolean onTick() {
        if(this.consumedPowerPerTick > 0) {
            this.drainPower(this.consumedPowerPerTick);
        }
        if(dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    @Override
    public void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains(nbtKey(), Tag.TAG_COMPOUND)) {
            CompoundTag power = (CompoundTag) nbt.get(nbtKey());
            if(power == null) return;
            this.storedPower = power.getInt("stored_power");
            this.consumedPowerPerTick = power.getInt("total_power_cost");
            this.maxPower = power.getInt("max_stored_power");
        }

    }

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("stored_power", storedPower);
        return tag;
    }

    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains(nbtKey(), Tag.TAG_COMPOUND)) {
            CompoundTag power = (CompoundTag) nbt.get(nbtKey());
            if(power == null) return;
            this.storedPower = power.getInt("stored_power");
        }
    }

    @Override
    public String nbtKey() {
        return KEY;
    }


}
