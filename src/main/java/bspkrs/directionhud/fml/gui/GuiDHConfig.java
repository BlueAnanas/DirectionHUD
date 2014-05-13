package bspkrs.directionhud.fml.gui;

import net.minecraft.client.gui.GuiScreen;
import bspkrs.directionhud.fml.Reference;
import bspkrs.util.config.ConfigProperty;
import bspkrs.util.config.Configuration;
import bspkrs.util.config.gui.GuiConfig;

public class GuiDHConfig extends GuiConfig
{
    public GuiDHConfig(GuiScreen parent) throws NoSuchMethodException, SecurityException
    {
        super(parent, (new ConfigProperty(Reference.config.getCategory(Configuration.CATEGORY_GENERAL))).getConfigPropertiesList(true),
                true, Reference.MODID, true, GuiConfig.getAbridgedConfigPath(Reference.config.toString()));
    }
}
