package us.drullk.potentialgoggles.worldgen.chunkblanketing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Function;

public interface ChunkBlanketingProcessor {
    HolderSet<Biome> biomesForApplication();

    void processChunk(RandomSource random, Function<BlockPos, Holder<Biome>> biomeGetter, ChunkAccess chunkAccess);

    ChunkBlanketingType getType();

    interface SimpleProcessor extends ChunkBlanketingProcessor {
        @Override
        default void processChunk(RandomSource random, Function<BlockPos, Holder<Biome>> biomeGetter, ChunkAccess chunkAccess) {
            for (int dX = 0; dX < 16; dX++) {
                for (int dZ = 0; dZ < 16; dZ++) {
                    int surfaceY = chunkAccess.getHeight(this.heightmap(), dX, dZ);
                    BlockPos firstAvailableBlock = chunkAccess.getPos().getBlockAt(dX, surfaceY + 1, dZ);

                    if (!this.biomesForApplication().contains(biomeGetter.apply(firstAvailableBlock)))
                        continue;

                    this.processColumn(random, chunkAccess, firstAvailableBlock);
                }
            }
        }

        void processColumn(RandomSource random, ChunkAccess chunkAccess, BlockPos aboveFloor);

        Heightmap.Types heightmap();
    }
}
