package us.drullk.potentialgoggles.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.ByteMap;

import java.util.function.Supplier;

public class GogglesByteMaps {
    // TODO add types, and create one that deserializes from an image directly instead of requiring datagen
    public static final ResourceKey<Registry<ByteMap>> BYTE_MAP_REGISTRY_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("byte_maps"));
    public static final DeferredRegister<ByteMap> BYTE_MAPS = DeferredRegister.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.MODID);
    public static final Supplier<IForgeRegistry<ByteMap>> BYTE_MAP_REGISTRY_SUPPLIER = BYTE_MAPS.makeRegistry(() -> new RegistryBuilder<ByteMap>().disableSync());
    public static final Codec<ByteMap> REGISTRY_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BYTE_MAP_REGISTRY_SUPPLIER.get().getCodec());

    public static final ResourceKey<ByteMap> DENSITY_CLAW = ResourceKey.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.prefix("density_claw"));
    public static final ResourceKey<ByteMap> DENSITY_SPIRAL = ResourceKey.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.prefix("density_spiral"));
    public static final ResourceKey<ByteMap> SEQUENTIAL_COUNTING_BYTES = ResourceKey.create(BYTE_MAP_REGISTRY_KEY, PotentialGoggles.prefix("sequential_counting"));

    public static void setRegistryDatapack(DataPackRegistryEvent.NewRegistry event) {
        //event.dataPackRegistry(BYTE_MAP_REGISTRY_KEY, ExampleByteMaps.REGISTRY_CODEC);
        event.dataPackRegistry(BYTE_MAP_REGISTRY_KEY, ByteMap.CODEC);
    }
}