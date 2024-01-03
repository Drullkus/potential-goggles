package us.drullk.potentialgoggles.worldgen;

import com.mojang.serialization.Codec;

@FunctionalInterface
public interface ChunkSurfaceModifierType {
    Codec<? extends ChunkSurfaceModifier> getCodec();
}
