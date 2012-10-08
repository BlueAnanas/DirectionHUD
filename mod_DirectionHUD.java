package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import org.lwjgl.opengl.GL11;
import bspkrs.util.client.HUDUtils;
import bspkrs.util.ModVersionChecker;

import net.minecraft.client.Minecraft;

public class mod_DirectionHUD extends BaseMod 
{
    protected float zLevel = 0.0F;
    private ScaledResolution scaledResolution;
    @MLProp(info="Set to true to allow checking for mod updates, false to disable")
    public static boolean allowUpdateCheck = true;
    @MLProp(info="Valid alignment strings are topleft, topcenter, topright, middleleft, middlecenter, middleright, bottomleft, bottomcenter, bottomright")
    public static String alignMode = "topcenter";
    @MLProp(info="Valid color values are 0-9, a-f (color values can be found here: http://www.minecraftwiki.net/wiki/File:Colors.png)")
    public static String markerColor = "c";
    @MLProp(info="Horizontal offset from the edge of the screen (when using right alignments the x offset is relative to the right edge of the screen)")
    public static int xOffset = 2;
    @MLProp(info="Vertical offset from the edge of the screen (when using bottom alignments the y offset is relative to the bottom edge of the screen)")
    public static int yOffset = 2;
    @MLProp(info="Vertical offset used only for the bottomcenter alignment to avoid the vanilla HUD")
    public static int yOffsetBottomCenter = 41;
    @MLProp(info="Set to true if you want the xOffset value to be applied when using a center alignment")
    public static boolean applyXOffsetToCenter = false;
    @MLProp(info="Set to true if you want the yOffset value to be applied when using a middle alignment")
    public static boolean applyYOffsetToMiddle = false;
    @MLProp(info="Set to true to show info when chat is open, false to disable info when chat is open\n\n**ONLY EDIT WHAT IS BELOW THIS**")
    public static boolean showInChat = true;
    
    private boolean checkUpdate;
    private ModVersionChecker versionChecker;
    private String versionURL = "https://dl.dropbox.com/u/20748481/Minecraft/1.3.1/directionHUD.version";
    private String mcfTopic = "http://www.minecraftforum.net/topic/1114612-";
    
	public mod_DirectionHUD() 
	{
        ModLoader.setInGameHook(this, true, false);
        versionChecker = new ModVersionChecker(getName(), getVersion(), versionURL, mcfTopic, ModLoader.getLogger());
        checkUpdate = allowUpdateCheck;
	}

    @Override
    public String getName() 
    {
        return "DirectionHUD";
    }

	@Override
	public String getVersion() 
	{
		return "v1.52(1.3.2)";
	}

	@Override
	public void load() 
	{
        versionChecker.checkVersionWithLogging();
    }

    @Override
	public boolean onTickInGame(float f, Minecraft mc)
	{
		if((mc.inGameHasFocus || mc.currentScreen == null || (mc.currentScreen instanceof GuiChat && showInChat)) 
		        && !mc.gameSettings.showDebugInfo && !mc.gameSettings.keyBindPlayerList.pressed)
        {
            scaledResolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            displayHUD(mc);
        }
        
        if(checkUpdate)
        {
            if(!versionChecker.isCurrentVersion())
                for(String msg : versionChecker.getInGameMessage())
                    mc.thePlayer.addChatMessage(msg);
            checkUpdate = false;
        }
		return true;
	}

    private int getX(int width)
    {
        if(alignMode.equalsIgnoreCase("topcenter") || alignMode.equalsIgnoreCase("middlecenter") || alignMode.equalsIgnoreCase("bottomcenter"))
            return scaledResolution.getScaledWidth() / 2 - width / 2 + (applyXOffsetToCenter ? xOffset : 0);
        else if(alignMode.equalsIgnoreCase("topright") || alignMode.equalsIgnoreCase("middleright")|| alignMode.equalsIgnoreCase("bottomright"))
            return scaledResolution.getScaledWidth() - width - xOffset;
        else
            return xOffset;
    }

    private int getY(int rowCount, int height)
    {
    	if(alignMode.equalsIgnoreCase("middleleft") || alignMode.equalsIgnoreCase("middlecenter") || alignMode.equalsIgnoreCase("middleright"))
    		return (scaledResolution.getScaledHeight()/2) - ((rowCount * height)/2) + (applyYOffsetToMiddle ? yOffset : 0);
    	else if(alignMode.equalsIgnoreCase("bottomleft") || alignMode.equalsIgnoreCase("bottomright"))
    		return scaledResolution.getScaledHeight() - (rowCount * height) - yOffset;
    	else if(alignMode.equalsIgnoreCase("bottomcenter"))
    		return scaledResolution.getScaledHeight() - (rowCount * height) - yOffsetBottomCenter;
    	else
    		return yOffset;
    }
	
	private void displayHUD(Minecraft mc)
    {
		int direction = MathHelper.floor_double((double)((mc.thePlayer.rotationYaw * 256F) / 360F) + 0.5D) & 255;
        int guiTexture = mc.renderEngine.getTexture("/gui/compass.png");

        int yBase = getY(1,12);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(guiTexture);
        int xBase = getX(65);
        if(direction < 128)
            HUDUtils.drawTexturedModalRect(xBase, yBase, direction, 0, 65, 12, zLevel);
        else
            HUDUtils.drawTexturedModalRect(xBase, yBase, direction-128, 12, 65, 12, zLevel);
        mc.fontRenderer.drawString("\247" + markerColor.toLowerCase() + "|", xBase + 32, yBase+1, 0xffffff);
        mc.fontRenderer.drawString("\247" + markerColor.toLowerCase() + "|", xBase + 32, yBase+5, 0xffffff);
    }
}
