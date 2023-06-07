package us.drullk.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public ExampleMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ExampleObjects.TYPE_REGISTER.register(modEventBus);
        ExampleObjects.TEST_REGISTER.register(modEventBus);
        ExampleWorldgen.DENSITY_FUNCTIONS.register(modEventBus);

        modEventBus.addListener(ExampleObjects::setRegistryDatapack);
        modEventBus.addListener(ExampleDataGen::gatherData);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }
}
