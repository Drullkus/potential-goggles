package us.drullk.examplemod.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import us.drullk.examplemod.ExampleMod;

public class ExampleLocale extends LanguageProvider {
    public ExampleLocale(PackOutput output) {
        super(output, ExampleMod.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("generator.examplemod.testing_preset", "Test Preset");
    }
}
