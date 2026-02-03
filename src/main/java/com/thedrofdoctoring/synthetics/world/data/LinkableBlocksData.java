package com.thedrofdoctoring.synthetics.world.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.linkable.BlockLinkingPlayer;
import com.thedrofdoctoring.synthetics.capabilities.linkable.LinkableBlockLocation;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLinkableUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class LinkableBlocksData extends SavedData {

    private static final String ID = "linkable_blocks_data";
    private static final UnboundedMapCodec<UUID, LinkableData> MAP_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, LinkableData.CODEC.codec());

    private final Map<UUID, LinkableData> allLinked;

    public LinkableBlocksData(Map<UUID, LinkableData> map) {
        this.allLinked = new HashMap<>(map);
    }

    public static LinkableBlocksData create() {
        return new LinkableBlocksData(new HashMap<>());
    }

    public static LinkableBlocksData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        return MAP_CODEC
                .decode(NbtOps.INSTANCE, getOrEmpty(tag))
                .result()
                .map(mapTagPair -> new LinkableBlocksData(mapTagPair.getFirst()))
                .orElseGet(LinkableBlocksData::create);
    }

    private static Tag getOrEmpty(CompoundTag tag) {
        Tag t = tag.get(ID);
        if(t instanceof CompoundTag) {
            return t;
        } else {
            return new CompoundTag();
        }
    }


    private static Predicate<LinkableData> isPlayerLinked(Player player) {
        return (linkableData -> linkableData.isPlayerLinked(player));
    }

     public List<LinkableBlockLocation> getAllLocationsLinkedToPlayer(Player player) {
        return this.allLinked.values()
                .stream()
                .filter(isPlayerLinked(player))
                .map(data -> new LinkableBlockLocation(data.location, data.dimension))
                .toList();
     }

    public static LinkableBlocksData getData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new Factory<>(LinkableBlocksData::create, LinkableBlocksData::load), ID);
    }


    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        MAP_CODEC
                .encodeStart(NbtOps.INSTANCE, allLinked)
                .ifSuccess(result -> compoundTag.put(ID, result))
                .ifError( (err) -> Synthetics.LOGGER.warn("Failed to serialise linkable data, {}", err));
        return compoundTag;
    }

    public void addPlayer(ServerPlayer player, UUID linked) {
        this.allLinked.computeIfPresent(linked, (id, data) -> data.addPlayer(player));
        syncToClient(player);
    }

    public void removePlayer(ServerPlayer player, UUID linked) {
        this.allLinked.computeIfPresent(linked, (id, data) -> data.removePlayer(player));
        syncToClient(player);
    }

    public void syncToClient(ServerPlayer player) {
        ClientboundLinkableUpdatePacket self = new ClientboundLinkableUpdatePacket(this.getAllLocationsLinkedToPlayer(player));
        BlockLinkingPlayer.get(player).setLinkableData(self.linkingLocations());
        player.connection.send(self);
    }

    // This is inefficient, ideally we would only sync what has changed, but this is only a problem to solve if it becomes a problem
    public List<ServerPlayer> removeLinked(ServerLevel level, UUID linked) {
        List<ServerPlayer> affectedPlayers = new LinkedList<>();
        if(this.allLinked.containsKey(linked)) {
            LinkableData data = this.allLinked.remove(linked);
            level.players().forEach(player -> {
                if(data.linkedPlayers.contains(player.getUUID())) {
                    syncToClient(player);
                    affectedPlayers.add(player);
                }
            });
        }
        return affectedPlayers;

    }

    public boolean isAlreadyLinked(UUID key) {
        return this.allLinked.containsKey(key);
    }

    public int getLinkedPlayerCount(UUID linkedKey) {
        LinkableData data = this.allLinked.get(linkedKey);
        return data == null ? -1 : data.linkedPlayers.size();
    }

    public List<LinkableData> linkedToPlayer(Player player) {
        return this.allLinked.values()
                .stream()
                .filter(isPlayerLinked(player))
                .toList();
    }
    public List<LinkableData> linkedToPlayerInDimension(Player player) {
        return this.allLinked.values()
                .stream()
                .filter(linkableData -> player.level().dimension().equals(linkableData.dimension))
                .filter(isPlayerLinked(player))
                .toList();
    }

    public boolean isPlayerLinkedToLinkable(Player player, UUID uuid) {
        if(this.allLinked.containsKey(uuid)) {
            return this.allLinked.get(uuid)
                    .linkedPlayers()
                    .contains(player.getUUID());
        }
        return false;
    }

    public void addNewLinked(UUID key, ResourceKey<Level> dimensionID, BlockPos location) {
        // just in case, if there's any linkables in the exact same location, we clear them when a new one is added at that place.
        var toRemove = this.allLinked.entrySet()
                .stream()
                .filter(data -> data.getValue().dimension.equals(dimensionID) && data.getValue().location().equals(location))
                .map(Map.Entry::getKey).toList();
        for(UUID removedUUID : toRemove) {
            this.allLinked.remove(removedUUID);
        }
        this.allLinked.computeIfAbsent(key, id -> new LinkableData(dimensionID, location, new HashSet<>()));
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public record LinkableData(ResourceKey<Level> dimension, BlockPos location, Set<UUID> linkedPlayers) {

        public static final MapCodec<LinkableData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(LinkableData::dimension),
                BlockPos.CODEC.fieldOf("position").forGetter(LinkableData::location),
                UUIDUtil.CODEC_SET.fieldOf("players").forGetter(LinkableData::linkedPlayers)
        ).apply(instance, LinkableData::new));

        public LinkableData removePlayer(Player player) {
            this.linkedPlayers.remove(player.getUUID());
            return this;
        }

        public LinkableData addPlayer(Player player) {
            this.linkedPlayers.add(player.getUUID());
            return this;
        }

        public boolean isPlayerLinked(Player player) {
            return linkedPlayers.contains(player.getUUID());
        }

    }
}
