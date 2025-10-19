package com.thedrofdoctoring.synthetics.blocks.entities.forge;

import com.thedrofdoctoring.synthetics.blocks.SyntheticForge;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.recipes.SyntheticForgeRecipe;
import com.thedrofdoctoring.synthetics.core.data.recipes.SyntheticsRecipes;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.items.BlueprintItem;
import com.thedrofdoctoring.synthetics.menus.SyntheticForgeMenu;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class SyntheticForgeBlockEntity extends BaseContainerBlockEntity implements ISyntheticForge {

    private FluidTank lavaTank = new ForgeLavaTank(2000, f -> f.is(FluidTags.LAVA));

    private @NotNull NonNullList<ItemStack> items = NonNullList.withSize(11, ItemStack.EMPTY);
    private @Nullable Player player;

    private int totalRecipeTime;
    private int recipeTime;

    protected final ContainerData dataAccess;


    public SyntheticForgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.SYNTHETIC_FORGE.get(), pos, blockState);
        this.dataAccess = new ContainerData() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> SyntheticForgeBlockEntity.this.recipeTime;
                    case 1 -> SyntheticForgeBlockEntity.this.totalRecipeTime;
                    case 2 -> SyntheticForgeBlockEntity.this.lavaTank.getFluidAmount();
                    case 3 -> SyntheticForgeBlockEntity.this.lavaTank.getCapacity();
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SyntheticForgeBlockEntity.this.recipeTime = value;
                    case 1 -> SyntheticForgeBlockEntity.this.totalRecipeTime = value;
                    case 2 -> SyntheticForgeBlockEntity.this.lavaTank.setFluid(new FluidStack(Fluids.LAVA, value));
                    case 3 -> SyntheticForgeBlockEntity.this.lavaTank.setCapacity(value);

                }

            }

            public int getCount() {
                return 4;
            }
        };
    }


    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        this.recipeTime = tag.getInt("recipe_time");
        this.totalRecipeTime = tag.getInt("max_time");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.lavaTank = lavaTank.readFromNBT(registries, tag);
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putInt("recipe_time", recipeTime);
        tag.putInt("max_time", totalRecipeTime);

        ContainerHelper.saveAllItems(tag, this.items, registries);
        lavaTank.writeToNBT(registries, tag);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.synthetics.synthetic_forge");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    public Player getActivePlayer() {
        return this.player;
    }
    public void setActivePlayer(Player player) {
        this.player = player;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level != null && this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }
    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        if (index >= 0 && index < this.items.size()) {
            this.items.set(index, stack);
        }
    }

    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return index != 0;
    }

    @NotNull
    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItem(int index, int amount) {
        return ContainerHelper.removeItem(this.items, index, amount);

    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }


    public @Nullable IFluidHandler getFluidCap(Direction side) {
        return lavaTank;
    }
    @Override
    public int getRecipeTime() {
        return recipeTime;
    }
    public FluidTank lavaTank() {
        return lavaTank;
    }


    @Override
    public SyntheticForgeBlockEntity getMaster() {
        return this;
    }
    public static <T extends BlockEntity> void serverTick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull T blockEntity) {
        if(!(blockEntity instanceof SyntheticForgeBlockEntity forge)) return;
        boolean lit = forge.recipeTime > 0;

        if(lit) {
            if (level.getGameTime() % 30 == 0) {
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            if(forge.recipeTime >= forge.totalRecipeTime && level.getServer() != null && forge.totalRecipeTime > 0) {
                CraftingInput craftinginput = CraftingInput.of(3, 3, forge.getItems().subList(2, 11));
                Optional<RecipeHolder<SyntheticForgeRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(SyntheticsRecipes.SYNTHETIC_FORGE_RECIPE.get(), craftinginput, level);
                if(optional.isPresent()) {
                    RecipeHolder<SyntheticForgeRecipe> recipe = optional.get();
                    if(recipe.value().getLavaCost() > forge.lavaTank().getFluidAmount()) {
                        forge.recipeTime = 0;
                        forge.totalRecipeTime = 0;
                        forge.setChanged();
                        return;
                    }
                    ItemStack blueprintStack = forge.getItem(1);
                    if(blueprintStack.getItem() instanceof BlueprintItem blueprint && recipe.value().requiredResearch() != null) {
                        Optional<ResearchNode> node = blueprint.getResearch(forge.getItem(1));
                        if(node.isEmpty()) {
                            blueprintStack.set(SyntheticsDataComponents.BLUEPRINT_RESEARCH, recipe.value().requiredResearch());
                        }
                    }

                    ItemStack result = recipe.value().getResult();
                    ItemStack currentResult = forge.getItem(0);
                    if(currentResult.is(result.getItem()) && currentResult.getCount() < currentResult.getMaxStackSize()) {
                        currentResult.setCount(currentResult.getCount() + result.getCount());
                        forge.setItem(0, currentResult);
                    } else {
                        forge.setItem(0, result.copy());
                    }

                    for(int i = 2; i < 11; i++) {
                        forge.getItem(i).shrink(1);
                    }
                    forge.lavaTank.drain(recipe.value().getLavaCost(), IFluidHandler.FluidAction.EXECUTE);

                }
                forge.recipeTime = 0;
                forge.totalRecipeTime = 0;
                forge.setChanged();
                return;
            }

            forge.recipeTime++;

        }
        if (lit != state.getValue(SyntheticForge.ACTIVE)) {
            state = state.setValue(SyntheticForge.ACTIVE, lit);
            level.setBlock(pos, state, 3);
        }
    }


    @NotNull
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        this.setChanged();
        return new SyntheticForgeMenu(id, player, this, dataAccess, level == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(level, worldPosition));
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }


    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
        super.handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public void setChanged() {
        if (this.level != null && !this.level.isClientSide) {
            if(level.getServer() != null) {
                CraftingInput craftinginput = CraftingInput.of(3, 3, this.items.subList(2, 11));
                Optional<RecipeHolder<SyntheticForgeRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(SyntheticsRecipes.SYNTHETIC_FORGE_RECIPE.get(), craftinginput, level);
                if(optional.isPresent()) {

                    SyntheticForgeRecipe recipe = optional.get().value();
                    ItemStack blueprint = this.items.get(1);
                    ItemStack result = this.items.get(0);
                    if(!result.equals(ItemStack.EMPTY)) {
                        if((!recipe.getResult().is(result.getItem())) || result.getCount() + recipe.getResult().getCount() > result.getMaxStackSize()) {

                            this.recipeTime = 0;
                            this.totalRecipeTime = 0;
                            return;
                        }
                    }

                    if(recipe.getLavaCost() > this.lavaTank.getFluidAmount()) {
                        this.recipeTime = 0;
                        this.totalRecipeTime = 0;
                        return;
                    }

                    if(this.recipeTime == 0) {
                        Holder<ResearchNode> node = recipe.requiredResearch();
                        if(node != null) {

                            if(blueprint.getItem() instanceof BlueprintItem blueprintItem && blueprintItem.getResearch(blueprint).isPresent()) {
                                if(!blueprintItem.getResearch(blueprint).get().equals(node.value())) {
                                    return;
                                }
                            } else if(player == null || !SyntheticsPlayer.get(player).getResearchManager().hasResearched(node.value())) {
                                return;
                            }
                        }

                        this.recipeTime = 1;
                        this.totalRecipeTime = recipe.getRecipeTime();

                    }


                } else {
                    this.recipeTime = 0;
                    this.totalRecipeTime = 0;
                }

            } else {
                this.recipeTime = 0;
                this.totalRecipeTime = 0;
            }
        }
        super.setChanged();

    }

    public class ForgeLavaTank extends FluidTank {

        public ForgeLavaTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {

            setChanged();
        }
    }

}
