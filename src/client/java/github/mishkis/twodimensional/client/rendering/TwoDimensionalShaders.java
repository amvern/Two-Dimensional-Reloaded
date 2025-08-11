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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class TwoDimensionalShaders implements PostWorldRenderCallback, ShaderEffectRenderCallback {
    public static final Identifier PLANE_SHADERS_ID = new Identifier(TwoDimensional.MOD_ID, "shaders/post/plane_shaders.json");
    public static final TwoDimensionalShaders INSTANCE = new TwoDimensionalShaders();

    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    private final Matrix4f projectionMatrix = new Matrix4f();

    final ManagedShaderEffect PLANE_SHADERS = ShaderEffectManager.getInstance().manage(PLANE_SHADERS_ID, shader -> {
        shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)minecraftClient.getFramebuffer()).getStillDepthMap());
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
        uniformInverseTransformMatrix.set(GlMatrices.getInverseTransformMatrix(projectionMatrix));

        uniformCameraPos.set(camera.getPos().toVector3f());

        Plane plane = TwoDimensionalClient.plane;
        if (plane != null) {
            uniformPlaneOffset.set(plane.getOffset().toVector3f());
            uniformPlaneSlope.set((float) plane.getSlope());
            uniformPlaneNormal.set(plane.getNormal().toVector3f());
        }

        float[] fogColor = RenderSystem.getShaderFogColor();
        uniformSkyColor.set(fogColor[0], fogColor[1], fogColor[2]);

        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        lightLevel = lightLevel.lerp(new Vector2f(world.getLightLevel(LightType.BLOCK, player.getBlockPos()), world.getLightLevel(LightType.SKY, player.getBlockPos())), 0.2f);
        uniformLightLevel.set(lightLevel);

        uniformPlayerPos.set(player.getPos().toVector3f());

        PLANE_SHADERS.render(tickDelta);
    }

    @Override
    public void renderShaderEffects(float tickDelta) {
        PLANE_SHADERS.render(tickDelta);
    }
}
