package us.drullk.potentialgoggles.worldgen.chunkblanketing;

import com.mojang.serialization.Codec;

@FunctionalInterface
public interface ChunkBlanketingType {
    Codec<? extends ChunkBlanketingProcessor> getCodec();
}
