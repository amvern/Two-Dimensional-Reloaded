package github.mishkis.twodimensional.client.rendering;

import com.mojang.blaze3d.platform.Window;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.resources.ResourceLocation;

public class TwoDimensionalCrosshairRenderer {
    public static void intialize() {
        HudRenderCallback.EVENT.register(((guiGraphics, tickDelta) -> {
            if (TwoDimensionalClient.plane != null) {
                Minecraft minecraft = Minecraft.getInstance();
                MouseHandler mouse = minecraft.mouseHandler;
                Window window = minecraft.getWindow();

                double mouseX = mouse.xpos() * guiGraphics.guiWidth() / window.getScreenWidth();
                double mouseY = mouse.ypos() * guiGraphics.guiHeight() / window.getScreenHeight();
                guiGraphics.blit(new ResourceLocation("textures/gui/icons.png"), (int)(mouseX) - 7, (int)(mouseY) - 7, 0, 0, 15, 15, 265, 265);
            }
        }));
    }
}