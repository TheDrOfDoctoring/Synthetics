package com.thedrofdoctoring.synthetics.compat.patchouli;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliBook {

    private static final ResourceLocation BOOK_ID = Synthetics.rl("synthetics_guide");

    public static ItemStack make() {
        return PatchouliAPI.get().getBookStack(BOOK_ID);
    }
}
