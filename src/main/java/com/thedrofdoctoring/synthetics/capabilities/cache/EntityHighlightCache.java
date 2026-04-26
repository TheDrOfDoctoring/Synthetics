package com.thedrofdoctoring.synthetics.capabilities.cache;

import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.EntityHighlightAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import it.unimi.dsi.fastutil.doubles.DoubleObjectPair;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityHighlightCache {

    public boolean shouldHighlightEntities;
    private final List<DoubleObjectPair<HolderSet<EntityType<?>>>> blacklistedInRadii;

    public EntityHighlightCache() {
        this.blacklistedInRadii = new ArrayList<>();
    }

    public void rebuild(SyntheticsPlayer player) {
        AbilityManager abilities = player.getAbilityManager();
        blacklistedInRadii.clear();
        for(AbilityActiveInstance<?> active : abilities.getActiveAbilities()) {
            if(active instanceof EntityHighlightAbilityInstance instance) {
                blacklistedInRadii.add(DoubleObjectPair.of(instance.radius(), instance.entityBlacklist()));
            }
        }
    }

    public Collection<DoubleObjectPair<HolderSet<EntityType<?>>>> blacklistedInRadii() {
        return blacklistedInRadii;
    }
}
