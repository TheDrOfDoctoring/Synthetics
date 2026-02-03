package com.thedrofdoctoring.synthetics.core.data.types.body.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.AbilityWheel;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.ISlotEntry;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.AbilityRadialSlot;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public record Ability(AbilityType abilityType, AbilityData abilityData, AbilityNature abilityNature, ResourceLocation id) implements ISlotEntry<AbilityRadialSlot> {

    public static final MapCodec<Ability> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityType.CODEC.fieldOf("ability_type").forGetter(Ability::abilityType),
            AbilityData.DISPATCH_CODEC.fieldOf("ability_data").forGetter(Ability::abilityData),
            StringRepresentable.fromEnum(AbilityNature::values).fieldOf("nature").forGetter(Ability::abilityNature),
            ResourceLocation.CODEC.fieldOf("id").forGetter(Ability::id)
    ).apply(instance, Ability::new));

    public static Ability create(AbilityType abilityType, AbilityData data, ResourceLocation id) {
        return new Ability(abilityType, data, AbilityNature.BENEFICIAL, id);
    }

    public static Ability create(AbilityType abilityType, AbilityData data, AbilityNature nature, ResourceLocation id) {
        return new Ability(abilityType, data, nature, id);
    }

    public static final Codec<HolderSet<Ability>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.ABILITIES, CODEC.codec());
    public static final Codec<Holder<Ability>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.ABILITIES, CODEC.codec());


    @Override
    public AbilityRadialSlot addToSlot(GenericRadialMenu<AbilityRadialSlot> menu, AbilityRadialSlot slot, Level level) {

        LinkedList<Holder<Ability>> slotData = slot.slotData().abilities().stream().collect(Collectors.toCollection(LinkedList::new));

        var lookupOpt = level.registryAccess().lookup(SyntheticsData.ABILITIES);
        if(lookupOpt.isPresent()) {
            ResourceKey<Ability> selfKey = ResourceKey.create(SyntheticsData.ABILITIES, id);
            Optional<Holder.Reference<Ability>> self = lookupOpt.get().get(selfKey);
            if(self.isPresent()) {
                slotData.add(self.get());
                return new AbilityRadialSlot(menu, new AbilityWheel.Slot(HolderSet.direct(slotData)));
            }
            return slot;
        }
        Synthetics.LOGGER.warn("Failed to add ability {} to slot", id);
        return slot;
    }

    @Override
    public void drawSelected(GuiGraphics graphics, GenericRadialMenu<AbilityRadialSlot> menu, int x, int y) {
        graphics.blit(this.textureLocation(), (x - 8), (y - 8), 0, 0, 0, 16, 16, 16, 16);
    }

    public ResourceLocation textureLocation() {
        ResourceLocation id = abilityType.getAbilityID();
        return id.withPath("textures/abilities/" + id.getPath() + ".png");
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Ability> STREAM_CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, Ability::abilityType,
            AbilityData.DISPATCH_STREAM, Ability::abilityData,
            AbilityNature.STREAM_CODEC, Ability::abilityNature,
            ResourceLocation.STREAM_CODEC, Ability::id,
            Ability::new);

    public enum AbilityNature implements StringRepresentable {

        BENEFICIAL(0, "beneficial", ChatFormatting.BLUE),
        NEUTRAL(1, "neutral", ChatFormatting.GRAY),
        DETRIMENTAL(2, "detrimental", ChatFormatting.RED);

        private final String representation;
        private final int id;
        private final ChatFormatting defaultColour;

        private static final IntFunction<AbilityNature> BY_ID = ByIdMap.continuous(AbilityNature::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, AbilityNature> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, AbilityNature::getId);

        AbilityNature(int id, String representation, ChatFormatting defaultColour) {
            this.representation = representation;
            this.id = id;
            this.defaultColour = defaultColour;
        }
        public int getId() {
            return this.id;
        }

        public ChatFormatting defaultColour() {
            return defaultColour;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.representation;
        }
    }
}
