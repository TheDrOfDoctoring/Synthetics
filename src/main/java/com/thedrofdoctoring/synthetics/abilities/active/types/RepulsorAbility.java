package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardActiveAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RepulsorAbility extends StandardActiveAbility {
    public RepulsorAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        Player player = syntheticsPlayer.getEntity();
        Vec3 startPos = player.position();
        Vec3 direction = player.getViewVector(1f);
        AABB aabb = new AABB(new Vec3(startPos.x, startPos.y - 1f, startPos.z),
                startPos.add(direction.multiply(10f, 5f, 10f))
        ).inflate(0, 0.5f, 0);
        List<Entity> repulsedEntities = player.level().getEntities(player, aabb).stream().filter(entity -> entity.distanceToSqr(startPos) < 750).toList();

        pushEntities(repulsedEntities, startPos, direction, abilityData.factor());
        if(!repulsedEntities.isEmpty()) {
            player.level().playSound(null, startPos.x, startPos.y, startPos.z, SoundEvents.BREEZE_WIND_CHARGE_BURST, SoundSource.PLAYERS, 0.75f, 5f);
            return true;
        }
        return false;
    }

    private void pushEntities(List<Entity> repulsedEntities, Vec3 startPos, Vec3 direction, double factor) {
        for(Entity entity : repulsedEntities) {
            entity.hurtMarked = true;
            float strength = (float) Math.min(factor * 3f, (float)
                    (factor * ( 1 / (600 * (Mth.square(entity.position().distanceToSqr(startPos) / 500))))));
            Vec3 delta = entity.getDeltaMovement();
            entity.setDeltaMovement(delta.x + (direction.x * strength), delta.y + (direction.y * strength / 1.5f), delta.z + (direction.z * strength));
        }
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }
}
