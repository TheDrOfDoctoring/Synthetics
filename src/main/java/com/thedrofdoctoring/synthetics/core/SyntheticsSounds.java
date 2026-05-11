package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class SyntheticsSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Synthetics.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> DRONE_BUZZ = create("entity.drone_buzz");
    public static final DeferredHolder<SoundEvent, SoundEvent> SQUELCH = create("entity.player_squelch");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_FLIGHT = create("entity.rocket_flight");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLASTING = create("entity.blasting");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOCK_HIT = create("entity.shock_hit");


    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> create(@NotNull String soundNameIn) {
        ResourceLocation resourcelocation = Synthetics.rl(soundNameIn);
        return SOUND_EVENTS.register(soundNameIn, () -> SoundEvent.createVariableRangeEvent(resourcelocation));
    }
}
