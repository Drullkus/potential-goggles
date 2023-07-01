package us.drullk.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ExampleObjects.TYPE_REGISTER.register(modEventBus);
        ExampleObjects.TEST_REGISTER.register(modEventBus);
        ExampleWorldgen.DENSITY_FUNCTIONS.register(modEventBus);
        ExampleByteMaps.BYTE_MAPS.register(modEventBus);

        modEventBus.addListener(ExampleObjects::setRegistryDatapack);
        modEventBus.addListener(ExampleByteMaps::setRegistryDatapack);
        modEventBus.addListener(ExampleDataGen::gatherData);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }
}
