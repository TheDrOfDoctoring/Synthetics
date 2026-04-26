package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardActiveAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TeleportAbility extends StandardActiveAbility {
    public TeleportAbility(ResourceLocation id) {
        super(id);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        if(!(syntheticsPlayer.getEntity() instanceof ServerPlayer serverPlayer)) return false;
        if(serverPlayer.getCamera() != syntheticsPlayer.getEntity()) return false;
        Player player = syntheticsPlayer.getEntity();
        HitResult hit = Helper.getPlayerLookingSpot(player, abilityData.factor());
        double ox = player.getX();
        double oy = player.getY();
        double oz = player.getZ();
        if (hit.getType() == HitResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 1, 1);
            return false;
        }
        Level level = player.level();
        BlockPos hitPos = null;
        if (hit.getType() == HitResult.Type.BLOCK) {
            if (level.getBlockState(((BlockHitResult) hit).getBlockPos()).blocksMotion()) {
                hitPos = ((BlockHitResult) hit).getBlockPos().above();
            }
        } else if(hit.getType() == HitResult.Type.ENTITY) {
            if (level.getBlockState(((EntityHitResult) hit).getEntity().blockPosition()).blocksMotion()) {
                hitPos = ((EntityHitResult) hit).getEntity().blockPosition();
            }
        }

        if (hitPos != null) {
            player.setPos(hitPos.getX() + 0.5, hitPos.getY() + 0.1, hitPos.getZ() + 0.5);
            if (level.containsAnyLiquid(player.getBoundingBox()) || !level.isUnobstructed(player)) {
                hitPos = null;
            }
        }


        if (hitPos == null) {
            player.setPos(ox, oy, oz);
            player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 1, 1);
            return false;
        }
        serverPlayer.removeVehicle();
        serverPlayer.teleportTo(hitPos.getX() + 0.5, hitPos.getY() + 0.1, hitPos.getZ() + 0.5);

        level.playSound(null, ox,oy,oz, SoundEvents.BREEZE_SHOOT, SoundSource.PLAYERS, 0.75f, 0.75f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.75f, 1.75f);

        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }


}
