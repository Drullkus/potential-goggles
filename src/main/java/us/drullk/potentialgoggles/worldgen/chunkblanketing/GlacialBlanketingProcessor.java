package us.drullk.potentialgoggles.worldgen.chunkblanketing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import us.drullk.potentialgoggles.content.ChunkBlanketings;

public record GlacialBlanketingProcessor(HolderSet<Biome> biomesForApplication, BlockStateProvider glacialBlocks, BlockStateProvider topBlocks, int height) implements ChunkBlanketingProcessor.SimpleProcessor {
    public static final Codec<GlacialBlanketingProcessor> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registries.BIOME, false).fieldOf("biome_filter").forGetter(GlacialBlanketingProcessor::biomesForApplication),
            BlockStateProvider.CODEC.fieldOf("glacial_blocks").forGetter(GlacialBlanketingProcessor::glacialBlocks),
            BlockStateProvider.CODEC.fieldOf("top_blocks").forGetter(GlacialBlanketingProcessor::topBlocks),
            Codec.INT.fieldOf("height").forGetter(GlacialBlanketingProcessor::height)
    ).apply(inst, GlacialBlanketingProcessor::new));

    @Override
    public void processColumn(RandomSource random, ChunkAccess chunkAccess, BlockPos aboveFloor) {
        int firstAvailableY = aboveFloor.getY();
        int maxY = firstAvailableY + this.height;

        BlockPos maxPosY = aboveFloor.atY(maxY);
        chunkAccess.setBlockState(maxPosY, this.topBlocks.getState(random, maxPosY), false);

        for (int y = maxY - 1; y >= firstAvailableY; y--) {
            BlockPos posSurfaceChunk = aboveFloor.atY(y);
            chunkAccess.setBlockState(posSurfaceChunk, this.glacialBlocks.getState(random, posSurfaceChunk), false);
        }
    }

    @Override
    public Heightmap.Types heightmap() {
        return Heightmap.Types.OCEAN_FLOOR_WG;
    }

    @Override
    public ChunkBlanketingType getType() {
        return ChunkBlanketings.GLACIAL_BLANKETING.value();
    }
}
