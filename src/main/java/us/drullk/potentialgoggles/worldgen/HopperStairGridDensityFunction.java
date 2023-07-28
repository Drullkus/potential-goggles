package us.drullk.potentialgoggles.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.function.Supplier;

public class HopperStairGridDensityFunction implements DensityFunction.SimpleFunction {
    public static final Supplier<HopperStairGridDensityFunction> INSTANCE = Suppliers.memoize(HopperStairGridDensityFunction::new);
    public static final Codec<HopperStairGridDensityFunction> CODEC = Codec.unit(INSTANCE);
    public static final KeyDispatchDataCodec<HopperStairGridDensityFunction> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    private final int size;
    private final int halfSize;

    private HopperStairGridDensityFunction() {
        this.size = 128;
        this.halfSize = this.size / 2;
    }

    @Override
    public double compute(FunctionContext context) {
        int xRescale = this.rescaleCoord(context.blockX());
        int zRescale = this.rescaleCoord(context.blockZ());

        //int y = context instanceof NoiseChunk noiseChunk ? context.blockY() - noiseChunk.noiseSettings.minY() : context.blockY();
        int y = context.blockY();

        return Mth.clamp(this.getValue(this.boxCoord(xRescale), y, this.boxCoord(zRescale)), -300, 300);
    }

    private int rescaleCoord(int coord) {
        return (coord + this.size * 3) / 3;
    }

    private int boxCoord(int coord) {
        return Math.floorMod(coord, this.size) - this.halfSize;
    }

    private int getValue(int x, int y, int z) {
        //return y <= Math.max(Mth.abs(x), Mth.abs(z)) * 4;
        return ((x == 0 && z == 0 ? 3 : getHeight(x, z)) + Math.max(Mth.abs(x), Mth.abs(z)) * 4) - y;
    }

    private static int getHeight(int x, int z) {
        int absX = Math.abs(x);
        int absZ = Math.abs(z);

        if (x == z) {
            return x > 0 ? 3 : 1;
        } else if (x == -z) {
            return z > 0 ? 2 : 3;
        } else if (absX > absZ) {
            return x >= 0 ? 3 : 1;
        } else if (absZ > absX) {
            return z > 0 ? 2 : 0;
        }

        return 0;
    }

    @Override
    public double minValue() {
        return -300;
    }

    @Override
    public double maxValue() {
        return 300;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return KEY_CODEC;
    }
}
