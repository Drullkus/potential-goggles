package us.drullk.potentialgoggles.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import us.drullk.potentialgoggles.PotentialGoggles;

public class GogglesKeys {
    public static final ResourceKey<WorldPreset> TEST_PRESET = ResourceKey.create(Registries.WORLD_PRESET, PotentialGoggles.prefix("testing_preset"));
    public static final ResourceKey<NoiseGeneratorSettings> TEST_NOISE_SETTINGS = ResourceKey.create(Registries.NOISE_SETTINGS, PotentialGoggles.prefix("testing_noise_settings"));
    public static final ResourceKey<NormalNoise.NoiseParameters> TEST_NOISE_PARAMS = ResourceKey.create(Registries.NOISE, PotentialGoggles.prefix("testing_noise"));

    public static final ResourceKey<Structure> CUSTOM_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, PotentialGoggles.prefix("custom_structure"));
    public static final ResourceKey<StructureSet> CUSTOM_STRUCTURE_SET = ResourceKey.create(Registries.STRUCTURE_SET, PotentialGoggles.prefix("custom_structure_set"));
}
