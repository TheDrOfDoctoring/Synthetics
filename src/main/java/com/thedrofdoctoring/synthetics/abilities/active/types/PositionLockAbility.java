package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
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
        if (ability.abilityData() instanceof AbilityActiveInstance.Data activeData) {
            ActiveAbilityOptions options = activeData.options();
            description.add(Component.translatable("abilities.synthetics.description.cooldown", options.cooldown()).withStyle(ChatFormatting.BLUE));
            if (options.duration() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.duration", options.duration()).withStyle(ChatFormatting.BLUE));
            }
            if (options.powerCost() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.power_cost", options.powerCost()).withStyle(ChatFormatting.BLUE));

            }
            if (options.powerDrain() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.power_drain", options.powerDrain()).withStyle(ChatFormatting.BLUE));
            }
        }
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

    public static boolean shouldCancelKnockback(Player player, LivingKnockBackEvent event) {
        if(SyntheticsPlayerCache.get(player).lockedInPlace) {
            event.setCanceled(true);
            return true;
        }
        return false;
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
