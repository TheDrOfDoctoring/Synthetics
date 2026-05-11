package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.abilities.active.types.PositionLockAbility;
import com.thedrofdoctoring.synthetics.abilities.passive.IAbilityEventListener;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.types.generators.ShockAbsorberAbility;
import com.thedrofdoctoring.synthetics.config.CommonConfig;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttributes;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.AppliedAugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundUpdateDataCachePacket;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class SyntheticsEventHandler {


    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        SyntheticsPlayer.get(event.getEntity()).onTick();
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ClientboundUpdateDataCachePacket packet = ClientboundUpdateDataCachePacket.getInstance();
        if (event.getPlayer() != null) {
            event.getPlayer().connection.send(packet);
        } else {
            event.getPlayerList().getPlayers().forEach(p -> p.connection.send(packet));
        }

    }

    @SubscribeEvent
    public static void onOrbPickup(PlayerXpEvent.PickupXp event) {
        float experienceGainMult = (float) event.getEntity().getAttributeValue(SyntheticsAttributes.EXPERIENCE_GAIN);
        event.getOrb().value = (int) (event.getOrb().value * experienceGainMult);
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingDamageEvent.Pre event) {
        if(event.getEntity() instanceof Player player) {
            SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);
            for(IntObjectPair<AbilityPassiveInstance<?>> instance : syntheticsPlayer.getAbilityManager().getPassiveAbilitiesPairs()) {
                if(instance.second().type() instanceof IAbilityEventListener<?> listener) {
                    listener.onDamage(event, instance.second(), instance.firstInt(), syntheticsPlayer);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockbackEvent(LivingKnockBackEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(PositionLockAbility.shouldCancelKnockback(player)) {
                event.setCanceled(true);
                return;
            }
        }
        if(event.getEntity() instanceof ServerPlayer player) {
            SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);
            for(IntObjectPair<AbilityPassiveInstance<?>> instance : syntheticsPlayer.getAbilityManager().getPassiveAbilitiesPairs()) {
                if(instance.second().type() instanceof ShockAbsorberAbility listener) {
                    listener.onKnockback(instance.second(), instance.firstInt(), syntheticsPlayer, event);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onExplosionKnockback(ExplosionKnockbackEvent event) {
        if(event.getAffectedEntity() instanceof Player player) {
            if(PositionLockAbility.shouldCancelExplosionKnockback(player, event)) {
                return;
            }
        }
        if(event.getAffectedEntity() instanceof ServerPlayer player) {
            SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);
            for(IntObjectPair<AbilityPassiveInstance<?>> instance : syntheticsPlayer.getAbilityManager().getPassiveAbilitiesPairs()) {
                if(instance.second().type() instanceof ShockAbsorberAbility listener) {
                    listener.onExplosionKnockback(instance.second(), instance.firstInt(), syntheticsPlayer, event);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(CommonConfig.dropInstalledOnDeath.get()) {
                dropInstalledParts(event, player);
            }
            deactivateAbilities(player);
        }

    }

    private static void deactivateAbilities(Player player) {
        AbilityManager manager = SyntheticsPlayer.get(player).getAbilityManager();
        manager.deactivateAll();
    }


    private static void dropInstalledParts(LivingDropsEvent event, Player player) {
        SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);
        Level level = player.level();
        Vec3 pos = player.position();
        for(AppliedAugmentInstance inst : syntheticsPlayer.parts().installedAugments()) {
            syntheticsPlayer.removeNoUpdate(inst);
            ItemStack stack = inst.augment().createDefaultItemStack(player.registryAccess());
            event.getDrops().add(new ItemEntity(level, pos.x, pos.y, pos.z, stack));
        }
        for(BodyPart part : syntheticsPlayer.parts().installedBodyParts()) {
            BodyPart defaultPart = part.type().value().defaultPart().value();
            if(part.equals(defaultPart)) continue;
            syntheticsPlayer.parts().replaceBodyPart(defaultPart, false);
            ItemStack stack = part.createDefaultItemStack(player.registryAccess());
            event.getDrops().add(new ItemEntity(level, pos.x, pos.y, pos.z, stack));
        }
        for(BodySegment segment : syntheticsPlayer.parts().installedSegments()) {
            BodySegment defaultSegment = segment.type().value().defaultSegment().value();
            if(segment.equals(defaultSegment)) continue;
            syntheticsPlayer.parts().replaceSegment(defaultSegment, false);
            ItemStack stack = segment.createDefaultItemStack(player.registryAccess());
            event.getDrops().add(new ItemEntity(level, pos.x, pos.y, pos.z, stack));
        }
        syntheticsPlayer.onUpdate(true);
    }

}
