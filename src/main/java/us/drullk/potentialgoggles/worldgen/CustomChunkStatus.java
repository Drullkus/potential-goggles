package us.drullk.potentialgoggles.worldgen;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import us.drullk.potentialgoggles.PotentialGoggles;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CustomChunkStatus {
    public static void init() {
        // Ensures a once-execution of static initializer, injecting a custom Chunk Status as a side effect. This method does nothing else.
    }

    static {
        ChunkStatus injectBefore = ChunkStatus.CARVERS;

        ChunkStatus.SimpleGenerationTask doWork = CustomChunkStatus::doWork;
        final ChunkStatus forInjection = Registry.register(BuiltInRegistries.CHUNK_STATUS, PotentialGoggles.prefix("raw_surface_modification"), new ChunkStatus(injectBefore.getParent(), injectBefore.getParent().range, false, EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), ChunkStatus.ChunkType.PROTOCHUNK, doWork, CustomChunkStatus::passThrough));

        injectBefore.parent = forInjection;

        var status = ChunkStatus.FULL;
        while (status != status.parent && status != forInjection) {
            status.index++;
            PotentialGoggles.LOGGER.info(status + " → " + status.index + " (After +1)");
            status = status.parent;
        }
        while (status != status.parent) {
            PotentialGoggles.LOGGER.info(status + " → " + status.index + " (Unadjusted)");
            status = status.parent;
        }

        PotentialGoggles.LOGGER.info("Successfully custom Chunk Status injection");
    }

    private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> passThrough(ChunkStatus chunkStatus, ServerLevel serverLevel, StructureTemplateManager templateManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> completableFutureFunction, ChunkAccess chunkAccess) {
        return CompletableFuture.completedFuture(Either.left(chunkAccess));
    }

    private static void doWork(ChunkStatus status, ServerLevel serverLevel, ChunkGenerator generator, List<ChunkAccess> chunkAccesses, ChunkAccess chunkAccess) {
        for (int dX = 0; dX < 16; dX++) {
            for (int dZ = 0; dZ < 16; dZ++) {
                int y = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, dX, dZ);
                BlockPos chunkMiddle = chunkAccess.getPos().getBlockAt(dX, y + 20, dZ);
                chunkAccess.setBlockState(chunkMiddle, Blocks.GLASS.defaultBlockState(), false);
            }
        }
    }
}
