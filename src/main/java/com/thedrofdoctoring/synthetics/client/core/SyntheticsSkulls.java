package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

public class SyntheticsSkulls {

    public static void registerSkullModels(EntityRenderersEvent.CreateSkullModels event) {
        event.registerSkullModel(SkullTypes.ORGAN_HEAD, new SkullModel(event.getEntityModelSet().bakeLayer(SyntheticsEntitiesClient.ORGAN_HEAD)));
        SkullBlockRenderer.SKIN_BY_TYPE.put(SkullTypes.ORGAN_HEAD, Synthetics.rl("textures/entity/organ_mob.png"));
    }

    public enum SkullTypes implements SkullBlock.Type {
        ORGAN_HEAD("organ_head");

        private final String name;

        SkullTypes(String name) {
            this.name = name;
        }


        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
