package com.thedrofdoctoring.synthetics.client.sounds;

import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class DroneFlyingSoundInstance extends AbstractTickableSoundInstance {
    private static final float VOLUME_MAX = 0.7F;
    private static final float PITCH_MAX = 0.7f;
    protected final DroneEntity drone;

    public DroneFlyingSoundInstance(DroneEntity drone, SoundEvent soundEvent, SoundSource source) {
        super(soundEvent, source, SoundInstance.createUnseededRandom());
        this.drone = drone;
        this.x = ((float)drone.getX());
        this.y = ((float)drone.getY());
        this.z = ((float)drone.getZ());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
    }

    public void tick() {

        if (!this.drone.isRemoved() && this.drone.isFlying()) {
            this.x = ((float)this.drone.getX());
            this.y = ((float)this.drone.getY());
            this.z = ((float)this.drone.getZ());

            this.pitch = PITCH_MAX;
            this.volume = VOLUME_MAX;
        } else {
            this.stop();
        }

    }


    public boolean canStartSilent() {
        return true;
    }

    public boolean canPlaySound() {
        return !this.drone.isSilent();
    }

}
