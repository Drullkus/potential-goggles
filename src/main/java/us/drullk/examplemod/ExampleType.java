package us.drullk.examplemod;

import com.mojang.serialization.Codec;

@FunctionalInterface
public interface ExampleType {
    Codec<? extends ExampleObject> getCodec();
}
