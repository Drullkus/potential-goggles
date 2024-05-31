package us.drullk.potentialgoggles.hooks;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import us.drullk.potentialgoggles.worldgen.CustomDensitySource;

import java.util.List;

public final class BeardifierHooks {
    public static ObjectListIterator<DensityFunction> customStructureTerrain(ChunkPos chunkPos, StructureManager structureManager) {
        ObjectArrayList<DensityFunction> customStructureTerraforms = new ObjectArrayList<>(10);

        for (StructureStart structureStart : structureManager.startsForStructure(chunkPos, s -> s instanceof CustomDensitySource))
            if (structureStart.getStructure() instanceof CustomDensitySource customDensitySource)
                customStructureTerraforms.add(customDensitySource.getStructureTerraformer(chunkPos, structureStart));

        return customStructureTerraforms.iterator();
    }

    public static double getNewDensity(DensityFunction.FunctionContext context, ObjectListIterator<DensityFunction> customDensities) {
        double newDensity = 0;

        while (customDensities.hasNext()) {
            newDensity += customDensities.next().compute(context);
        }
        customDensities.back(Integer.MAX_VALUE);

        return newDensity;
    }
}
