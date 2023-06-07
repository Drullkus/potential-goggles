package us.drullk.examplemod;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ExampleDataGen {
    private static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ExampleObjects.TEST_REGISTRY_KEY, ExampleDataGen::testGenerate)
            .add(Registries.NOISE, ExampleDataGen::generateNoiseParams)
            .add(Registries.NOISE_SETTINGS, ExampleDataGen::generateNoiseSettings)
            .add(Registries.WORLD_PRESET, ExampleDataGen::generateWorldPresets);

    public static void testGenerate(BootstapContext<ExampleObject> context) {
        ExampleObject.ExampleBlock block = new ExampleObject.ExampleBlock(Blocks.DIAMOND_BLOCK);
        ExampleObject.ExampleItem item = new ExampleObject.ExampleItem(Items.CLAY_BALL);
        context.register(ExampleObjects.TEST_BLOCK, block);
        context.register(ExampleObjects.TEST_ITEM, item);
        context.register(ResourceKey.create(ExampleObjects.TEST_REGISTRY_KEY, ExampleMod.prefix("fused_wrapper")), new ExampleObject.ExampleFused(block, item));
    }

    public static void generateNoiseParams(BootstapContext<NormalNoise.NoiseParameters> context) {
        context.register(ExampleKeys.TEST_NOISE_PARAMS, new NormalNoise.NoiseParameters(1, -1));
    }

    public static void generateNoiseSettings(BootstapContext<NoiseGeneratorSettings> context) {
        HolderGetter<DensityFunction> densityFunctions = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseParameters = context.lookup(Registries.NOISE);

        DensityFunction shiftX = new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation("shift_x"))));
        DensityFunction shiftZ = new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation("shift_z"))));
        DensityFunction finalDensity = DensityFunctions.add(DensityFunctions.noise(noiseParameters.getOrThrow(ExampleKeys.TEST_NOISE_PARAMS), 0.0625f, 0.0625f), DensityFunctions.yClampedGradient(48, 80, 1.5, -1.5));

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
                DensityFunctions.zero(),
                // erosion
                DensityFunctions.zero(),
                // depth
                DensityFunctions.zero(),
                // ridges
                DensityFunctions.zero(),
                // initial_density_without_jaggedness
                DensityFunctions.zero(),
                // final_density
                HopperStairGridDensityFunction.INSTANCE.get(),
                // vein_toggle
                DensityFunctions.zero(),
                // vein_ridged
                DensityFunctions.zero(),
                // vein_gap
                DensityFunctions.zero()
        );

        context.register(ExampleKeys.TEST_NOISE_SETTINGS, new NoiseGeneratorSettings(
                NoiseSettings.create(-64, 384, 1, 2),
                Blocks.STONE.defaultBlockState(),
                //Blocks.WATER.defaultBlockState(),
                Blocks.AIR.defaultBlockState(),
                noiseRouter,
                SurfaceRuleData.overworld(),
                (new OverworldBiomeBuilder()).spawnTarget(),
                63,
                true,
                false,
                false,
                false
        ));
    }

    public static void generateWorldPresets(BootstapContext<WorldPreset> context) {
        context.register(ExampleKeys.TEST_PRESET, new WorldPreset(generatePreset(context)));
    }

    private static Map<ResourceKey<LevelStem>, LevelStem> generatePreset(BootstapContext<WorldPreset> context) {
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<MultiNoiseBiomeSourceParameterList> biomeNoiseParameters = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

        return Map.of(
                LevelStem.OVERWORLD, new LevelStem(dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(biomeNoiseParameters.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)), noiseSettings.getOrThrow(ExampleKeys.TEST_NOISE_SETTINGS))),
                LevelStem.NETHER, new LevelStem(dimensionTypes.getOrThrow(BuiltinDimensionTypes.NETHER), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(biomeNoiseParameters.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)), noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER))),
                LevelStem.END, new LevelStem(dimensionTypes.getOrThrow(BuiltinDimensionTypes.END), new NoiseBasedChunkGenerator(TheEndBiomeSource.create(biomes), noiseSettings.getOrThrow(NoiseGeneratorSettings.END)))
        );
    }

    public static void gatherData(GatherDataEvent event) {
        boolean isServer = event.includeServer();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(isServer, new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, REGISTRY_SET_BUILDER, Collections.singleton(ExampleMod.MODID)));
        //generator.addProvider(isServer, new ExampleTagGen(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(isServer, new ExampleWorldPresetTags(packOutput, lookupProvider, event.getExistingFileHelper()));
    }
}
