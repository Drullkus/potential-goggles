package us.drullk.potentialgoggles.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.*;
import us.drullk.potentialgoggles.worldgen.bytemap.PositionedSpriteDensityFunction;
import us.drullk.potentialgoggles.worldgen.bytemap.TilingSpriteDensityFunction;

public class GogglesWorldgen {
    public static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, PotentialGoggles.MODID);

    public static final DeferredHolder<Codec<? extends DensityFunction>, Codec<HopperStairGridDensityFunction>> HOPPER_STAIR_GRID = DENSITY_FUNCTIONS.register("hopper_stair_grid", () -> HopperStairGridDensityFunction.CODEC);
    public static final DeferredHolder<Codec<? extends DensityFunction>, Codec<TilingSpriteDensityFunction>> SPRITE = DENSITY_FUNCTIONS.register("sprite", () -> TilingSpriteDensityFunction.CODEC);
    public static final DeferredHolder<Codec<? extends DensityFunction>, Codec<PositionedSpriteDensityFunction>> SPRITE_POS = DENSITY_FUNCTIONS.register("sprite_special", () -> PositionedSpriteDensityFunction.CODEC);
    public static final DeferredHolder<Codec<? extends DensityFunction>, Codec<DistanceDensityFunction>> DISTANCE = DENSITY_FUNCTIONS.register("distance", () -> DistanceDensityFunction.CODEC);

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, PotentialGoggles.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<ACustomStructure>> CUSTOM_STRUCTURE = STRUCTURE_TYPES.register(GogglesKeys.CUSTOM_STRUCTURE.location().getPath(), () -> () -> ACustomStructure.CODEC);

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, PotentialGoggles.MODID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> TESTING_FORT = STRUCTURE_PIECES.register("test_fort", () -> TestFortPiece::new);
}
