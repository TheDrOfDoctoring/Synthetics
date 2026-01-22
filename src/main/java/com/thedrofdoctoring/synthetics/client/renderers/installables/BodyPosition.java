package com.thedrofdoctoring.synthetics.client.renderers.installables;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public enum BodyPosition implements IBodyPosition {
    HEAD("head", "head"),
    BODY("body", "body"),
    RIGHT_ARM("right_arm", "right_arm"),
    LEFT_ARM("left_arm", "left_arm"),
    RIGHT_LEG("right_leg", "right_leg"),
    LEFT_LEG("left_leg", "left_leg");

    public static final Codec<BodyPosition> CODEC = Codec.stringResolver(BodyPosition::id, BodyPosition::fromString);
    public static final StreamCodec<ByteBuf, BodyPosition> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(BodyPosition::fromString, BodyPosition::id);

    private static Map<String, BodyPosition> LOOKUP;

    private final String id;
    private final String part;

    BodyPosition(String id, String part) {
        this.id = id;
        this.part = part;
    }

    public String id() {
        return id;
    }

    @Override
    public String part() {
        return part;
    }

    public static @Nullable BodyPosition fromString(String s) {
        if (LOOKUP == null) {
            Map<String, BodyPosition> lookup = new HashMap<>();
            for (var pos : BodyPosition.values()) {
                lookup.put(pos.id(), pos);
            }
            LOOKUP = lookup;
        }

        return LOOKUP.get(s);
    }
}
