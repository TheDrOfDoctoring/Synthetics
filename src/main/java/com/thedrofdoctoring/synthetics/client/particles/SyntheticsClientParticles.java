package com.thedrofdoctoring.synthetics.client.particles;

import com.thedrofdoctoring.synthetics.core.SyntheticsParticles;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

public class SyntheticsClientParticles {


    public static void registerParticles(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpecial(SyntheticsParticles.GENERIC_PARTICLE.get(), new GenericParticleClient.Factory());
    }

}
