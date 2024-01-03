package us.drullk.potentialgoggles.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkSurfaceModifier {
    HolderSet<Biome> biomesForApplication();

    void processChunk(RandomSource random, ChunkAccess chunkAccess);

    ChunkSurfaceModifierType getType();
}
