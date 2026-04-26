package com.thedrofdoctoring.synthetics.world.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.linkable.BlockLinkingPlayer;
import com.thedrofdoctoring.synthetics.capabilities.linkable.LinkableBlockLocation;
import com.thedrofdoctoring.synthetics.capabilities.linkable.LinkableContext;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLinkableUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class LinkableBlocksData extends SavedData {

    private static final String ID = "linkable_blocks_data";
    private static final UnboundedMapCodec<UUID, LinkableData> MAP_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, LinkableData.CODEC.codec());

    private final Map<UUID, LinkableData> allLinked;
    private final HolderLookup.Provider registryAccess;

    public LinkableBlocksData(HolderLookup.Provider registryAccess, Map<UUID, LinkableData> map) {
        this.allLinked = new HashMap<>(map);
        this.registryAccess = registryAccess;
    }



    public static LinkableBlocksData create(HolderLookup.Provider access) {
        return new LinkableBlocksData(access, new HashMap<>());
    }

    public static LinkableBlocksData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        return MAP_CODEC
                .decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), getOrEmpty(tag))
                .result()
                .map(mapTagPair -> new LinkableBlocksData(lookupProvider, mapTagPair.getFirst()))
                .orElseGet(() -> create(lookupProvider));
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
                .map(data -> new LinkableBlockLocation(data.position, data.context))
                .toList();
     }

    public static LinkableBlocksData getData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new Factory<>((() -> create(server.registryAccess())), LinkableBlocksData::load), ID);
    }


    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        MAP_CODEC
                .encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), allLinked)
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

    public void updateLinkedPosition(ServerLevel level, UUID linked, BlockPos newPos) {
        List<ServerPlayer> affectedPlayers = new LinkedList<>();
        if(this.allLinked.containsKey(linked)) {
            LinkableData data = this.allLinked.remove(linked);
            level.players().forEach(player -> {
                if(data.linkedPlayers.contains(player.getUUID())) {
                    affectedPlayers.add(player);
                }
            });
        }
        this.addNewLinked(linked, level, newPos);
        if(this.allLinked.containsKey(linked)) {
            LinkableData data = this.allLinked.get(linked);
            data.addPlayers(affectedPlayers);
        }
        affectedPlayers.forEach(this::syncToClient);


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
                .filter(linkableData -> player.level().dimension().equals(linkableData.position.dimension()))
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

    public void addNewLinked(UUID key, Level level, BlockPos location) {
        // just in case, if there's any linkables in the exact same location, we clear them when a new one is added at that place.
        GlobalPos globalPosition = GlobalPos.of(level.dimension(), location);
        var toRemove = this.allLinked.entrySet()
                .stream()
                .filter(data -> data.getValue().position.equals(globalPosition))
                .map(Map.Entry::getKey).toList();
        for(UUID removedUUID : toRemove) {
            this.allLinked.remove(removedUUID);
        }

        BlockEntity be = level.getBlockEntity(location);
        if(be != null) {
            Component customTitle = be.components().get(DataComponents.CUSTOM_NAME);
            Component title =  customTitle != null ?
                    MutableComponent.create(customTitle.getContents()) :
                    be.getBlockState().getBlock().getName();
            LinkableContext context = new LinkableContext(title, be.getBlockState().getBlockHolder());
            this.allLinked.computeIfAbsent(key, id -> new LinkableData(globalPosition, Optional.of(context), new HashSet<>()));
        } else {
            this.allLinked.computeIfAbsent(key, id -> new LinkableData(globalPosition, Optional.empty(), new HashSet<>()));
        }
    }


    @Override
    public boolean isDirty() {
        return true;
    }

    public record LinkableData(GlobalPos position, Optional<LinkableContext> context, Set<UUID> linkedPlayers) {

        public static final MapCodec<LinkableData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                GlobalPos.CODEC.fieldOf("position").forGetter(LinkableData::position),
                LinkableContext.CODEC.codec().optionalFieldOf("context").forGetter(LinkableData::context),
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
        public LinkableData addPlayers(List<? extends Player> players) {
            for(Player player : players) {
                this.linkedPlayers.add(player.getUUID());
            }
            return this;
        }

        public boolean isPlayerLinked(Player player) {
            return linkedPlayers.contains(player.getUUID());
        }

    }
}
