package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.LastingAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.ShockAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.SyntheticsSounds;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class ShockAbility extends LastingAbilityType<ShockAbilityInstance> {

    public ShockAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, ShockAbilityInstance instance) {
        return !SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).hasShockActive;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, ShockAbilityInstance instance) {
        activate(syntheticsPlayer);
    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, ShockAbilityInstance instance) {
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).hasShockActive = false;
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, ShockAbilityInstance instance) {
        activate(syntheticsPlayer);
    }

    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache.get(player.getEntity()).hasShockActive = true;
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, ShockAbilityInstance abilityData) {
        activate(syntheticsPlayer);
        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData() instanceof ShockAbilityInstance.Data data) {
            description.add(Component.translatable("abilities.synthetics.description.damage", data.factor()).withStyle(ChatFormatting.BLUE));
            description.add(Component.translatable("abilities.synthetics.description.energy_drain", data.energyDrained()).withStyle(ChatFormatting.BLUE));
            description.add(Component.translatable("abilities.synthetics.description.slow_duration", data.slowDuration()).withStyle(ChatFormatting.BLUE));
        }
        ActiveAbilityType.activeAbilityDescription(ability, description);
    }

    @SubscribeEvent
    public static void onCrit(CriticalHitEvent event) {
        if(event.isCriticalHit() && SyntheticsPlayerCache.get(event.getEntity()).hasShockActive) {

            SyntheticsPlayer sourcePlayer = SyntheticsPlayer.get(event.getEntity());
            for(AbilityActiveInstance<?> inst : sourcePlayer.getAbilityManager().getToggledAbilities()) {
                if(inst instanceof ShockAbilityInstance shockAbilityInstance) {

                    if(!(event.getTarget() instanceof LivingEntity living)) return;
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, shockAbilityInstance.slowDuration(), 1));

                    if(event.getTarget() instanceof Player player) {
                        SyntheticsPlayer targetPlayer = SyntheticsPlayer.get(player);
                        int drained = targetPlayer.getPowerManager().drainPower(shockAbilityInstance.energyDrain());
                        sourcePlayer.getPowerManager().addPower(drained);
                        targetPlayer.getPowerManager().markDirty();
                        sourcePlayer.getPowerManager().markDirty();
                    }
                    living.level().playSound(null, living.getX(), living.getY(), living.getZ(), SyntheticsSounds.SHOCK_HIT, SoundSource.PLAYERS, 1.0f, 1.5f);

                    SyntheticsPlayerCache.get(event.getEntity()).hasShockActive = false;
                    return;
                }
            }


        }
    }

    @Override
    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof ShockAbilityInstance.Data activeData) {
            return Optional.of(new ShockAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static ShockAbilityInstance.Data create(double damage, ActiveAbilityOptions options, int stunDuration, int energyDrain) {
        return new ShockAbilityInstance.Data(damage, options, stunDuration, energyDrain);
    }
}
