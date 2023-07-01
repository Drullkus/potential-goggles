package us.drullk.examplemod;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ExampleLocaleData extends LanguageProvider {
    public ExampleLocaleData(PackOutput output) {
        super(output, ExampleMod.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("generator.examplemod.testing_preset", "Test Preset");
    }
}
