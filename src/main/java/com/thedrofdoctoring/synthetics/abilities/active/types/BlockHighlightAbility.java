package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.LastingAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.BlockHighlightAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.BlockHighlightCache;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;


/**
 * This could do with some optimisations, for a small radius it's alright
 */
public class BlockHighlightAbility extends LastingAbilityType<BlockHighlightAbilityInstance> {
    public BlockHighlightAbility(ResourceLocation id) {
        super(id);
    }


    /**
     * Check #tick instead, avoiding per instance tick just because its more convenient to rebuild the cache that way
     */
    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, BlockHighlightAbilityInstance instance) {
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, BlockHighlightAbilityInstance instance) {

    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, BlockHighlightAbilityInstance instance) {
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).highlightCache.shouldDraw = false;

    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, BlockHighlightAbilityInstance instance) {
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).highlightCache.shouldDraw = true;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if (ability.abilityData() instanceof BlockHighlightAbilityInstance.Data activeData) {
            ActiveAbilityType.activeAbilityDescription(ability, description);
            description.add(Component.translatable("abilities.synthetics.description.radius", activeData.radius()).withStyle(ChatFormatting.BLUE));

        }
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, BlockHighlightAbilityInstance abilityData) {
        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    public static void tick(Player syntheticsPlayer) {
        if (syntheticsPlayer.tickCount % 20 == 0) {
            BlockHighlightCache cache = SyntheticsPlayerCache.get(syntheticsPlayer).highlightCache;
            if(!cache.shouldDraw) {
                return;
            }
            cache.clear();

            checkSurroundings(syntheticsPlayer, cache);
        }
    }

    public static void blockDestroyed(BlockPos pos, Player player) {
        BlockHighlightCache cache = SyntheticsPlayerCache.get(player).highlightCache;
        if(!cache.shouldDraw) {
            return;
        }
        cache.remove(pos);
    }

    private static void checkSurroundings(Player player, BlockHighlightCache cache) {
        AABB aabb = new AABB(player.getOnPos()).inflate(cache.maxRadius());
        BlockPos.betweenClosedStream(aabb)
                .filter(pos -> cache.isInWhitelist(
                        player.level().getBlockState(pos),
                        Math.sqrt(pos.distSqr(new Vec3i((int) player.position().x, (int) player.position().y, (int) player.position().z))
                        ))
                )
                .forEach(cache::addPosition);
    }

    @Override
    public void onAbilityAdded(BlockHighlightAbilityInstance instance, SyntheticsPlayer player) {
        super.onAbilityAdded(instance, player);
        SyntheticsPlayerCache.get(player.getEntity()).highlightCache.rebuildWhitelist(player);
    }

    @Override
    public void onAbilityRemoved(BlockHighlightAbilityInstance instance, SyntheticsPlayer player) {
        super.onAbilityRemoved(instance, player);
        SyntheticsPlayerCache.get(player.getEntity()).highlightCache.rebuildWhitelist(player);
    }


    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof BlockHighlightAbilityInstance.Data activeData) {
            return Optional.of(new BlockHighlightAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static BlockHighlightAbilityInstance.Data create(double radius, HolderSet<Block> whitelistedBlocks, ActiveAbilityOptions options, String titlePathOverride, ResourceLocation texturePathOverride) {
        return new BlockHighlightAbilityInstance.Data(radius, options, whitelistedBlocks, Optional.of(titlePathOverride), Optional.ofNullable(texturePathOverride));
    }

    public static BlockHighlightAbilityInstance.Data create(double radius, HolderSet<Block> whitelistedBlocks, ActiveAbilityOptions options, String titlePathOverride) {
        return new BlockHighlightAbilityInstance.Data(radius, options, whitelistedBlocks, Optional.of(titlePathOverride), Optional.empty());
    }
}
