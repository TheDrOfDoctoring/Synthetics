package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLeapPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class LeapAbility extends StandardLastingAbility {

    public LeapAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        if(syntheticsPlayer.getEntity() instanceof ServerPlayer player && player.onGround()) {
            player.connection.send(new ClientboundLeapPacket(data.factor()));
            player.level().playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.BREEZE_SHOOT, SoundSource.PLAYERS, 0.75f, 0.75f);
            return true;
        }

        return false;
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        syntheticsPlayer.getEntity().resetFallDistance();
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {

    }


    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {

    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {


    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer player) {
        return true;
    }
}
