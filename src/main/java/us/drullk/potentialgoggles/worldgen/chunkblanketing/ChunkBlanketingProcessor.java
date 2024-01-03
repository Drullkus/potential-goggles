package us.drullk.potentialgoggles.worldgen.chunkblanketing;

import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkBlanketingProcessor {
    HolderSet<Biome> biomesForApplication();

    void processChunk(RandomSource random, ChunkAccess chunkAccess);

    ChunkBlanketingType getType();
}
