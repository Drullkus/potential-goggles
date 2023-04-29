package us.drullk.examplemod;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public static final ResourceKey<Registry<ExampleType>> TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ExampleMod.prefix("type_registry"));
    public static final DeferredRegister<ExampleType> TYPE_REGISTER = DeferredRegister.create(TYPE_REGISTRY_KEY, ExampleMod.MODID);
    public static final Supplier<IForgeRegistry<ExampleType>> TYPE_REGISTRY_SUPPLIER = TYPE_REGISTER.makeRegistry(() -> new RegistryBuilder<ExampleType>().disableSync());
    public static final Codec<ExampleType> TYPE_CODEC = ExtraCodecs.lazyInitializedCodec(() -> TYPE_REGISTRY_SUPPLIER.get().getCodec());

    public static final RegistryObject<ExampleType> BLOCK = registerType("block", () -> () -> ExampleObject.ExampleBlock.CODEC);
    public static final RegistryObject<ExampleType> ITEM = registerType("item", () -> () -> ExampleObject.ExampleItem.CODEC);
    public static final RegistryObject<ExampleType> FUSED = registerType("fused", () -> () -> ExampleObject.ExampleFused.CODEC);

    private static RegistryObject<ExampleType> registerType(String name, Supplier<ExampleType> factory) {
        return TYPE_REGISTER.register(name, factory);
    }

    public static final ResourceKey<Registry<ExampleObject>> TEST_REGISTRY_KEY = ResourceKey.createRegistryKey(ExampleMod.prefix("test_registry"));
    public static final DeferredRegister<ExampleObject> TEST_REGISTER = DeferredRegister.create(TEST_REGISTRY_KEY, ExampleMod.MODID);
    //public static final Supplier<IForgeRegistry<ExampleObject>> TEST_REGISTRY_SUPPLIER = TEST_REGISTER.makeRegistry(() -> new RegistryBuilder<ExampleObject>().disableSync());
    public static final Codec<ExampleObject> DISPATCH_CODEC = TYPE_CODEC.dispatch("example_type", ExampleObject::getType, ExampleType::getCodec);

    public static final ResourceKey<ExampleObject> TEST_BLOCK = ResourceKey.create(TEST_REGISTRY_KEY, ExampleMod.prefix("block_wrapper"));
    public static final ResourceKey<ExampleObject> TEST_ITEM = ResourceKey.create(TEST_REGISTRY_KEY, ExampleMod.prefix("item_wrapper"));

    public ExampleMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        TYPE_REGISTER.register(modEventBus);
        TEST_REGISTER.register(modEventBus);
        modEventBus.addListener(ExampleMod::setRegistryDatapack);
        modEventBus.addListener(ExampleDataGen::gatherData);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static void setRegistryDatapack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(TEST_REGISTRY_KEY, DISPATCH_CODEC);
    }
}
