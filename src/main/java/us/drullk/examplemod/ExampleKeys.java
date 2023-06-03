package us.drullk.examplemod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ExampleKeys {
    public static final ResourceKey<WorldPreset> TEST_PRESET = ResourceKey.create(Registries.WORLD_PRESET, ExampleMod.prefix("testing_preset"));
    public static final ResourceKey<NoiseGeneratorSettings> TEST_NOISE_SETTINGS = ResourceKey.create(Registries.NOISE_SETTINGS, ExampleMod.prefix("testing_noise_settings"));
    public static final ResourceKey<NormalNoise.NoiseParameters> TEST_NOISE_PARAMS = ResourceKey.create(Registries.NOISE, ExampleMod.prefix("testing_noise"));
}
