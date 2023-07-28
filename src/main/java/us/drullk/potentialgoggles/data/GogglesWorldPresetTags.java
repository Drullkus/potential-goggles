package us.drullk.potentialgoggles.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.tags.WorldPresetTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import us.drullk.potentialgoggles.PotentialGoggles;
import us.drullk.potentialgoggles.worldgen.GogglesKeys;

import java.util.concurrent.CompletableFuture;

public class GogglesWorldPresetTags extends WorldPresetTagsProvider {
    public GogglesWorldPresetTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, PotentialGoggles.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //this.tag(WorldPresetTags.NORMAL).add(ExampleKeys.TEST_PRESET);
        this.tag(WorldPresetTags.NORMAL).addOptional(GogglesKeys.TEST_PRESET.location());
    }
}
