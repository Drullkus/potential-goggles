package us.drullk.potentialgoggles.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.BitStorage;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import us.drullk.potentialgoggles.PotentialGoggles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

public class ByteMap {
    public static final Codec<ByteMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("size_x").forGetter(m -> m.sizeX),
            Codec.INT.fieldOf("size_y").forGetter(m -> m.sizeY),
            Codec.STRING.fieldOf("packed_data").forGetter(ByteMap::encode)
    ).apply(instance, ByteMap::new));

    private static final float RED_GRAY_WEIGHT = 0.299f;
    private static final float GREEN_GRAY_WEIGHT = 0.587f;
    private static final float BLUE_GRAY_WEIGHT = 0.114f;

    private final BitStorage storedImage;
    public final int sizeX, sizeY;

    public static ByteMap initForExternalImage(String fileName) {
        if (FMLLoader.isProduction())
            throw new RuntimeException("This method is only permitted in-dev!");

        // Looks in the root dir for this IDE project (exits /potential-goggles/runs/data/ into the main /potential-goggles/ directory)
        return initForExternalImage(FMLPaths.GAMEDIR.get().resolve("../../bytemap_sprites/" + fileName + ".png").toFile());
    }

    public static ByteMap initForExternalImage(File file) {
        try {
            return new ByteMap(ImageIO.read(file));
        } catch (IOException e) {
            PotentialGoggles.LOGGER.error("Failed to read file " + file, e);
            throw new RuntimeException(e);
        }
    }

    public ByteMap(BufferedImage image) {
        this.sizeX = image.getWidth();
        this.sizeY = image.getHeight();
        int size = this.sizeX * this.sizeY;

        this.storedImage = new SimpleBitStorage(8, size);

        for (int i = 0; i < size; i++) {
            int x = this.xFromIndex(i);
            int y = this.yFromIndex(i);
            int color = image.getRGB(x, y);

            float r = FastColor.ARGB32.red(color) * RED_GRAY_WEIGHT;
            float g = FastColor.ARGB32.green(color) * GREEN_GRAY_WEIGHT;
            float b = FastColor.ARGB32.blue(color) * BLUE_GRAY_WEIGHT;

            this.storedImage.set(i, Mth.clamp((int) (r + g + b), 0, 255));
        }
    }

    public ByteMap(int sizeX, int sizeY, PixelFunction perPixelGenerator) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        int size = this.sizeX * this.sizeY;

        this.storedImage = new SimpleBitStorage(8, size);

        for (int i = 0; i < size; i++) {
            int x = this.xFromIndex(i);
            int y = this.yFromIndex(i);
            this.storedImage.set(i, perPixelGenerator.forPixel(x, y) & 0xFF);
        }
    }

    public ByteMap(int sizeX, int sizeY, String packedContents) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        int size = this.sizeX * this.sizeY;

        byte[] decode = Base64.getDecoder().decode(packedContents);
        this.storedImage = new SimpleBitStorage(8, size, bytesToLongs(decode));
    }

    private String encode() {
        return Base64.getEncoder().encodeToString(longsToBytes(this.storedImage.getRaw()));
    }

    private static byte[] longsToBytes(long[] longs) {
        var byteBuf = ByteBuffer.allocate(longs.length * 8).order(ByteOrder.LITTLE_ENDIAN);
        byteBuf.asLongBuffer().put(longs);
        return byteBuf.array();
    }

    private static long[] bytesToLongs(byte[] bytes) {
        long[] longs = new long[bytes.length / 8];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(longs);
        return longs;
    }

    public int wrappedGetByte(int x, int y) {
        int mapX = Math.floorMod(x, this.sizeX);
        int mapY = Math.floorMod(y, this.sizeY);

        return this.unwrappedGetByte(mapX, mapY);
    }

    private int unwrappedGetByte(int indexX, int indexY) {
        return this.storedImage.get(this.getIndex(indexX, indexY));
    }

    private int getIndex(int x, int y) {
        return x + y * this.sizeX;
    }

    private int xFromIndex(int index) {
        return index % this.sizeX;
    }

    private int yFromIndex(int index) {
        return index / this.sizeY;
    }

    @FunctionalInterface
    public interface PixelFunction {
        byte forPixel(int x, int y);
    }
}
