package us.drullk.potentialgoggles.mixin;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import org.spongepowered.asm.mixin.Mixin;
import us.drullk.potentialgoggles.worldgen.CustomDensitySource;
import us.drullk.potentialgoggles.worldgen.DistanceDensityFunction;

// Example implementation of the CustomDensitySource interface on a Structure class, where a sphere of floating sand/sandstone will generate above desert temples
@Mixin(DesertPyramidStructure.class)
public class DesertPyramidStructureMixin implements CustomDensitySource {
    @Override
    public DensityFunction getStructureTerraformer(ChunkPos chunkPosAt, StructureStart structurePieceSource) {
        BoundingBox boundingBox = structurePieceSource.getBoundingBox();
        int centerX = (boundingBox.maxX() + boundingBox.minX()) >> 1;
        int centerZ = (boundingBox.maxZ() + boundingBox.minZ()) >> 1;
        return new DistanceDensityFunction(centerX, boundingBox.maxY() + 20, centerZ, 9, 2f, 0);
    }
}
