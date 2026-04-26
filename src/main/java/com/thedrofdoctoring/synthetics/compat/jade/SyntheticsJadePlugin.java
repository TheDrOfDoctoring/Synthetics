package com.thedrofdoctoring.synthetics.compat.jade;

import com.thedrofdoctoring.synthetics.blocks.entities.linkables.PermeableLinkableBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SyntheticsJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        this.overridePermeableShowcase(registration);
    }

    private void overridePermeableShowcase(IWailaClientRegistration registration) {
        registration.addRayTraceCallback((hitResult, accessor, orig) -> {
            if(accessor instanceof BlockAccessor blockAccessor
                    && blockAccessor.getBlockEntity() instanceof PermeableLinkableBlockEntity linkable
                    && linkable.disguisedAs() != null
            ) {
                return registration.blockAccessor().from(blockAccessor).blockState(linkable.disguisedAs()).build();
            }
            return accessor;
        });
    }
}
