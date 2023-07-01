package us.drullk.examplemod;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ExampleWorldgen {
    public static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, ExampleMod.MODID);

    public static final RegistryObject<Codec<HopperStairGridDensityFunction>> HOPPER_STAIR_GRID = DENSITY_FUNCTIONS.register("hopper_stair_grid", () -> HopperStairGridDensityFunction.CODEC);
    public static final RegistryObject<Codec<SpriteDensityFunction>> SPRITE = DENSITY_FUNCTIONS.register("sprite", () -> SpriteDensityFunction.CODEC);
}
