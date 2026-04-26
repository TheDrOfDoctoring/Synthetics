package com.thedrofdoctoring.synthetics.abilities.passive.types.generators;

import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.types.StandardPassiveAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;

public class ShockAbsorberAbility extends StandardPassiveAbility {

    private static final int BASE_ENERGY = 12500;

    public ShockAbsorberAbility(ResourceLocation id) {
        super(id);
    }

    public void onKnockback(AbilityPassiveInstance<?> instance, int count, SyntheticsPlayer player, LivingKnockBackEvent event) {
        if(!instance.isEnabled()) return;

        int addedEnergy = (int) (event.getStrength() * BASE_ENERGY * count * instance.factor());
        float reducedAmount = (float) (0.1 * count * instance.factor());
        event.setStrength(event.getStrength() * (1 - reducedAmount));
        player.getPowerManager().addPower(addedEnergy);
        player.getPowerManager().markDirty();
    }
    public void onExplosionKnockback(AbilityPassiveInstance<?> instance, int count, SyntheticsPlayer player, ExplosionKnockbackEvent event) {
        if(!instance.isEnabled()) return;
        double magnitude = event.getKnockbackVelocity().lengthSqr();
        int addedEnergy = (int) (magnitude * BASE_ENERGY * count * instance.factor());
        float reducedAmount = (float) (0.1 * count * instance.factor());
        event.setKnockbackVelocity(event.getKnockbackVelocity().scale(1 - reducedAmount));
        player.getPowerManager().addPower(addedEnergy);
        player.getPowerManager().markDirty();
    }
}
