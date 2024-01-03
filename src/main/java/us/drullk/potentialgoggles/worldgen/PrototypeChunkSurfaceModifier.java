package us.drullk.potentialgoggles.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import us.drullk.potentialgoggles.content.ChunkSurfaceModifiers;

public record PrototypeChunkSurfaceModifier(HolderSet<Biome> biomesForApplication, BlockStateProvider blockState, int height) implements ChunkSurfaceModifier {
    public static final Codec<PrototypeChunkSurfaceModifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registries.BIOME, false).fieldOf("biome_filter").forGetter(PrototypeChunkSurfaceModifier::biomesForApplication),
            BlockStateProvider.CODEC.fieldOf("blockstate").forGetter(PrototypeChunkSurfaceModifier::blockState),
            Codec.INT.fieldOf("height").forGetter(PrototypeChunkSurfaceModifier::height)
    ).apply(inst, PrototypeChunkSurfaceModifier::new));

    @Override
    public void processChunk(RandomSource random, ChunkAccess chunkAccess) {
        for (int dX = 0; dX < 16; dX++) {
            for (int dZ = 0; dZ < 16; dZ++) {
                int y = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, dX, dZ);
                BlockPos chunkMiddle = chunkAccess.getPos().getBlockAt(dX, y + this.height, dZ);
                chunkAccess.setBlockState(chunkMiddle, blockState.getState(random, chunkMiddle), false);
            }
        }
    }

    @Override
    public ChunkSurfaceModifierType getType() {
        return ChunkSurfaceModifiers.PROTOTYPE_MODIFIER.value();
    }
}
