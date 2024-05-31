package us.drullk.potentialgoggles.experimental;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import us.drullk.potentialgoggles.content.ExampleObjects;

public interface ExampleObject {
    ExampleType getType();

    record ExampleBlock(Block block) implements ExampleObject {
        public static final MapCodec<ExampleBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("test_block").forGetter(ExampleBlock::block)
        ).apply(instance, ExampleBlock::new));

        @Override
        public ExampleType getType() {
            return ExampleObjects.BLOCK.get();
        }
    }

    record ExampleItem(Item item) implements ExampleObject {
        public static final MapCodec<ExampleItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("test_item").forGetter(ExampleItem::item)
        ).apply(instance, ExampleItem::new));

        @Override
        public ExampleType getType() {
            return ExampleObjects.ITEM.get();
        }
    }

    record ExampleFused(ExampleObject first, ExampleObject second) implements ExampleObject {
        public static final MapCodec<ExampleFused> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExampleObjects.DISPATCH_CODEC.fieldOf("first").forGetter(ExampleFused::first),
                ExampleObjects.DISPATCH_CODEC.fieldOf("second").forGetter(ExampleFused::second)
        ).apply(instance, ExampleFused::new));

        @Override
        public ExampleType getType() {
            return ExampleObjects.FUSED.get();
        }
    }
}
