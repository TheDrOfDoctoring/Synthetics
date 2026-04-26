package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.LastingAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.EntityHighlightAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class EntityHighlightAbility extends LastingAbilityType<EntityHighlightAbilityInstance> {
    public EntityHighlightAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, EntityHighlightAbilityInstance instance) {
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, EntityHighlightAbilityInstance instance) {

    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer player, EntityHighlightAbilityInstance instance) {
        AbilityManager abilities = player.getAbilityManager();
        boolean anyActive = false;
        for(AbilityActiveInstance<?> active : abilities.getToggledAbilities()) {
            if(active instanceof EntityHighlightAbilityInstance) {
                anyActive = true;
                break;
            }
        }
        SyntheticsPlayerCache.get(player.getEntity()).entityHighlightcache.shouldHighlightEntities = anyActive;
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, EntityHighlightAbilityInstance instance) {
        activate(syntheticsPlayer);
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).entityHighlightcache.rebuild(syntheticsPlayer);
    }

    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache.get(player.getEntity()).entityHighlightcache.shouldHighlightEntities = true;
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, EntityHighlightAbilityInstance abilityData) {
        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof EntityHighlightAbilityInstance.Data activeData) {
            return Optional.of(new EntityHighlightAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static EntityHighlightAbilityInstance.Data create(double radius, HolderSet<EntityType<?>> blacklistedEntities, ActiveAbilityOptions options, String titlePathOverride, ResourceLocation texturePathOverride) {
        return new EntityHighlightAbilityInstance.Data(radius, options, blacklistedEntities, Optional.of(titlePathOverride), Optional.ofNullable(texturePathOverride));
    }

    public static EntityHighlightAbilityInstance.Data create(double radius, HolderSet<EntityType<?>> blacklistedEntities, ActiveAbilityOptions options, String titlePathOverride) {
        return new EntityHighlightAbilityInstance.Data(radius, options, blacklistedEntities, Optional.of(titlePathOverride), Optional.empty());
    }
}
