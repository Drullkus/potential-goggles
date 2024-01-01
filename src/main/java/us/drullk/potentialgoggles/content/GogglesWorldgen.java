package us.drullk.potentialgoggles.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.*;

public class GogglesWorldgen {
    public static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, PotentialGoggles.MODID);

    public static final RegistryObject<Codec<HopperStairGridDensityFunction>> HOPPER_STAIR_GRID = DENSITY_FUNCTIONS.register("hopper_stair_grid", () -> HopperStairGridDensityFunction.CODEC);
    public static final RegistryObject<Codec<TilingSpriteDensityFunction>> SPRITE = DENSITY_FUNCTIONS.register("sprite", () -> TilingSpriteDensityFunction.CODEC);
    public static final RegistryObject<Codec<PositionedSpriteDensityFunction>> SPRITE_POS = DENSITY_FUNCTIONS.register("sprite_special", () -> PositionedSpriteDensityFunction.CODEC);
    public static final RegistryObject<Codec<DistanceDensityFunction>> DISTANCE = DENSITY_FUNCTIONS.register("distance", () -> DistanceDensityFunction.CODEC);

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, PotentialGoggles.MODID);

    public static final RegistryObject<StructureType<ACustomStructure>> CUSTOM_STRUCTURE = STRUCTURE_TYPES.register(GogglesKeys.CUSTOM_STRUCTURE.location().getPath(), () -> () -> ACustomStructure.CODEC);

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, PotentialGoggles.MODID);

    public static final RegistryObject<StructurePieceType> TESTING_FORT = STRUCTURE_PIECES.register("test_fort", () -> TestFortPiece::new);
}
