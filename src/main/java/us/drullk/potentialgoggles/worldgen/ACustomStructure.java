package us.drullk.potentialgoggles.worldgen;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import us.drullk.potentialgoggles.content.GogglesByteMaps;
import us.drullk.potentialgoggles.content.GogglesWorldgen;

import java.util.List;
import java.util.Optional;

public class ACustomStructure extends Structure implements CustomDensitySource {
    public final static Codec<ACustomStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Structure.settingsCodec(instance),
            RegistryFileCodec.create(GogglesByteMaps.BYTE_MAP_REGISTRY_KEY, ByteMap.CODEC, false).fieldOf("terrain_bytemap").forGetter(f -> f.imageHolder)
    ).apply(instance, ACustomStructure::new));

    private final Holder<ByteMap> imageHolder;

    public ACustomStructure(StructureSettings settings, Holder<ByteMap> imageHolder) {
        super(settings);

        this.imageHolder = imageHolder;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext pContext) {
        Rotation rotation = Rotation.getRandom(pContext.random());
        BlockPos blockpos = this.getLowestYIn5by5BoxOffset7Blocks(pContext, rotation);
        return Optional.of(new Structure.GenerationStub(blockpos, (p_227538_) -> this.generatePieces(p_227538_, blockpos, rotation, pContext)));
    }

    private void generatePieces(StructurePiecesBuilder pBuilder, BlockPos pStartPos, Rotation pRotation, Structure.GenerationContext pContext) {
        List<StructurePiece> list = Lists.newArrayList();
        EndCityPieces.startHouseTower(pContext.structureTemplateManager(), pStartPos, pRotation, list, pContext.random());
        list.forEach(pBuilder::addPiece);
    }

    @Override
    public StructureType<?> type() {
        return GogglesWorldgen.CUSTOM_STRUCTURE.get();
    }

    @Override
    public DensityFunction getStructureTerraformer(ChunkPos chunkPosAt, StructureStart structurePieceSource) {
        PositionedSpriteDensityFunction imageDensity = PositionedSpriteDensityFunction.fromBox(this.imageHolder, 0, 1, structurePieceSource.getBoundingBox());
        DensityFunction yGradientStrip = DensityFunctions.mul(DensityFunctions.yClampedGradient(64, 80, 1, 0), DensityFunctions.yClampedGradient(48, 64, 0, 1));
        return DensityFunctions.lerp(imageDensity, DensityFunctions.constant(0), yGradientStrip);
    }
}
