package us.drullk.potentialgoggles.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.*;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.experimental.ExampleObject;
import us.drullk.potentialgoggles.experimental.ExampleType;

import java.util.function.Supplier;

public class ExampleObjects {
    public static final ResourceKey<Registry<ExampleType>> TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("type_registry"));
    public static final DeferredRegister<ExampleType> TYPE_REGISTER = DeferredRegister.create(TYPE_REGISTRY_KEY, PotentialGoggles.MODID);
    public static final Supplier<IForgeRegistry<ExampleType>> TYPE_REGISTRY_SUPPLIER = TYPE_REGISTER.makeRegistry(() -> new RegistryBuilder<ExampleType>().disableSync());
    public static final Codec<ExampleType> TYPE_CODEC = ExtraCodecs.lazyInitializedCodec(() -> TYPE_REGISTRY_SUPPLIER.get().getCodec());
    //public static final Supplier<IForgeRegistry<ExampleObject>> TEST_REGISTRY_SUPPLIER = TEST_REGISTER.makeRegistry(() -> new RegistryBuilder<ExampleObject>().disableSync());
    public static final Codec<ExampleObject> DISPATCH_CODEC = TYPE_CODEC.dispatch("example_type", ExampleObject::getType, ExampleType::getCodec);
    public static final RegistryObject<ExampleType> BLOCK = TYPE_REGISTER.register("block", () -> () -> ExampleObject.ExampleBlock.CODEC);
    public static final RegistryObject<ExampleType> ITEM = TYPE_REGISTER.register("item", () -> () -> ExampleObject.ExampleItem.CODEC);
    public static final RegistryObject<ExampleType> FUSED = TYPE_REGISTER.register("fused", () -> () -> ExampleObject.ExampleFused.CODEC);

    public static final ResourceKey<Registry<ExampleObject>> TEST_REGISTRY_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("test_registry"));
    public static final ResourceKey<ExampleObject> TEST_ITEM = ResourceKey.create(TEST_REGISTRY_KEY, PotentialGoggles.prefix("item_wrapper"));
    public static final ResourceKey<ExampleObject> TEST_BLOCK = ResourceKey.create(TEST_REGISTRY_KEY, PotentialGoggles.prefix("block_wrapper"));
    public static final DeferredRegister<ExampleObject> TEST_REGISTER = DeferredRegister.create(TEST_REGISTRY_KEY, PotentialGoggles.MODID);

    public static void setRegistryDatapack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(TEST_REGISTRY_KEY, DISPATCH_CODEC);
    }
}
