package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;

import java.util.List;

public class PositionLockAbility extends StandardLastingAbility {
    public PositionLockAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        return false;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        ActiveAbilityType.activeAbilityDescription(ability, description);
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        this.activate(syntheticsPlayer);

    }

    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache.get(player.getEntity()).lockedInPlace = true;
    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).lockedInPlace = false;
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        this.activate(syntheticsPlayer);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        this.activate(syntheticsPlayer);
        return true;
    }

    public static boolean shouldCancelKnockback(Player player) {
        return SyntheticsPlayerCache.get(player).lockedInPlace;
    }

    public static boolean shouldCancelExplosionKnockback(Player player, ExplosionKnockbackEvent event) {
        if(SyntheticsPlayerCache.get(player).lockedInPlace) {
            event.setKnockbackVelocity(Vec3.ZERO);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }
}
