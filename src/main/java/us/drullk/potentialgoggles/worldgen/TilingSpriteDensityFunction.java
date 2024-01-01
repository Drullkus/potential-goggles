package us.drullk.potentialgoggles.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import us.drullk.potentialgoggles.content.GogglesByteMaps;

import java.util.function.Supplier;

public class TilingSpriteDensityFunction implements DensityFunction.SimpleFunction {
    public static final Codec<TilingSpriteDensityFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryFileCodec.create(GogglesByteMaps.BYTE_MAP_REGISTRY_KEY, ByteMap.CODEC, false).fieldOf("bytemap").forGetter(f -> f.imageHolder),
            Codec.DOUBLE.fieldOf("min_value").forGetter(TilingSpriteDensityFunction::minValue),
            Codec.DOUBLE.fieldOf("max_value").forGetter(TilingSpriteDensityFunction::maxValue),
            Codec.FLOAT.fieldOf("x_scalar").orElse(1f).forGetter(f -> f.xScalar),
            Codec.FLOAT.fieldOf("z_scalar").orElse(1f).forGetter(f -> f.zScalar)
    ).apply(instance, TilingSpriteDensityFunction::new));
    public static final KeyDispatchDataCodec<TilingSpriteDensityFunction> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    private final double minValue;
    private final double maxValue;
    private final float xScalar;
    private final float zScalar;
    private final double rescale;

    private final Holder<ByteMap> imageHolder;
    private final Supplier<ByteMap> imageGetter;

    public TilingSpriteDensityFunction(Holder<ByteMap> image, double minValue, double maxValue, float xScalar, float zScalar) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.xScalar = xScalar;
        this.zScalar = zScalar;
        this.rescale = (this.maxValue - this.minValue) / 255f;

        this.imageHolder = image;
        this.imageGetter = Suppliers.memoize(this.imageHolder::get);
    }

    @Override
    public double compute(FunctionContext pContext) {
        float pixelAt = this.linearApproximation(pContext.blockX() * this.xScalar, pContext.blockZ() * this.zScalar);
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
