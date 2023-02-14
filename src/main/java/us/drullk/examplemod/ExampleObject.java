package us.drullk.examplemod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public record ExampleObject(Block block) {
    public static final Codec<ExampleObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("test_entry").forGetter(ExampleObject::block)
    ).apply(instance, ExampleObject::new));
}
