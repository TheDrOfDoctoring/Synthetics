package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;

public interface IPowerManager extends ISyncable {

    void setTotalPowerCost(int cost);
    int getTotalPowerCost();
    int getStoredPower();
    void drainPower(int amount);
    int addPower(int amount);
}
