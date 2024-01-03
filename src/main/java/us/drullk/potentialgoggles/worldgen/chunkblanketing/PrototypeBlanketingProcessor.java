package us.drullk.potentialgoggles.worldgen.chunkblanketing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import us.drullk.potentialgoggles.content.ChunkBlanketings;

import java.util.function.Function;

public record PrototypeBlanketingProcessor(HolderSet<Biome> biomesForApplication, BlockStateProvider blockState, int height) implements ChunkBlanketingProcessor {
    public static final Codec<PrototypeBlanketingProcessor> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registries.BIOME, false).fieldOf("biome_filter").forGetter(PrototypeBlanketingProcessor::biomesForApplication),
            BlockStateProvider.CODEC.fieldOf("blockstate").forGetter(PrototypeBlanketingProcessor::blockState),
            Codec.INT.fieldOf("height").forGetter(PrototypeBlanketingProcessor::height)
    ).apply(inst, PrototypeBlanketingProcessor::new));

    @Override
    public void processChunk(RandomSource random, Function<BlockPos, Holder<Biome>> biomeGetter, ChunkAccess chunkAccess) {
        for (int dX = 0; dX < 16; dX++) {
            for (int dZ = 0; dZ < 16; dZ++) {
                int y = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, dX, dZ);
                BlockPos posInChunk = chunkAccess.getPos().getBlockAt(dX, y + this.height, dZ);

                if (!this.biomesForApplication().contains(biomeGetter.apply(posInChunk)))
                    continue;

                chunkAccess.setBlockState(posInChunk, this.blockState.getState(random, posInChunk), false);
            }
        }
    }

    @Override
    public ChunkBlanketingType getType() {
        return ChunkBlanketings.PROTOTYPE_BLANKETING.value();
    }
}
