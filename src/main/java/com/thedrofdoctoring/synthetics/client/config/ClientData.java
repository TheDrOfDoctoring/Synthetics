package com.thedrofdoctoring.synthetics.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ClientData {

    private boolean shouldShowEnergyOverlay;

    public ClientData(boolean shouldShowEnergyOverlay) {
        this.shouldShowEnergyOverlay = shouldShowEnergyOverlay;
    }


    public static ClientData empty() {
        return new ClientData(true);
    }

    public boolean shouldShowEnergyOverlay() {
        return shouldShowEnergyOverlay;
    }

    public void setShouldShowEnergyOverlay(boolean showEnergyOverlay) {
        this.shouldShowEnergyOverlay = showEnergyOverlay;
    }

    public static final MapCodec<ClientData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("should_show_energy").forGetter(ClientData::shouldShowEnergyOverlay)
    ).apply(instance, ClientData::new));
}
