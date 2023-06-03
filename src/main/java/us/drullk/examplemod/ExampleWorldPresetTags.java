package us.drullk.examplemod;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.tags.WorldPresetTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ExampleWorldPresetTags extends WorldPresetTagsProvider {
    public ExampleWorldPresetTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, ExampleMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //this.tag(WorldPresetTags.NORMAL).add(ExampleKeys.TEST_PRESET);
        this.tag(WorldPresetTags.NORMAL).addOptional(ExampleKeys.TEST_PRESET.location());
    }
}
