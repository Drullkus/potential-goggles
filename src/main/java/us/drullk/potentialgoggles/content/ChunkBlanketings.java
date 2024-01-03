package us.drullk.potentialgoggles.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.chunkblanketing.ChunkBlanketingProcessor;
import us.drullk.potentialgoggles.worldgen.chunkblanketing.ChunkBlanketingType;
import us.drullk.potentialgoggles.worldgen.chunkblanketing.GlacialBlanketingProcessor;
import us.drullk.potentialgoggles.worldgen.chunkblanketing.PrototypeBlanketingProcessor;

public class ChunkBlanketings {
    public static final ResourceKey<Registry<ChunkBlanketingType>> CHUNK_BLANKETING_TYPE_REG_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("chunk_blanketing_types"));
    public static final DeferredRegister<ChunkBlanketingType> CHUNK_BLANKETING_TYPES = DeferredRegister.create(CHUNK_BLANKETING_TYPE_REG_KEY, PotentialGoggles.MODID);
    public static final Registry<ChunkBlanketingType> TYPE_REGISTRY_SUPPLIER = CHUNK_BLANKETING_TYPES.makeRegistry(builder -> builder.sync(false));
    public static final Codec<ChunkBlanketingType> TYPE_CODEC = ExtraCodecs.lazyInitializedCodec(TYPE_REGISTRY_SUPPLIER::byNameCodec);
    public static final Codec<ChunkBlanketingProcessor> DISPATCH_CODEC = TYPE_CODEC.dispatch("chunk_blanketing_type", ChunkBlanketingProcessor::getType, ChunkBlanketingType::getCodec);

    public static final DeferredHolder<ChunkBlanketingType, ChunkBlanketingType> PROTOTYPE_BLANKETING = CHUNK_BLANKETING_TYPES.register("prototype", () -> () -> PrototypeBlanketingProcessor.CODEC);
    public static final DeferredHolder<ChunkBlanketingType, ChunkBlanketingType> GLACIAL_BLANKETING = CHUNK_BLANKETING_TYPES.register("glacier", () -> () -> GlacialBlanketingProcessor.CODEC);

    public static final ResourceKey<Registry<ChunkBlanketingProcessor>> CHUNK_BLANKETING_REG_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("chunk_blanketing_processors"));
    public static final DeferredRegister<ChunkBlanketingProcessor> CHUNK_BLANKETING_PROCESSORS = DeferredRegister.create(CHUNK_BLANKETING_REG_KEY, PotentialGoggles.MODID);
    public static final ResourceKey<ChunkBlanketingProcessor> TEST = ResourceKey.create(CHUNK_BLANKETING_REG_KEY, PotentialGoggles.prefix("stronghold_biased"));
    public static final ResourceKey<ChunkBlanketingProcessor> RIVER_TEST = ResourceKey.create(CHUNK_BLANKETING_REG_KEY, PotentialGoggles.prefix("river_test"));
    public static final ResourceKey<ChunkBlanketingProcessor> GLACIER = ResourceKey.create(CHUNK_BLANKETING_REG_KEY, PotentialGoggles.prefix("glacier"));

    public static void setRegistryDatapack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(CHUNK_BLANKETING_REG_KEY, DISPATCH_CODEC);
    }

    public static void bootstrap(BootstapContext<ChunkBlanketingProcessor> context) {
        HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);
        context.register(TEST, new PrototypeBlanketingProcessor(lookup.getOrThrow(BiomeTags.IS_BEACH), BlockStateProvider.simple(Blocks.CANDLE), 32));
        context.register(RIVER_TEST, new PrototypeBlanketingProcessor(lookup.getOrThrow(BiomeTags.IS_RIVER), BlockStateProvider.simple(Blocks.BLUE_CANDLE), 16));
        context.register(GLACIER, new GlacialBlanketingProcessor(HolderSet.direct(lookup.getOrThrow(Biomes.SNOWY_PLAINS)), BlockStateProvider.simple(Blocks.PACKED_ICE), BlockStateProvider.simple(Blocks.ICE), 20));
    }
}
