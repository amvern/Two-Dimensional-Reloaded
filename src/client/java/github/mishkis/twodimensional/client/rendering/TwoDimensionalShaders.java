package github.mishkis.twodimensional.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.*;
import ladysnake.satin.api.util.GlMatrices;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class TwoDimensionalShaders implements PostWorldRenderCallback, ShaderEffectRenderCallback {
    public static final ResourceLocation PLANE_SHADERS_ID = new ResourceLocation(TwoDimensional.MOD_ID, "shaders/post/plane_shaders.json");
    public static final TwoDimensionalShaders INSTANCE = new TwoDimensionalShaders();

    private final Minecraft minecraftClient = Minecraft.getInstance();

    private final Matrix4f projectionMatrix = new Matrix4f();

    final ManagedShaderEffect PLANE_SHADERS = ShaderEffectManager.getInstance().manage(PLANE_SHADERS_ID, shader -> {
        shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)minecraftClient.getMainRenderTarget()).getStillDepthMap());
    });
    private final UniformMat4 uniformInverseTransformMatrix = PLANE_SHADERS.findUniformMat4("InverseTransformMatrix");
    private final Uniform3f uniformCameraPos = PLANE_SHADERS.findUniform3f("CameraPos");
    private final Uniform3f uniformPlayerPos = PLANE_SHADERS.findUniform3f("PlayerPos");
    private final Uniform3f uniformPlaneOffset = PLANE_SHADERS.findUniform3f("PlaneOffset");
    private final Uniform1f uniformPlaneSlope = PLANE_SHADERS.findUniform1f("PlaneSlope");
    private final Uniform3f uniformPlaneNormal = PLANE_SHADERS.findUniform3f("PlaneNormal");
    private final Uniform3f uniformSkyColor = PLANE_SHADERS.findUniform3f("SkyColor");
    private final Uniform2f uniformLightLevel = PLANE_SHADERS.findUniform2f("LightLevel");

    private Vector2f lightLevel = new Vector2f(15f);

    @Override
    public void onWorldRendered(Camera camera, float tickDelta, long nanoTime) {
        Plane plane = TwoDimensionalClient.plane;
        if (plane != null) {
            uniformInverseTransformMatrix.set(GlMatrices.getInverseTransformMatrix(projectionMatrix));

            uniformCameraPos.set(camera.getPosition().toVector3f());

            uniformPlaneOffset.set(plane.getOffset().toVector3f());
            uniformPlaneSlope.set((float) plane.getSlope());
            uniformPlaneNormal.set(plane.getNormal().toVector3f());

            float[] fogColor = RenderSystem.getShaderFogColor();
            uniformSkyColor.set(fogColor[0], fogColor[1], fogColor[2]);

            Level world = Minecraft.getInstance().level;
            Player player = Minecraft.getInstance().player;
            Vector2f targetLightLevel = new Vector2f(world.getBrightness(LightLayer.BLOCK, player.blockPosition()), world.getBrightness(LightLayer.SKY, player.blockPosition()));
            player.getHandSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof BlockItem blockItem) {
                    targetLightLevel.x = Math.max(targetLightLevel.x, Mth.clamp(blockItem.getBlock().defaultBlockState().getLightEmission(), 0f, 10f));
                }
            });

            lightLevel = lightLevel.lerp(targetLightLevel, 0.2f);
            uniformLightLevel.set(lightLevel);

            uniformPlayerPos.set(player.position().toVector3f());
        }
    }

    @Override
    public void renderShaderEffects(float tickDelta) {
        if (TwoDimensionalClient.plane != null) {
            PLANE_SHADERS.render(tickDelta);
        }
    }
}
