package us.drullk.potentialgoggles.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import us.drullk.potentialgoggles.hooks.BeardifierHooks;
import us.drullk.potentialgoggles.worldgen.CustomDensitySource;

@Mixin(Beardifier.class)
public class BeardifierMixin {
    protected ObjectListIterator<DensityFunction> customStructureDensities;

    @Inject(method = "forStructuresInChunk", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void augmentStructureTerrain(StructureManager structureManager, ChunkPos chunkPos, CallbackInfoReturnable<Beardifier> cir, int minBlockX, int minBlockZ, ObjectList<Beardifier.Rigid> pieceList, ObjectList<JigsawJunction> jigsawList) {
        Beardifier beardifier = new Beardifier(pieceList.iterator(), jigsawList.iterator());
        ((BeardifierMixin) (Object) beardifier).customStructureDensities = BeardifierHooks.customStructureTerrain(chunkPos, structureManager.startsForStructure(chunkPos, s -> s instanceof CustomDensitySource));
        cir.setReturnValue(beardifier);
    }

    @Inject(method = "compute", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void recompute(DensityFunction.FunctionContext pContext, CallbackInfoReturnable<Double> cir, int i, int j, int k, double d0) {
        cir.setReturnValue(BeardifierHooks.getNewDensity(pContext, this.customStructureDensities) + d0);
    }
}
