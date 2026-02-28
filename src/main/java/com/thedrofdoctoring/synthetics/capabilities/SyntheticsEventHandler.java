package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.abilities.passive.IAbilityEventListener;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttributes;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundUpdateDataCachePacket;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
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

}
