package com.thedrofdoctoring.synthetics.codec;

import com.mojang.datafixers.util.Function8;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ExtendedStreamCodecs {

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> codec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> codec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> codec7, final Function<C, T7> getter7, final StreamCodec<? super B, T8> codec8, final Function<C, T8> getter8,final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> p_331335_) {
        return new StreamCodec<>() {
            public @NotNull C decode(@NotNull B val) {
                T1 t1 = codec1.decode(val);
                T2 t2 = codec2.decode(val);
                T3 t3 = codec3.decode(val);
                T4 t4 = codec4.decode(val);
                T5 t5 = codec5.decode(val);
                T6 t6 = codec6.decode(val);
                T7 t7 = codec7.decode(val);
                T8 t8 = codec8.decode(val);
                return p_331335_.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            public void encode(@NotNull B b, @NotNull C c) {
                codec1.encode(b, getter1.apply(c));
                codec2.encode(b, getter2.apply(c));
                codec3.encode(b, getter3.apply(c));
                codec4.encode(b, getter4.apply(c));
                codec5.encode(b, getter5.apply(c));
                codec6.encode(b, getter6.apply(c));
                codec7.encode(b, getter7.apply(c));
                codec8.encode(b, getter8.apply(c));
            }
        };
    }
}
