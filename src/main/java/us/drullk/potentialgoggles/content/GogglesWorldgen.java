package us.drullk.potentialgoggles.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.HopperStairGridDensityFunction;
import us.drullk.potentialgoggles.worldgen.SpriteDensityFunction;

public class GogglesWorldgen {
    public static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, PotentialGoggles.MODID);

    public static final RegistryObject<Codec<HopperStairGridDensityFunction>> HOPPER_STAIR_GRID = DENSITY_FUNCTIONS.register("hopper_stair_grid", () -> HopperStairGridDensityFunction.CODEC);
    public static final RegistryObject<Codec<SpriteDensityFunction>> SPRITE = DENSITY_FUNCTIONS.register("sprite", () -> SpriteDensityFunction.CODEC);
}
