package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.LastingAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.FlamethrowerAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import com.thedrofdoctoring.synthetics.entities.FlameProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class FlamethrowerAbility extends LastingAbilityType<FlamethrowerAbilityInstance> {

    public FlamethrowerAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, FlamethrowerAbilityInstance data) {
        createFlameProjectile(syntheticsPlayer, data);
        return true;
    }

    private void createFlameProjectile(SyntheticsPlayer syntheticsPlayer, FlamethrowerAbilityInstance data) {
        Player player = syntheticsPlayer.getEntity();
        Vec3 startPos = player.position();
        Vec3 direction = player.getViewVector(1f);
        Vec3 orthogonal = new Vec3(-direction.z, 0, direction.x);
        float yOffset = player.getEyeHeight() * 0.5f;
        FlameProjectileEntity flame = new FlameProjectileEntity(player.getCommandSenderWorld(), startPos.x + orthogonal.x, startPos.y + yOffset, startPos.z + orthogonal.z, direction);
        flame.setSpeed(1.25f);
        flame.setOwner(player);
        flame.setContactDamage((float) data.factor());
        flame.setMaxTimeAlive(data.flameLifeTime());
        player.getCommandSenderWorld().addFreshEntity(flame);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, FlamethrowerAbilityInstance data) {

        Player player = syntheticsPlayer.getEntity();
        int tickCount = player.tickCount;

        if(tickCount % 4 == 0) {
            createFlameProjectile(syntheticsPlayer, data);
        } else if(tickCount % 5 == 0) {
            Vec3 pos = player.position();
            player.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.45f, 1.25f);
        }
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, FlamethrowerAbilityInstance data) {

    }


    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer) {

    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, FlamethrowerAbilityInstance data) {


    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer player) {
        return true;
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof FlamethrowerAbilityInstance.Data activeData) {
            return Optional.of(new FlamethrowerAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static FlamethrowerAbilityInstance.Data create(double damage, ActiveAbilityOptions options, int flameLifeSpan) {
        return new FlamethrowerAbilityInstance.Data(damage, options, flameLifeSpan);
    }
}
