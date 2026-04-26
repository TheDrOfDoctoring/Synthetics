package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientEventHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class RocketFlightAbility extends StandardLastingAbility {
    public RocketFlightAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        Player player = syntheticsPlayer.getEntity();
        if(player.isLocalPlayer()) {
            handleFlight(player, instance.factor());
        }
        player.resetFallDistance();
        if(player.tickCount % 8 == 0 && !player.onGround()) {
            float speedRandom = player.getRandom().nextFloat() * 0.1f;
            float offsetRandom = player.getRandom().nextFloat() * 0.5f;
            player.level().addParticle(ParticleTypes.FLAME, player.position().x, player.position().y, player.position().z, speedRandom, -0.1f, speedRandom);
            player.level().addParticle(ParticleTypes.FLAME, player.position().x + offsetRandom, player.position().y, player.position().z + offsetRandom, -speedRandom, -0.1f, speedRandom);
            player.level().addParticle(ParticleTypes.FLAME, player.position().x - offsetRandom, player.position().y, player.position().z - offsetRandom, speedRandom, -0.1f, -speedRandom);
        }


        return false;
    }

    private void handleFlight(Player player, double factor) {
        SyntheticsClientEventHandler.handleRocketFlight(player, factor);
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        this.activate(syntheticsPlayer);
    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).isRocketFlight = false;
    }

    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache.get(player.getEntity()).isRocketFlight = true;
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

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }
}
