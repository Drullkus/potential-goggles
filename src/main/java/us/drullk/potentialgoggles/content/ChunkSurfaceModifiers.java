package us.drullk.potentialgoggles.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.ChunkSurfaceModifier;
import us.drullk.potentialgoggles.worldgen.ChunkSurfaceModifierType;
import us.drullk.potentialgoggles.worldgen.PrototypeChunkSurfaceModifier;

public class ChunkSurfaceModifiers {
    public static final ResourceKey<Registry<ChunkSurfaceModifierType>> CHUNK_SURFACE_MODIFIER_TYPE_REG_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("chunk_surface_modifier_types"));
    public static final DeferredRegister<ChunkSurfaceModifierType> CHUNK_SURFACE_MODIFIER_TYPES = DeferredRegister.create(CHUNK_SURFACE_MODIFIER_TYPE_REG_KEY, PotentialGoggles.MODID);
    public static final Registry<ChunkSurfaceModifierType> TYPE_REGISTRY_SUPPLIER = CHUNK_SURFACE_MODIFIER_TYPES.makeRegistry(builder -> builder.sync(false));
    public static final Codec<ChunkSurfaceModifierType> TYPE_CODEC = ExtraCodecs.lazyInitializedCodec(TYPE_REGISTRY_SUPPLIER::byNameCodec);
    public static final Codec<ChunkSurfaceModifier> DISPATCH_CODEC = TYPE_CODEC.dispatch("chunk_surface_modifier_type", ChunkSurfaceModifier::getType, ChunkSurfaceModifierType::getCodec);

    public static final DeferredHolder<ChunkSurfaceModifierType, ChunkSurfaceModifierType> PROTOTYPE_MODIFIER = CHUNK_SURFACE_MODIFIER_TYPES.register("prototype", () -> () -> PrototypeChunkSurfaceModifier.CODEC);

    public static final ResourceKey<Registry<ChunkSurfaceModifier>> CHUNK_SURFACE_MODIFIER_REG_KEY = ResourceKey.createRegistryKey(PotentialGoggles.prefix("chunk_surface_modifiers"));
    public static final DeferredRegister<ChunkSurfaceModifier> CHUNK_SURFACE_MODIFIERS = DeferredRegister.create(CHUNK_SURFACE_MODIFIER_REG_KEY, PotentialGoggles.MODID);
    public static final ResourceKey<ChunkSurfaceModifier> TEST = ResourceKey.create(CHUNK_SURFACE_MODIFIER_REG_KEY, PotentialGoggles.prefix("stronghold_biased"));
    public static final ResourceKey<ChunkSurfaceModifier> RIVER_TEST = ResourceKey.create(CHUNK_SURFACE_MODIFIER_REG_KEY, PotentialGoggles.prefix("river_test"));

    public static void setRegistryDatapack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(CHUNK_SURFACE_MODIFIER_REG_KEY, DISPATCH_CODEC);
    }

    public static void bootstrap(BootstapContext<ChunkSurfaceModifier> context) {
        HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);
        context.register(TEST, new PrototypeChunkSurfaceModifier(lookup.getOrThrow(BiomeTags.STRONGHOLD_BIASED_TO), BlockStateProvider.simple(Blocks.RED_STAINED_GLASS), 32));
        context.register(RIVER_TEST, new PrototypeChunkSurfaceModifier(lookup.getOrThrow(BiomeTags.IS_RIVER), BlockStateProvider.simple(Blocks.GLASS), 16));
    }
}
