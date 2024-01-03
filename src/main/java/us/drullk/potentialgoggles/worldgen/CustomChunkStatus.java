package us.drullk.potentialgoggles.worldgen;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.content.ChunkBlanketings;
import us.drullk.potentialgoggles.worldgen.chunkblanketing.ChunkBlanketingProcessor;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class CustomChunkStatus {
    public static final ChunkStatus CHUNK_BLANKETING;

    public static void init() {
        // Ensures a once-execution of the static initializer, injecting a custom Chunk Status as a side effect of class-loading.
        // On subsequent calls, expect no other side effects to happen.
    }

    static {
        CHUNK_BLANKETING = registerInjectChunkStatus(PotentialGoggles.prefix("raw_surface_modification"), ChunkStatus.CARVERS, false, ChunkStatus.POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, CustomChunkStatus::chunkBlanketing, CustomChunkStatus::passThrough);
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private static ChunkStatus registerInjectChunkStatus(ResourceLocation name, ChunkStatus injectBefore, boolean hasLoadDependencies, EnumSet<Heightmap.Types> oceanFloor, ChunkStatus.ChunkType chunkType, ChunkStatus.SimpleGenerationTask simpleGenerationTask, ChunkStatus.LoadingTask loadingTask) {
        final ChunkStatus forInjection = Registry.register(BuiltInRegistries.CHUNK_STATUS, name, new ChunkStatus(injectBefore.getParent(), injectBefore.getParent().range, hasLoadDependencies, oceanFloor, chunkType, simpleGenerationTask, loadingTask));

        injectBefore.parent = forInjection;

        shiftSequentialStatuses(forInjection);

        return forInjection;
    }

    private static void shiftSequentialStatuses(ChunkStatus forInjection) {
        var name = forInjection.toString();
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
        PotentialGoggles.LOGGER.info("Successfully processed injection for custom Chunk Status '" + name + "'");
    }

    // A ChunkStatus.SimpleGenerationTask implementation
    private static void chunkBlanketing(ChunkStatus status, ServerLevel serverLevel, ChunkGenerator generator, List<ChunkAccess> chunkAccesses, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        WorldGenRegion worldGenRegion = new WorldGenRegion(serverLevel, chunkAccesses, status, 0);

        Set<Holder<Biome>> biomesInChunk = new ObjectArraySet<>();

        for (LevelChunkSection levelchunksection : worldGenRegion.getChunk(chunkPos.x, chunkPos.z).getSections()) {
            levelchunksection.getBiomes().getAll(biomesInChunk::add);
        }

        Iterator<ChunkBlanketingProcessor> modifierIterator = serverLevel
                .registryAccess()
                .registry(ChunkBlanketings.CHUNK_BLANKETING_REG_KEY)
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

    // A ChunkStatus.LoadingTask implementation
    private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> passThrough(ChunkStatus chunkStatus, ServerLevel serverLevel, StructureTemplateManager templateManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> completableFutureFunction, ChunkAccess chunkAccess) {
        return CompletableFuture.completedFuture(Either.left(chunkAccess));
    }
}
