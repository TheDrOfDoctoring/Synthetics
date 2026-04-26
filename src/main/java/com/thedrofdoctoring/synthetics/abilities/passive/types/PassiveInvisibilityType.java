package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.PassiveInvisibilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;

@EventBusSubscriber
public class PassiveInvisibilityType extends PassiveAbilityType<PassiveInvisibilityInstance> {

    public PassiveInvisibilityType(ResourceLocation id) {
        super(id);
    }

    @Override
    public Optional<PassiveInvisibilityInstance> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        if(data instanceof PassiveInvisibilityInstance.Data invisData) {
            return Optional.of(new PassiveInvisibilityInstance(this, player, invisData, instanceID, powerDraw));
        }
        return Optional.empty();
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData() instanceof PassiveInvisibilityInstance.Data data) {
            switch (data.type()) {
                case CROUCHING -> description.add(Component.translatable("abilities.synthetics.description.invis.crouching").withStyle(ChatFormatting.BLUE));
                case STILL -> description.add(Component.translatable("abilities.synthetics.description.invis.still").withStyle(ChatFormatting.BLUE));
                case BOTH -> description.add(Component.translatable("abilities.synthetics.description.invis.both").withStyle(ChatFormatting.BLUE));
                case EITHER -> description.add(Component.translatable("abilities.synthetics.description.invis.either").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    public static PassiveInvisibilityInstance.Data create(InvisibilityType type) {
        return new PassiveInvisibilityInstance.Data(type);
    }

    @SubscribeEvent
    public static void playerTickEvent(PlayerTickEvent.Post event) {
        if(event.getEntity().tickCount % 20 == 0) {
            Player player = event.getEntity();
            SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);

            boolean shouldBeInvisible = false;
            boolean isMoving = player.getDeltaMovement().lengthSqr() > 0;
            boolean isCrouching = player.isCrouching();

            for(Ability ability : syntheticsPlayer.getAbilityManager().addedAbilities()) {
                if(ability.abilityData() instanceof PassiveInvisibilityInstance.Data data) {

                    switch (data.type()) {
                        case CROUCHING -> {
                            if(isCrouching) shouldBeInvisible = true;
                        }
                        case STILL -> {
                            if(!isMoving) shouldBeInvisible = true;
                        }
                        case BOTH -> {
                            if(!isMoving && isCrouching) shouldBeInvisible = true;
                        }
                        case EITHER -> {
                            if(!isMoving || isCrouching) shouldBeInvisible = true;
                        }
                    }
                }
            }

            setInvisible(player, shouldBeInvisible);
        }
    }

    private static void setInvisible(Player player, boolean shouldBe) {
        player.setInvisible(shouldBe);
        SyntheticsPlayerCache.get(player).invisible = shouldBe;
    }


    public enum InvisibilityType implements StringRepresentable {

        CROUCHING(0, "crouching"),
        STILL(1, "standing_still"),
        BOTH(2, "both"),
        EITHER(3, "either");

        private final String representation;
        private final int id;

        private static final IntFunction<InvisibilityType> BY_ID = ByIdMap.continuous(InvisibilityType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, InvisibilityType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, InvisibilityType::getId);

        InvisibilityType(int id, String representation) {
            this.representation = representation;
            this.id = id;
        }
        public int getId() {
            return this.id;
        }


        @Override
        public @NotNull String getSerializedName() {
            return this.representation;
        }
    }
}
