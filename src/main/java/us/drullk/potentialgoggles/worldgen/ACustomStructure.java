package us.drullk.potentialgoggles.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.content.GogglesByteMaps;
import us.drullk.potentialgoggles.content.GogglesWorldgen;
import us.drullk.potentialgoggles.worldgen.bytemap.ByteMap;
import us.drullk.potentialgoggles.worldgen.bytemap.PositionedSpriteDensityFunction;

import java.util.Optional;

public class ACustomStructure extends Structure implements CustomDensitySource {
    public final static MapCodec<ACustomStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Structure.settingsCodec(instance),
            RegistryFileCodec.create(GogglesByteMaps.BYTE_MAP_REGISTRY_KEY, ByteMap.CODEC, false).fieldOf("terrain_bytemap").forGetter(f -> f.imageHolder)
    ).apply(instance, ACustomStructure::new));

    private final Holder<ByteMap> imageHolder;

    public ACustomStructure(StructureSettings settings, Holder<ByteMap> imageHolder) {
        super(settings);

        this.imageHolder = imageHolder;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        Mirror mirror = Util.getRandom(Mirror.values(), context.random());
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(80);
        return Optional.of(new Structure.GenerationStub(blockpos, (p_227538_) -> this.generatePieces(p_227538_, blockpos, mirror, rotation, context)));
    }

    private void generatePieces(StructurePiecesBuilder builder, BlockPos startPos, Mirror mirror, Rotation rotation, GenerationContext context) {
        builder.addPiece(new TestFortPiece(context.structureTemplateManager(), PotentialGoggles.prefix("test_fort"), new StructurePlaceSettings().setMirror(mirror).setRotation(rotation), startPos));
    }

    @Override
    public StructureType<?> type() {
        return GogglesWorldgen.CUSTOM_STRUCTURE.get();
    }

    @Override
    public DensityFunction getStructureTerraformer(ChunkPos chunkPosAt, StructureStart structurePieceSource) {
        PositionedSpriteDensityFunction imageDensity = PositionedSpriteDensityFunction.fromBox(this.imageHolder, 0, 2, structurePieceSource.getBoundingBox());
        int yLevel = 80;
        int yAbove = 4;
        int yDepth = 32;
        DensityFunction yGradientStrip = DensityFunctions.min(DensityFunctions.yClampedGradient(yLevel, yLevel + yAbove, 1, 0), DensityFunctions.yClampedGradient(yLevel - yDepth, yLevel, 0, 1));
        return DensityFunctions.lerp(imageDensity, DensityFunctions.constant(0), yGradientStrip);
    }

    // This override is especially important, because the bounding box determines how far the world generator will apply the above custom Terraformer
    // Chunks not overlapping this structure will not receive any terraforming normally applied by this structure.
    @Override
    public BoundingBox adjustBoundingBox(BoundingBox boundingBox) {
        return super.adjustBoundingBox(boundingBox).inflatedBy(32);
    }

    @Override
    public TerrainAdjustment terrainAdaptation() {
        return TerrainAdjustment.BEARD_THIN;
    }
}
