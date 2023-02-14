package us.drullk.examplemod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public static final ResourceKey<Registry<ExampleObject>> TEST_REGISTRY_KEY = ResourceKey.createRegistryKey(ExampleMod.prefix("test_registry"));
    public static final DeferredRegister<ExampleObject> TEST_REGISTER = DeferredRegister.create(TEST_REGISTRY_KEY, ExampleMod.MODID);
    public static final Supplier<IForgeRegistry<ExampleObject>> REGISTRY_SUPPLIER = TEST_REGISTER.makeRegistry(() -> new RegistryBuilder<ExampleObject>().hasTags().disableSync());

    public static final ResourceKey<ExampleObject> TEST_OBJECT = ResourceKey.create(TEST_REGISTRY_KEY, ExampleMod.prefix("test_object"));

    public ExampleMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        TEST_REGISTER.register(modEventBus);
        modEventBus.addListener(ExampleMod::setRegistryDatapack);
        modEventBus.addListener(ExampleDataGen::gatherData);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static void setRegistryDatapack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(TEST_REGISTRY_KEY, ExampleObject.CODEC);
    }
}
