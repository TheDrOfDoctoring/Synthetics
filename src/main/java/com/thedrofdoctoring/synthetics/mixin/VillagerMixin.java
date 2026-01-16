package com.thedrofdoctoring.synthetics.mixin;

import com.thedrofdoctoring.synthetics.core.SyntheticsAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    @Shadow
    public abstract VillagerData getVillagerData();

    public VillagerMixin(EntityType<? extends AbstractVillager> vil, Level level) {
        super(vil, level);
    }

    @Inject(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;getPlayerReputation(Lnet/minecraft/world/entity/player/Player;)I"))
    private void updateSpecialPrices(Player player, CallbackInfo ci) {
            double mult = -player.getAttributeValue(SyntheticsAttributes.TRADE_PRICES_MULTIPLIER) + 1;
            for (MerchantOffer merchantoffer1 : this.getOffers()) {
                int diff = mult != 0 ? (int) Math.floor((merchantoffer1.getBaseCostA().getCount()) * (mult)) : 0;
                merchantoffer1.addToSpecialPriceDiff(diff);
            }
    }
}
