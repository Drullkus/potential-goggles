package us.drullk.potentialgoggles;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.drullk.potentialgoggles.content.GogglesByteMaps;
import us.drullk.potentialgoggles.content.GogglesWorldgen;
import us.drullk.potentialgoggles.data.GogglesDataGen;
import us.drullk.potentialgoggles.content.ExampleObjects;

@Mod(PotentialGoggles.MODID)
public class PotentialGoggles {
    public static final String MODID = "potential_goggles";
    public static final Logger LOGGER = LogManager.getLogger();

    public PotentialGoggles(IEventBus modEventBus) {
        ExampleObjects.TYPE_REGISTER.register(modEventBus);
        ExampleObjects.TEST_REGISTER.register(modEventBus);
        GogglesWorldgen.DENSITY_FUNCTIONS.register(modEventBus);
        GogglesWorldgen.STRUCTURE_TYPES.register(modEventBus);
        GogglesWorldgen.STRUCTURE_PIECES.register(modEventBus);
        GogglesByteMaps.BYTE_MAPS.register(modEventBus);

        modEventBus.addListener(ExampleObjects::setRegistryDatapack);
        modEventBus.addListener(GogglesByteMaps::setRegistryDatapack);
        modEventBus.addListener(GogglesDataGen::gatherData);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }
}
