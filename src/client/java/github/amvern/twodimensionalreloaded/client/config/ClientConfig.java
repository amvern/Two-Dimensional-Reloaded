package github.amvern.twodimensionalreloaded.client.config;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = TwoDimensionalReloaded.MOD_ID)
public class ClientConfig implements ConfigData {
    public boolean renderBlockPlacementGuide = false;
    public boolean shouldRenderPlacementOutline = true;
    public RenderStyle blockRenderMode = RenderStyle.GHOST_BLOCK;
    public CameraMode cameraMode = CameraMode.DYNAMIC;

    public enum CameraMode {
        STABLE,
        DYNAMIC
    }

    public enum RenderStyle {
        FULL_BLOCK,
        GHOST_BLOCK,
        OUTLINE_ONLY
    }

    public int placeableOutlineColor = 0x8000FF00;
    public int nonPlaceableOutlineColor = 0x80FF0000;
    public float placementOutlineWidth = 4f;

    public boolean renderFogEnvironments = true;
}