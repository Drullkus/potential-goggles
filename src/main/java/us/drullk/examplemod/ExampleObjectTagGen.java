package us.drullk.examplemod;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ExampleObjectTagGen extends TagsProvider<ExampleObject> {
    public static final TagKey<ExampleObject> EXAMPLE_TAG_KEY = TagKey.create(ExampleObjects.TEST_REGISTRY_KEY, ExampleMod.prefix("test_tag"));

    protected ExampleObjectTagGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> providerCompletableFuture, ExistingFileHelper fileHelper) {
        super(packOutput, ExampleObjects.TEST_REGISTRY_KEY, providerCompletableFuture, ExampleMod.MODID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //tag(EXAMPLE_TAG_KEY).add(ExampleMod.TEST_OBJECT);
    }
}
