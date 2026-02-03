package com.thedrofdoctoring.synthetics.client.core.assets;

import com.thedrofdoctoring.synthetics.client.core.assets.gen.InstallableModelProvider;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegmentType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.ArrayList;
import java.util.List;

public class SyntheticsAssets {

    public static void dataGen(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        event.addProvider(defaultInstallableModels(output));

    }

    protected static InstallableModelProvider defaultInstallableModels(PackOutput output) {
        List<InstallableModelProvider.SubProvider<?>> providers = new ArrayList<>();

        providers.add(
                new InstallableModelProvider.SubProvider<Augment>(output, "augment") {
                    @Override
                    public void addModels() {


                    }
                }
        );

        providers.add(
                new InstallableModelProvider.SubProvider<BodySegmentType>(output, "segment") {
                    @Override
                    public void addModels() {

                    }
                }
        );

        providers.add(
                new InstallableModelProvider.SubProvider<BodyPartType>(output, "bodypart") {
                    @Override
                    public void addModels() {

                    }
                }
        );

        return new InstallableModelProvider(providers.toArray(InstallableModelProvider.SubProvider[]::new));
    }
}
