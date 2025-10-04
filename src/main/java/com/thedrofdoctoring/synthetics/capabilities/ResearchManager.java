package com.thedrofdoctoring.synthetics.capabilities;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResearchManager implements IResearchManager {

    private List<ResearchNode> unlockedResearch;
    private final List<ResearchNode> toBeAdded;
    private final List<ResearchNode> toBeRemoved;


    private final SyntheticsPlayer player;
    private boolean dirty = false;

    public ResearchManager(SyntheticsPlayer player) {
        this.unlockedResearch = new ArrayList<>();
        this.toBeAdded = new ArrayList<>();
        this.toBeRemoved = new ArrayList<>();

        this.player = player;
    }


    private static final String KEY = "research_manager";

    public boolean isDirty() {
        return dirty;
    }
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    // TODO: if this is called a lot, we need to cache the stored unlocks. lots of streams!

    @Override
    public boolean hasResearched(IBodyInstallable installable) {

        for(ResearchNode node : unlockedResearch) {
            if(node.unlocked().getAllUnlocked().contains(installable)) {
                return true;
            }
        }
        return false;
    }

    private int getXpNeededForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    public void handleResearchCosts(ResearchNode node) {
        if(this.hasResearched(node)) return;
        int experienceCost = node.requirements().experienceCost();
        Optional<List<Pair<Ingredient, Integer>>> items = node.requirements().requiredItems();
        if(experienceCost > 0) {
            int accountedExperience = 0;
            Player player = this.player.getEntity();
            int currentLevel = player.experienceLevel;
            int experience = (int) (player.experienceProgress * getXpNeededForLevel(currentLevel));
            while(true) {
                int originalAmount = accountedExperience;
                accountedExperience+=experience;
                if(accountedExperience >= experienceCost) {
                    int remainder = experienceCost - originalAmount;
                    int xpForLevel = getXpNeededForLevel(player.experienceLevel);
                    player.experienceProgress = (float) (xpForLevel - remainder) / xpForLevel;
                    player.totalExperience -= remainder;
                    break;
                }
                player.experienceLevel--;
                experience = getXpNeededForLevel(player.experienceLevel);
                player.experienceProgress = 0.999f;
                if(player.experienceLevel <= 0) {
                    break;
                }

            }

        }
        if(items.isPresent()) {
            for(Pair<Ingredient, Integer> pair : items.get()) {
                Ingredient item = pair.getFirst();
                Integer count = pair.getSecond();
                List<ItemStack> playerInventory = this.player.getEntity().getInventory().items;
                int amountOfIngredient = 0;
                for(ItemStack stack : playerInventory) {
                    if(item.test(stack)) {
                        int originalAmount = amountOfIngredient;
                        amountOfIngredient += stack.getCount();
                        if(amountOfIngredient >= count) {
                            int remainder = count - originalAmount;
                            stack.shrink(remainder);
                            break;
                        } else {
                            stack.shrink(stack.getCount());
                        }
                    }
                }
            }
        }
        if(this.player.getEntity() instanceof ServerPlayer serverPlayer) {
            ClientboundSetExperiencePacket self = new ClientboundSetExperiencePacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel);
            serverPlayer.connection.send(self);
        }
    }

    public boolean canResearch(ResearchNode node) {
        if(this.hasResearched(node)) return false;
        int experienceCost = node.requirements().experienceCost();
        Optional<List<Pair<Ingredient, Integer>>> items = node.requirements().requiredItems();
        if(experienceCost > 0) {
            int accountedExperience = 0;
            Player player = this.player.getEntity();
            int currentLevel = player.experienceLevel;
            int experience = (int) (player.experienceProgress * getXpNeededForLevel(currentLevel));
            while(accountedExperience < experienceCost) {
                accountedExperience+=experience;
                if(accountedExperience > experienceCost) {
                    break;
                }
                if(currentLevel - 1 <= 0) {
                    return false;
                }
                experience = getXpNeededForLevel(currentLevel--);

            }

        }
        if(items.isPresent()) {
            for(Pair<Ingredient, Integer> pair : items.get()) {
                Ingredient item = pair.getFirst();
                Integer count = pair.getSecond();
                List<ItemStack> playerInventory = this.player.getEntity().getInventory().items;
                int amountOfIngredient = 0;
                for(ItemStack stack : playerInventory) {
                    if(item.test(stack)) {
                        amountOfIngredient += stack.getCount();
                    }
                }
                if(amountOfIngredient < count) {
                    return false;
                }
            }
        }


        return true;

    }

    @Override
    public boolean hasResearched(ResearchNode node) {

        return this.unlockedResearch.contains(node);
    }

    @Override
    public void addResearched(ResearchNode node) {
        if(!this.player.getEntity().level().isClientSide) {
            if(!this.unlockedResearch.contains(node)) {
                this.unlockedResearch.add(node);
                this.toBeAdded.add(node);
                this.dirty = true;
            }
        }
    }

    @Override
    public void removedResearched(ResearchNode node) {
        if(!this.player.getEntity().level().isClientSide) {
            if(this.unlockedResearch.contains(node)) {
                this.unlockedResearch.remove(node);
                this.toBeRemoved.add(node);
                this.dirty = true;
            }
        }
    }

    private CompoundTag serialiseResearchNodes(List<ResearchNode> nodes) {
        CompoundTag tag = new CompoundTag();

        for(int i = 0; i < nodes.size(); i++) {
            tag.putString(String.valueOf(i), nodes.get(i).id().toString());
        }
        return tag;
    }
    private List<ResearchNode> deserialiseResearchNodes(Tag tag, HolderLookup.@NotNull Provider provider) {
        HolderGetter<ResearchNode> lookup = provider.lookupOrThrow(SyntheticsData.RESEARCH_NODES);
        List<ResearchNode> nodes = new ArrayList<>();
        if(tag instanceof CompoundTag nbt) {
            int size = nbt.size();
            for(int i = 0; i < size; i++) {
                String id = nbt.getString(String.valueOf(i));
                ResearchNode node = Helper.retrieveDataObject(id, SyntheticsData.RESEARCH_NODES, lookup, false);
                if(node != null) {
                    nodes.add(node);
                }
            }
        }
        return nodes;
    }

    @Override
    public CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider) {

        CompoundTag tag = new CompoundTag();
        tag.put("remove_nodes", serialiseResearchNodes(toBeRemoved));
        tag.put("add_nodes", serialiseResearchNodes(toBeAdded));
        toBeRemoved.clear();
        toBeAdded.clear();;

        return tag;
    }

    @Override
    public void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains(nbtKey(), Tag.TAG_COMPOUND)) {
            CompoundTag tag = (CompoundTag) nbt.get(nbtKey());
            assert tag != null;
            List<ResearchNode> toBeAdded = deserialiseResearchNodes(tag.get("add_nodes"), provider);
            List<ResearchNode> toBeRemoved = deserialiseResearchNodes(tag.get("remove_nodes"), provider);
            unlockedResearch.addAll(toBeAdded);
            unlockedResearch.removeAll(toBeRemoved);
        }


    }

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {

        CompoundTag tag = new CompoundTag();
        tag.put("all_nodes", serialiseResearchNodes(unlockedResearch));
        return tag;
    }

    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains(nbtKey(), Tag.TAG_COMPOUND)) {
            CompoundTag tag = (CompoundTag) nbt.get(nbtKey());
            assert tag != null;
            this.unlockedResearch = deserialiseResearchNodes(tag.get("all_nodes"), provider);
        }
    }

    @Override
    public String nbtKey() {
        return KEY;
    }
}
