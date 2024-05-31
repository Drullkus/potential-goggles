package us.drullk.potentialgoggles.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

public record DistanceDensityFunction(float centerX, float centerY, float centerZ, float radius, float nearValue, float farValue) implements DensityFunction.SimpleFunction {
    public static MapCodec<DistanceDensityFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("x_center").orElse(0f).forGetter(f -> f.centerX),
            Codec.FLOAT.fieldOf("y_center").orElse(0f).forGetter(f -> f.centerY),
            Codec.FLOAT.fieldOf("z_center").orElse(0f).forGetter(f -> f.centerZ),
            Codec.FLOAT.fieldOf("radius").orElse(0f).forGetter(f -> f.centerX),
            Codec.FLOAT.fieldOf("near_value").orElse(0f).forGetter(f -> f.centerZ),
            Codec.FLOAT.fieldOf("far_value").orElse(0f).forGetter(f -> f.centerX)
    ).apply(instance, DistanceDensityFunction::new));
    public static final KeyDispatchDataCodec<DistanceDensityFunction> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    @Override
    public double compute(FunctionContext context) {
        float dX = context.blockX() - this.centerX;
        float dY = context.blockY() - this.centerY;
        float dZ = context.blockZ() - this.centerZ;

        float dist = Mth.sqrt(dX * dX + dY * dY + dZ * dZ);

        return Mth.clampedMap(dist, 0, this.radius, this.nearValue, this.farValue);
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return this.radius;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return KEY_CODEC;
    }
}
