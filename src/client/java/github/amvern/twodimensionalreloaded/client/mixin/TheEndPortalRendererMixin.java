package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin culls the EndPortalBlocks at the plane
 */
@Mixin(TheEndPortalRenderer.class)
public abstract class TheEndPortalRendererMixin<T extends TheEndPortalBlockEntity> {

    @Shadow abstract protected float getOffsetUp();
    @Shadow abstract protected float getOffsetDown();
    @Shadow abstract void renderFace(T theEndPortalBlockEntity, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cull(TheEndPortalBlockEntity entity, float partialTicks, PoseStack poseStack,
                      MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (Plane.shouldCull(entity.getBlockPos(), TwoDimensionalReloadedClient.plane)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCube", at = @At("HEAD"), cancellable = true)
    private void renderSmallerCube(T theEndPortalBlockEntity, Matrix4f matrix4f, VertexConsumer vertexConsumer, CallbackInfo ci) {
        float f = this.getOffsetDown();
        float g = this.getOffsetUp();
        this.renderFace(theEndPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        this.renderFace(theEndPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, g, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        this.renderFace(theEndPortalBlockEntity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderFace(theEndPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderFace(theEndPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderFace(theEndPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, g, g, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);

        ci.cancel();
    }
}


