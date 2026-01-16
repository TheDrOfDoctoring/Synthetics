package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.particles.GenericParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SyntheticsParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Synthetics.MODID);

    public static final DeferredHolder<ParticleType<?>, GenericParticle> GENERIC_PARTICLE = PARTICLE_TYPES.register("particle", () -> new GenericParticle(false));

    public static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }
}
