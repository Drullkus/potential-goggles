package us.drullk.examplemod.experimental;

import com.mojang.serialization.Codec;

@FunctionalInterface
public interface ExampleType {
    Codec<? extends ExampleObject> getCodec();
}
