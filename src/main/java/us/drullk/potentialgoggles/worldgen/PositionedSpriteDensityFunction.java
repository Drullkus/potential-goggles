package us.drullk.potentialgoggles.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import us.drullk.potentialgoggles.content.GogglesByteMaps;

import java.util.function.Supplier;

public class PositionedSpriteDensityFunction implements DensityFunction.SimpleFunction {
    public static final Codec<PositionedSpriteDensityFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryFileCodec.create(GogglesByteMaps.BYTE_MAP_REGISTRY_KEY, ByteMap.CODEC, false).fieldOf("bytemap").forGetter(f -> f.imageHolder),
            Codec.DOUBLE.fieldOf("min_value").forGetter(PositionedSpriteDensityFunction::minValue),
            Codec.DOUBLE.fieldOf("max_value").forGetter(PositionedSpriteDensityFunction::maxValue),
            Codec.INT.fieldOf("x_min").forGetter(f -> f.xMin),
            Codec.INT.fieldOf("z_min").forGetter(f -> f.zMin),
            Codec.INT.fieldOf("x_max").forGetter(f -> f.xMax),
            Codec.INT.fieldOf("z_max").forGetter(f -> f.zMax)
    ).apply(instance, PositionedSpriteDensityFunction::new));
    public static final KeyDispatchDataCodec<PositionedSpriteDensityFunction> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    private final double minValue;
    private final double maxValue;
    private final int xMin, zMin, xMax, zMax;
    private final double rescale;

    private final Holder<ByteMap> imageHolder;
    private final Supplier<ByteMap> imageGetter;

    public static PositionedSpriteDensityFunction fromBox(Holder<ByteMap> image, double minValue, double maxValue, BoundingBox box) {
        return new PositionedSpriteDensityFunction(image, minValue, maxValue, box.minX(), box.minZ(), box.maxX(), box.maxZ());
    }

    public PositionedSpriteDensityFunction(Holder<ByteMap> image, double minValue, double maxValue, int xMin, int zMin, int xMax, int zMax) {
        this.minValue = minValue;
        this.maxValue = maxValue;

        this.xMin = xMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.zMax = zMax;

        this.rescale = (this.maxValue - this.minValue) / 255f;

        this.imageHolder = image;
        this.imageGetter = Suppliers.memoize(this.imageHolder::value);
    }

    @Override
    public double compute(FunctionContext pContext) {
        ByteMap byteMap = this.imageGetter.get();

        float x = Mth.clampedMap(pContext.blockX(), this.xMin, this.xMax, 0, byteMap.sizeX - 1);
        float z = Mth.clampedMap(pContext.blockZ(), this.zMin, this.zMax, 0, byteMap.sizeY - 1);

        float pixelAt = this.linearApproximation(x, z);
        return this.minValue + pixelAt * this.rescale;
    }

    public float linearApproximation(float x, float z) {
        int minX = Mth.floor(x);
        int maxX = Mth.ceil(x);
        int minZ = Mth.floor(z);
        int maxZ = Mth.ceil(z);

        if (minX != maxX) {
            if (minZ != maxZ) {
                return Mth.map(x, minX, maxX, Mth.map(z, minZ, maxZ, this.imageGetter.get().wrappedGetByte(minX, minZ), this.imageGetter.get().wrappedGetByte(minX, maxZ)), Mth.map(z, minZ, maxZ, this.imageGetter.get().wrappedGetByte(maxX, minZ), this.imageGetter.get().wrappedGetByte(maxX, maxZ)));
            } else {
                return Mth.map(x, minX, maxX, this.imageGetter.get().wrappedGetByte(minX, minZ), this.imageGetter.get().wrappedGetByte(maxX, minZ));
            }
        } else if (minZ != maxZ) {
            return Mth.map(z, minZ, maxZ, this.imageGetter.get().wrappedGetByte(minX, minZ), this.imageGetter.get().wrappedGetByte(minX, maxZ));
        } else {
            return this.imageGetter.get().wrappedGetByte(minX, minZ);
        }
    }

    @Override
    public double minValue() {
        return this.minValue;
    }

    @Override
    public double maxValue() {
        return this.maxValue;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return KEY_CODEC;
    }
}
