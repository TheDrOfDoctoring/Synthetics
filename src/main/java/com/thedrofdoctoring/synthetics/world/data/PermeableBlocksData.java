package com.thedrofdoctoring.synthetics.world.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.entities.PermeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PermeableBlocksData extends SavedData {
    // Maybe serialise an AABB instead of the block positions
    // We might also want to look into, instead of replacing the original blocks, mixining the section compiler, collision checks etc, and rebuild the chunk mesh, completely safe for BEs
    private static final String ID = "permeable_blocks_data";
    private static final UnboundedMapCodec<UUID, List<PermeableBlockData>> MAP_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, PermeableBlockData.CODEC.codec().listOf());

    private final Map<UUID, List<PermeableBlockData>> allPermeable;

    public PermeableBlocksData(Map<UUID, List<PermeableBlockData>> map) {
        this.allPermeable = new HashMap<>();
        for(var entry : map.entrySet()) {
            this.allPermeable.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    public static @Nullable PermeableBlocksData getData(MinecraftServer server, ResourceKey<Level> levelKey) {
        ServerLevel level = server.getLevel(levelKey);
        if(level != null) {
            return level.getDataStorage().computeIfAbsent(new Factory<>(PermeableBlocksData::create, PermeableBlocksData::load), ID);
        }
        return null;
    }

    public static PermeableBlocksData create() {
        return new PermeableBlocksData(new HashMap<>());
    }

    public static PermeableBlocksData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        return MAP_CODEC
                .decode(NbtOps.INSTANCE, getOrEmpty(tag))
                .result()
                .map(mapTagPair -> new PermeableBlocksData(mapTagPair.getFirst()))
                .orElseGet(PermeableBlocksData::create);
    }

    private static Tag getOrEmpty(CompoundTag tag) {
        Tag t = tag.get(ID);
        if(t instanceof CompoundTag) {
            return t;
        } else {
            return new CompoundTag();
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        MAP_CODEC
                .encodeStart(NbtOps.INSTANCE, allPermeable)
                .ifSuccess(result -> compoundTag.put(ID, result))
                .ifError( (err) -> Synthetics.LOGGER.warn("Failed to serialise permeable block data, {}", err));
        return compoundTag;
    }

    public void tick(ServerLevel level, int increment) {
        List<BlockPos> toRemove = incrementTimers(increment);
        this.reset(level, toRemove);
    }



    private List<BlockPos> incrementTimers(int increment) {
        List<BlockPos> toRemove = new ArrayList<>(1250);
        for(var entry : allPermeable.entrySet()) {
            Iterator<PermeableBlockData> iter = entry.getValue().iterator();
            while(iter.hasNext()) {
                PermeableBlockData data = iter.next();
                int newTimer = data.timer - increment;
                if(newTimer <= 0) {
                    toRemove.addAll(data.positions);
                    iter.remove();
                } else {
                    data.setTimer(newTimer);
                }
            }
        }
        return toRemove;
    }

    private void reset(Level level, List<BlockPos> toRemove) {
        for(BlockPos pos : toRemove) {
            BlockEntity be = level.getBlockEntity(pos);
            if(be instanceof PermeableBlockEntity permeableBlockEntity) {
                permeableBlockEntity.reset();
            }
        }
    }

    public boolean resetForUUID(Level level, UUID uuid) {
        List<PermeableBlockData> data = this.allPermeable.getOrDefault(uuid, Collections.emptyList());
        if(!data.isEmpty()) {
           reset(level, data.removeFirst().positions());
           return true;
        }
        return false;
    }

    @Override
    public boolean isDirty() {
        return true;
    }


    public void addData(UUID uuid, PermeableBlockData data) {
        this.allPermeable.compute(uuid,
                ((uuid1, permeableBlockData) -> {
                    if(permeableBlockData == null) {
                        List<PermeableBlockData> dataList = new ArrayList<>();
                        dataList.add(data);
                        return dataList;
                    } else {
                        permeableBlockData.add(data);
                        return permeableBlockData;
                    }
                })
        );
    }

    public static class PermeableBlockData {

        private final List<BlockPos> positions;
        private int timer;

        public PermeableBlockData(List<BlockPos> positions, int timer) {
            this.positions = positions;
            this.timer = timer;
        }

        public List<BlockPos> positions() {
            return positions;
        }

        public int timer() {
            return timer;
        }

        public void setTimer(int timer) {
            this.timer = timer;
        }

        public static final MapCodec<PermeableBlockData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockPos.CODEC.listOf().fieldOf("positions").forGetter(PermeableBlockData::positions),
                Codec.INT.fieldOf("timer").forGetter(PermeableBlockData::timer)
        ).apply(instance, PermeableBlockData::new));
    }
}
