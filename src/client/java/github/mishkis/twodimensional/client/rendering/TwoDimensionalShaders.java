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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class TwoDimensionalShaders implements PostWorldRenderCallback, ShaderEffectRenderCallback {
    public static final Identifier FOG_SHADER_ID = new Identifier(TwoDimensional.MOD_ID, "shaders/post/fog.json");
    public static final TwoDimensionalShaders INSTANCE = new TwoDimensionalShaders();

    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    private final Matrix4f projectionMatrix = new Matrix4f();

    final ManagedShaderEffect FOG_SHADER = ShaderEffectManager.getInstance().manage(FOG_SHADER_ID, shader -> {
        shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)minecraftClient.getFramebuffer()).getStillDepthMap());
    });
    private final UniformMat4 uniformInverseTransformMatrix = FOG_SHADER.findUniformMat4("InverseTransformMatrix");
    private final Uniform3f uniformCameraPos = FOG_SHADER.findUniform3f("CameraPos");
    private final Uniform3f uniformPlaneOffset = FOG_SHADER.findUniform3f("PlaneOffset");
    private final Uniform1f uniformPlaneSlope = FOG_SHADER.findUniform1f("PlaneSlope");
    private final Uniform3f uniformSkyColor = FOG_SHADER.findUniform3f("SkyColor");
    private final Uniform2f uniformLightLevel = FOG_SHADER.findUniform2f("LightLevel");

    private Vector2f lightLevel = new Vector2f(15f);

    @Override
    public void onWorldRendered(Camera camera, float tickDelta, long nanoTime) {
        uniformInverseTransformMatrix.set(GlMatrices.getInverseTransformMatrix(projectionMatrix));

        uniformCameraPos.set(camera.getPos().toVector3f());

        Plane plane = TwoDimensionalClient.plane;
        if (plane != null) {
            uniformPlaneOffset.set(TwoDimensionalClient.plane.getOffset().toVector3f());
            uniformPlaneSlope.set((float) (TwoDimensionalClient.plane.getSlope()));
        }

        float[] fogColor = RenderSystem.getShaderFogColor();
        uniformSkyColor.set(fogColor[0], fogColor[1], fogColor[2]);

        World world = MinecraftClient.getInstance().world;
        BlockPos pos = MinecraftClient.getInstance().player.getBlockPos();
        lightLevel = lightLevel.lerp(new Vector2f(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos)), 0.2f);
        uniformLightLevel.set(lightLevel);

        FOG_SHADER.render(tickDelta);
    }

    @Override
    public void renderShaderEffects(float tickDelta) {
        FOG_SHADER.render(tickDelta);
    }
}
