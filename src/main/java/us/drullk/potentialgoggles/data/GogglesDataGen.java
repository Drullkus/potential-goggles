package us.drullk.potentialgoggles.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import us.drullk.potentialgoggles.*;
import us.drullk.potentialgoggles.content.ChunkSurfaceModifiers;
import us.drullk.potentialgoggles.content.GogglesByteMaps;
import us.drullk.potentialgoggles.experimental.ExampleObject;
import us.drullk.potentialgoggles.worldgen.ACustomStructure;
import us.drullk.potentialgoggles.worldgen.ByteMap;
import us.drullk.potentialgoggles.worldgen.GogglesKeys;
import us.drullk.potentialgoggles.content.ExampleObjects;
import us.drullk.potentialgoggles.worldgen.TilingSpriteDensityFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GogglesDataGen {
    private static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ChunkSurfaceModifiers.CHUNK_SURFACE_MODIFIER_REG_KEY, ChunkSurfaceModifiers::bootstrap)
            .add(ExampleObjects.TEST_REGISTRY_KEY, GogglesDataGen::testGenerate)
            .add(GogglesByteMaps.BYTE_MAP_REGISTRY_KEY, GogglesDataGen::generateByteMaps)
            .add(Registries.NOISE, GogglesDataGen::generateNoiseParams)
            .add(Registries.NOISE_SETTINGS, GogglesDataGen::generateNoiseSettings)
            .add(Registries.WORLD_PRESET, GogglesDataGen::generateWorldPresets)
            .add(Registries.STRUCTURE, GogglesDataGen::generateStructureConfigs)
            .add(Registries.STRUCTURE_SET, GogglesDataGen::generateStructureSets);

    public static void generateByteMaps(BootstapContext<ByteMap> context) {
        registerBytemap(context, GogglesByteMaps.DENSITY_CLAW);
        registerBytemap(context, GogglesByteMaps.DENSITY_SPIRAL);

        context.register(GogglesByteMaps.SEQUENTIAL_COUNTING_BYTES, new ByteMap(16, 16, (x, y) -> (byte) (x + y * 16 & 0xFF)));
    }

    private static void registerBytemap(BootstapContext<ByteMap> context, ResourceKey<ByteMap> bytemapKey) {
        context.register(bytemapKey, ByteMap.initForExternalImage(bytemapKey.location().getPath()));
    }

    public static void testGenerate(BootstapContext<ExampleObject> context) {
        ExampleObject.ExampleBlock block = new ExampleObject.ExampleBlock(Blocks.DIAMOND_BLOCK);
        ExampleObject.ExampleItem item = new ExampleObject.ExampleItem(Items.CLAY_BALL);
        context.register(ExampleObjects.TEST_BLOCK, block);
        context.register(ExampleObjects.TEST_ITEM, item);
        context.register(ResourceKey.create(ExampleObjects.TEST_REGISTRY_KEY, PotentialGoggles.prefix("fused_wrapper")), new ExampleObject.ExampleFused(block, item));
    }

    public static void generateNoiseParams(BootstapContext<NormalNoise.NoiseParameters> context) {
        context.register(GogglesKeys.TEST_NOISE_PARAMS, new NormalNoise.NoiseParameters(1, -1));
    }

    public static void generateNoiseSettings(BootstapContext<NoiseGeneratorSettings> context) {
        HolderGetter<DensityFunction> densityFunctions = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseParameters = context.lookup(Registries.NOISE);
        HolderGetter<ByteMap> byteMaps = context.lookup(GogglesByteMaps.BYTE_MAP_REGISTRY_KEY);

        DensityFunction shiftX = new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation("shift_x"))));
        DensityFunction shiftZ = new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation("shift_z"))));
        //DensityFunction finalDensity = DensityFunctions.add(DensityFunctions.noise(noiseParameters.getOrThrow(ExampleKeys.TEST_NOISE_PARAMS), 0.0625f, 0.0625f), DensityFunctions.yClampedGradient(48, 80, 1.5, -1.5));
        //DensityFunction finalDensity = HopperStairGridDensityFunction.INSTANCE.get();
        //DensityFunction finalDensity = DensityFunctions.add(new SpriteDensityFunction(byteMaps.getOrThrow(ExampleByteMaps.SEQUENTIAL_COUNTING_BYTES), 0, 1, 2, 3), DensityFunctions.yClampedGradient(-64, 255, 0.25, -1));
        DensityFunction finalDensity = getTerrainDensityFunction(noiseParameters, byteMaps);

        // Customized from NoiseRouterData#noNewCaves
        NoiseRouter noiseRouter = new NoiseRouter(
                // barrierNoise
                DensityFunctions.zero(),
                // fluid_level_floodedness
                DensityFunctions.zero(),
                // fluid_level_spread
                DensityFunctions.zero(),
                // lava
                DensityFunctions.zero(),
                // temperature
                DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseParameters.getOrThrow(Noises.TEMPERATURE)),
                // vegetation
                DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseParameters.getOrThrow(Noises.VEGETATION)),
                // continents
                finalDensity,
                // erosion
                DensityFunctions.zero(),
                // depth
                new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(NoiseRouterData.DEPTH)),
                // ridges
                new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(NoiseRouterData.RIDGES)),
                // initial_density_without_jaggedness
                finalDensity,
                // final_density
                finalDensity,
                // vein_toggle
                DensityFunctions.zero(),
                // vein_ridged
                DensityFunctions.zero(),
                // vein_gap
                DensityFunctions.zero()
        );

        context.register(GogglesKeys.TEST_NOISE_SETTINGS, new NoiseGeneratorSettings(
                NoiseSettings.create(-64, 384, 1, 2),
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                //Blocks.AIR.defaultBlockState(),
                noiseRouter,
                SurfaceRuleData.overworld(),
                (new OverworldBiomeBuilder()).spawnTarget(),
                //List.of(new Climate.ParameterPoint()),
                63,
                true,
                false,
                false,
                false
        ));
    }

    @NotNull
    private static DensityFunction getTerrainDensityFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParameters, HolderGetter<ByteMap> byteMaps) {
        TilingSpriteDensityFunction spiral = new TilingSpriteDensityFunction(byteMaps.getOrThrow(GogglesByteMaps.DENSITY_SPIRAL), 0, 0.5, 0.66f, 0.66f);
        DensityFunction verticalGradient = DensityFunctions.yClampedGradient(-16, 128, 2, -2);

        DensityFunction smallNoise = DensityFunctions.mul(DensityFunctions.noise(noiseParameters.getOrThrow(GogglesKeys.TEST_NOISE_PARAMS), 0.0625f, 0.0625f), DensityFunctions.constant(0.05f));
        DensityFunction bigNoise = DensityFunctions.mul(DensityFunctions.noise(noiseParameters.getOrThrow(GogglesKeys.TEST_NOISE_PARAMS), 0.015625f, 0.0078125f), DensityFunctions.constant(0.2f));
        DensityFunction interpolator = DensityFunctions.mul(DensityFunctions.noise(noiseParameters.getOrThrow(GogglesKeys.TEST_NOISE_PARAMS), 0.03125f, 0.03125f), DensityFunctions.constant(0.5f));

        DensityFunction interpolatedNoise = DensityFunctions.mul(DensityFunctions.lerp(bigNoise, smallNoise, interpolator), DensityFunctions.constant(0.5f));

        DensityFunction terrain = DensityFunctions.add(verticalGradient, interpolatedNoise);
        return DensityFunctions.add(spiral, terrain);
    }

    public static void generateWorldPresets(BootstapContext<WorldPreset> context) {
        context.register(GogglesKeys.TEST_PRESET, new WorldPreset(generatePreset(context)));
    }

    private static Map<ResourceKey<LevelStem>, LevelStem> generatePreset(BootstapContext<WorldPreset> context) {
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<MultiNoiseBiomeSourceParameterList> biomeNoiseParameters = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

        Climate.Parameter zero = Climate.Parameter.point(0);
        return Map.of(
                //LevelStem.OVERWORLD, new LevelStem(dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(biomeNoiseParameters.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)), noiseSettings.getOrThrow(ExampleKeys.TEST_NOISE_SETTINGS))),
                LevelStem.OVERWORLD, new LevelStem(dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(List.of(Pair.of(new Climate.ParameterPoint(zero, zero, zero, zero, zero, zero, 0), biomes.getOrThrow(Biomes.PLAINS))))), noiseSettings.getOrThrow(GogglesKeys.TEST_NOISE_SETTINGS))),
                LevelStem.NETHER, new LevelStem(dimensionTypes.getOrThrow(BuiltinDimensionTypes.NETHER), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(biomeNoiseParameters.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)), noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER))),
                LevelStem.END, new LevelStem(dimensionTypes.getOrThrow(BuiltinDimensionTypes.END), new NoiseBasedChunkGenerator(TheEndBiomeSource.create(biomes), noiseSettings.getOrThrow(NoiseGeneratorSettings.END)))
        );
    }

    private static void generateStructureConfigs(BootstapContext<Structure> context) {
        Structure.StructureSettings settings = new Structure.StructureSettings(context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE);
        Holder.Reference<ByteMap> imageHolder = context.lookup(GogglesByteMaps.BYTE_MAP_REGISTRY_KEY).getOrThrow(GogglesByteMaps.DENSITY_SPIRAL);

        context.register(GogglesKeys.CUSTOM_STRUCTURE, new ACustomStructure(settings, imageHolder));
    }

    private static void generateStructureSets(BootstapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        RandomSpreadStructurePlacement placement = new RandomSpreadStructurePlacement(10, 5, RandomSpreadType.LINEAR, 0b11111101000);

        context.register(GogglesKeys.CUSTOM_STRUCTURE_SET, new StructureSet(structures.getOrThrow(GogglesKeys.CUSTOM_STRUCTURE), placement));
    }

    public static void gatherData(GatherDataEvent event) {
        boolean isServer = event.includeServer();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new GogglesLocale(packOutput));
        generator.addProvider(isServer, new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, REGISTRY_SET_BUILDER, Collections.singleton(PotentialGoggles.MODID)));
        //generator.addProvider(isServer, new ExampleTagGen(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(isServer, new GogglesWorldPresetTags(packOutput, lookupProvider, event.getExistingFileHelper()));
    }
}
