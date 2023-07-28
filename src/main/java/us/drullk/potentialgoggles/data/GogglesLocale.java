package us.drullk.potentialgoggles.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import us.drullk.potentialgoggles.PotentialGoggles;

public class GogglesLocale extends LanguageProvider {
    public GogglesLocale(PackOutput output) {
        super(output, PotentialGoggles.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("generator." + PotentialGoggles.MODID + ".testing_preset", "Test Preset");
    }
}
