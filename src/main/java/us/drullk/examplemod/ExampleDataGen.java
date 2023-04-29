package us.drullk.examplemod;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ExampleDataGen {
    private static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder().add(ExampleMod.TEST_REGISTRY_KEY, ExampleDataGen::testGenerate);

    public static void testGenerate(BootstapContext<ExampleObject> context) {
        context.register(ExampleMod.TEST_OBJECT, new ExampleObject(Blocks.DIAMOND_BLOCK));
    }

    public static void gatherData(GatherDataEvent event) {
        boolean isServer = event.includeServer();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(isServer, new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, REGISTRY_SET_BUILDER, Collections.singleton(ExampleMod.MODID)));
        //generator.addProvider(isServer, new ExampleTagGen(packOutput, lookupProvider, event.getExistingFileHelper()));
    }
}
