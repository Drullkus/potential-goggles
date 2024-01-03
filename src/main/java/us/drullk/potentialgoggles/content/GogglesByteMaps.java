package us.drullk.potentialgoggles.content;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.bytemap.ByteMap;

public class GogglesByteMaps {
    // TODO add types, and create one that deserializes from an image directly instead of requiring datagen
    public static final ResourceKey<Registry<ByteMap>> BYTE_MAP_REGISTRY_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("byte_maps"));
    public static final DeferredRegister<ByteMap> BYTE_MAPS = DeferredRegister.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.MODID);
    //public static final Registry<ByteMap> BYTE_MAP_REGISTRY_SUPPLIER = BYTE_MAPS.makeRegistry(builder -> builder.sync(false));
    //public static final Codec<ByteMap> REGISTRY_CODEC = ExtraCodecs.lazyInitializedCodec(BYTE_MAP_REGISTRY_SUPPLIER::byNameCodec);

    public static final ResourceKey<ByteMap> DENSITY_CLAW = ResourceKey.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.prefix("density_claw"));
    public static final ResourceKey<ByteMap> DENSITY_SPIRAL = ResourceKey.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.prefix("density_spiral"));
    public static final ResourceKey<ByteMap> SEQUENTIAL_COUNTING_BYTES = ResourceKey.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.prefix("sequential_counting"));

    public static void setRegistryDatapack(DataPackRegistryEvent.NewRegistry event) {
        //event.dataPackRegistry(BYTE_MAP_REGISTRY_KEY, ExampleByteMaps.REGISTRY_CODEC);
        event.dataPackRegistry(BYTE_MAP_REGISTRY_KEY, ByteMap.CODEC);
    }
}
