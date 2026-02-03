package com.thedrofdoctoring.synthetics.capabilities.linkable;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;


public class BlockLinkingPlayer {

    private static final String KEY = "block_linking_player";
    public static final ResourceLocation MANAGER_KEY = Synthetics.rl(KEY);

    private List<LinkableBlockLocation> linkableData = Collections.emptyList();
    private Map<ResourceKey<Level>, List<BlockPos>> dimensionToLinksMap = Collections.emptyMap();

    public List<LinkableBlockLocation> linkableData() {
        return linkableData;
    }

    public List<BlockPos> getLinkedPositionsInDimension(ResourceKey<Level> level) {
        return dimensionToLinksMap.getOrDefault(level, Collections.emptyList());
    }

    public void setLinkableData(List<LinkableBlockLocation> data) {
        this.linkableData = data;
        this.dimensionToLinksMap = linkableData
                .stream()
                .collect(
                        groupingBy(LinkableBlockLocation::dimension,
                                mapping(LinkableBlockLocation::position, toList())
                        )
                );
    }

    public boolean isLinkedToPos(BlockPos pos, Level level) {
        return dimensionToLinksMap
                .getOrDefault(level.dimension(), Collections.emptyList())
                .contains(pos);
    }

    public static BlockLinkingPlayer get(Player player) {
        return player.getData(SyntheticsAttachments.BlOCK_LINKING_PLAYER);
    }

    public static class Factory implements Function<IAttachmentHolder, BlockLinkingPlayer> {

        @Override
        public BlockLinkingPlayer apply(IAttachmentHolder holder) {
            if (holder instanceof Player) {
                return new BlockLinkingPlayer();
            }
            throw new IllegalArgumentException("Cannot create Block Linking Player attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }

}
