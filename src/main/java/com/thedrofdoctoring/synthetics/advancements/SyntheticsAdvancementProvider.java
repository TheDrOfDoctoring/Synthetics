package com.thedrofdoctoring.synthetics.advancements;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.advancements.criterion.AugmentInstalledCriterion;
import com.thedrofdoctoring.synthetics.advancements.criterion.GenericSyntheticsCriterion;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SyntheticsAdvancementProvider extends AdvancementProvider {
    public SyntheticsAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new SyntheticsAdvancements()));
    }

    private interface SyntheticsAdvancementSubProvider {
        void generate(@NotNull AdvancementHolder root, @NotNull HolderLookup.Provider holderProvider, @NotNull Consumer<AdvancementHolder> consumer);
    }

    private static class SyntheticsAdvancements implements AdvancementGenerator {

        private final List<SyntheticsAdvancementSubProvider> subProvider = List.of(new MainAdvancements());

        @Override
        public void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(SyntheticsItems.CREATIVE_TAB_ICON_ITEM.get(), Component.translatable("advancement.synthetics"), Component.translatable("advancement.synthetics.desc"), Synthetics.rl("textures/gui/research/background.png"), AdvancementType.TASK, false, false, false)
                    .addCriterion("main", InventoryChangeTrigger.TriggerInstance.hasItems(SyntheticsBlocks.RESEARCH_TABLE.get()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, Synthetics.MODID + ":main/root");
            this.subProvider.forEach(p -> p.generate(root, provider, consumer));
        }
    }

    private static class MainAdvancements implements SyntheticsAdvancementSubProvider {

        @Override
        public void generate(@NotNull AdvancementHolder root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<AdvancementHolder> consumer) {
            AdvancementHolder syntheticForge = Advancement.Builder.advancement()
                    .display(SyntheticsBlocks.SYNTHETIC_FORGE, Component.translatable("advancement.synthetics.synthetic_forge"), Component.translatable("advancement.synthetics.synthetic_forge.desc"), null, AdvancementType.TASK, false, false, false)
                    .parent(root)
                    .addCriterion("main", InventoryChangeTrigger.TriggerInstance.hasItems(SyntheticsBlocks.SYNTHETIC_FORGE.get()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, Synthetics.MODID + ":main/synthetic_forge");
            AdvancementHolder augmentationChamber = Advancement.Builder.advancement()
                    .display(SyntheticsBlocks.AUGMENTATION_CHAMBER, Component.translatable("advancement.synthetics.augmentation_chamber"), Component.translatable("advancement.synthetics.augmentation_chamber.desc"), null, AdvancementType.TASK, false, false, false)
                    .parent(root)
                    .addCriterion("main", InventoryChangeTrigger.TriggerInstance.hasItems(SyntheticsBlocks.AUGMENTATION_CHAMBER.get()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, Synthetics.MODID + ":main/augmentation_chamber");
            AdvancementHolder installAugment = Advancement.Builder.advancement()
                    .display(new DisplayInfo(augment(holderProvider, Augments.INTEGRATED_EXOSKELETON), Component.translatable("advancement.synthetics.install_augment"), Component.translatable("advancement.synthetics.install_augment.desc"), Optional.empty(), AdvancementType.TASK, true, true, false))
                    .parent(augmentationChamber)
                    .addCriterion("main", GenericSyntheticsCriterion.TriggerInstance.of(GenericSyntheticsCriterion.Trigger.INSTALLED_ANY_AUGMENT))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .rewards(AdvancementRewards.Builder.experience(50))
                    .save(consumer, Synthetics.MODID + ":main/install_augment");
            AdvancementHolder installPart = Advancement.Builder.advancement()
                    .display(new DisplayInfo(part(holderProvider, BodyParts.CYBERNETIC_RIGHT_HAND), Component.translatable("advancement.synthetics.install_part"), Component.translatable("advancement.synthetics.install_part.desc"), Optional.empty(), AdvancementType.TASK, true, true, false))
                    .parent(augmentationChamber)
                    .addCriterion("main", GenericSyntheticsCriterion.TriggerInstance.of(GenericSyntheticsCriterion.Trigger.INSTALLED_ANY_PART))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .rewards(AdvancementRewards.Builder.experience(25))
                    .save(consumer, Synthetics.MODID + ":main/install_part");
            AdvancementHolder installBattery = Advancement.Builder.advancement()
                    .display(new DisplayInfo(augment(holderProvider, Augments.HEART_BATTERY), Component.translatable("advancement.synthetics.install_battery"), Component.translatable("advancement.synthetics.install_battery.desc"), Optional.empty(), AdvancementType.TASK, true, true, false))
                    .parent(augmentationChamber)
                    .addCriterion("main", AugmentInstalledCriterion.TriggerInstance.of(holderProvider, Augments.HEART_BATTERY))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .rewards(AdvancementRewards.Builder.experience(75))
                    .save(consumer, Synthetics.MODID + ":main/install_battery");
        }
    }

    public static void register(DataGenerator gen, GatherDataEvent event, CompletableFuture<HolderLookup.Provider> future, PackOutput output) {
        gen.addProvider(event.includeServer(), new SyntheticsAdvancementProvider(output, future, event.getExistingFileHelper()));
    }

    private static ItemStack augment(HolderLookup.Provider provider, ResourceKey<Augment> augmentKey) {
        return provider.lookupOrThrow(SyntheticsData.AUGMENTS).get(augmentKey).orElseThrow().value().createDefaultItemStack(provider);
    }

    private static ItemStack part(HolderLookup.Provider provider, ResourceKey<BodyPart> partKey) {
        return provider.lookupOrThrow(SyntheticsData.BODY_PARTS).get(partKey).orElseThrow().value().createDefaultItemStack(provider);
    }
}
