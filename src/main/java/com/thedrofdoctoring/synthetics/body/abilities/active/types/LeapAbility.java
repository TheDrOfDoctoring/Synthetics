package com.thedrofdoctoring.synthetics.body.abilities.active.types;

import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticLastingAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLeapPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LeapAbility extends SyntheticLastingAbilityType {

    public LeapAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, double factor) {
        if(syntheticsPlayer.getEntity() instanceof ServerPlayer player) {
            player.connection.send(new ClientboundLeapPacket(factor));
        }

        return true;
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, double factor) {
        syntheticsPlayer.getEntity().resetFallDistance();
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, double factor) {

    }


    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer) {

    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, double factor) {


    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer player) {
        return true;
    }
}
