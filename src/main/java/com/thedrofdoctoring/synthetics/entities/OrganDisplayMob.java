package com.thedrofdoctoring.synthetics.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class OrganDisplayMob extends Skeleton {
    public OrganDisplayMob(EntityType<OrganDisplayMob> type, Level level) {
        super(type, level);
    }
}
