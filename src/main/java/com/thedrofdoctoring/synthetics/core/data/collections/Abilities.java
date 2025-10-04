package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class Abilities {

    public static final ResourceKey<SyntheticAbility> INERTIAL_DAMPENERS_FALL_DAMAGE = create("inertial_dampener_fall_damage");
    public static final ResourceKey<SyntheticAbility> INERTIAL_DAMPENERS_SAFE_FALL = create("inertial_dampener_safe_fall");
    public static final ResourceKey<SyntheticAbility> LAUNCHBOOT_LAUNCH = create("launchboot_launch");


    private static ResourceKey<SyntheticAbility> create(String name) {
        return ResourceKey.create(
                SyntheticsData.ABILITIES,
                Synthetics.rl(name)
        );
    }
    private static @NotNull TagKey<SyntheticAbility> tag(@NotNull String name) {
        return TagKey.create(SyntheticsData.ABILITIES, Synthetics.rl(name));
    }
}
