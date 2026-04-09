package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.HarpoonAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import com.thedrofdoctoring.synthetics.entities.HarpoonProjectileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class HarpoonAbility extends ActiveAbilityType<HarpoonAbilityInstance> {
    public HarpoonAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, HarpoonAbilityInstance instance) {
        createHarpoon(syntheticsPlayer, instance);
        return true;
    }

    private void createHarpoon(SyntheticsPlayer syntheticsPlayer, HarpoonAbilityInstance data) {
        Player player = syntheticsPlayer.getEntity();
        Vec3 startPos = player.position();

        float yOffset = player.getEyeHeight() * 0.5f;
        HarpoonProjectileEntity harpoon = new HarpoonProjectileEntity(player.getCommandSenderWorld(), startPos.x , startPos.y + yOffset, startPos.z, null);
        harpoon.setSpeed(data.harpoonData().speed());
        harpoon.setOwner(player);
        harpoon.setMaxConnectedTime(data.getDuration() * 20);
        harpoon.setForce(data.harpoonData().force());
        harpoon.setContactDamage((float) data.factor());
        harpoon.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, data.harpoonData().speed(), 0f);
        player.level().playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.BREEZE_SHOOT, SoundSource.PLAYERS, 0.75f, 1.5f);
        player.level().addFreshEntity(harpoon);
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof HarpoonAbilityInstance.Data activeData) {
            return Optional.of(new HarpoonAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static HarpoonAbilityInstance.Data create(double damage, ActiveAbilityOptions options, float force, float speed) {
        return new HarpoonAbilityInstance.Data(damage, options, force, speed);
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        super.addDescriptionInfo(ability, description);
        if(ability.abilityData() instanceof HarpoonAbilityInstance.Data data) {
            description.add(Component.translatable("abilities.synthetics.description.contact_damage", data.factor()).withStyle(ChatFormatting.BLUE));
            description.add(Component.translatable("abilities.synthetics.description.force", data.force()).withStyle(ChatFormatting.BLUE));
            description.add(Component.translatable("abilities.synthetics.description.projectile_speed", data.speed()).withStyle(ChatFormatting.BLUE));
        }
    }
}
