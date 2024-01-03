package us.drullk.potentialgoggles.mixin;

import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.drullk.potentialgoggles.worldgen.CustomChunkStatus;

import java.util.List;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    @Inject(method = "getStatusList", at = @At("HEAD"))
    private static void updateStatusList(CallbackInfoReturnable<List<ChunkStatus>> cir) {
        CustomChunkStatus.init();
    }
}
