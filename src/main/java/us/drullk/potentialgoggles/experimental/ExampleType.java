package us.drullk.potentialgoggles.experimental;

import com.mojang.serialization.MapCodec;

@FunctionalInterface
public interface ExampleType {
    MapCodec<? extends ExampleObject> getCodec();
}
