package us.drullk.potentialgoggles.worldgen;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.content.ChunkSurfaceModifiers;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

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
        ChunkPos chunkPos = chunkAccess.getPos();
        WorldGenRegion worldGenRegion = new WorldGenRegion(serverLevel, chunkAccesses, status, 0);

        Set<Holder<Biome>> biomesInChunk = new ObjectArraySet<>();

        for (LevelChunkSection levelchunksection : worldGenRegion.getChunk(chunkPos.x, chunkPos.z).getSections()) {
            levelchunksection.getBiomes().getAll(biomesInChunk::add);
        }

        Iterator<ChunkSurfaceModifier> modifierIterator = serverLevel
                .registryAccess()
                .registry(ChunkSurfaceModifiers.CHUNK_SURFACE_MODIFIER_REG_KEY)
                .map(Registry::stream)
                .orElseGet(Stream::empty)
                .filter(modifier -> modifier.biomesForApplication().stream().anyMatch(biomesInChunk::contains))
                .iterator();

        XoroshiroRandomSource random = new XoroshiroRandomSource(serverLevel.getSeed());

        long seed = serverLevel.getSeed() ^ Mth.getSeed(chunkPos.x, random.nextInt(256), chunkPos.z);

        while (modifierIterator.hasNext()) {
            random.setSeed(seed);
            seed = serverLevel.getSeed() ^ random.nextLong();
            modifierIterator.next().processChunk(random, chunkAccess);
        }
    }

}
