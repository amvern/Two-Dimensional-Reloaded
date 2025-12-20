package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

/**
 * This mixin culls the EndPortalBlocks at the plane
 */
@Mixin(AbstractEndPortalRenderer.class)
public abstract class AbstractEndPortalRendererMixin<T extends TheEndPortalBlockEntity, S extends EndPortalRenderState> implements BlockEntityRenderer<T, S> {

    @Shadow abstract protected float getOffsetUp();
    @Shadow abstract protected float getOffsetDown();
    @Shadow abstract void renderFace(EnumSet<Direction> enumSet, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction);

    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/TheEndPortalBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/EndPortalRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At("HEAD"), cancellable = true)
    public void extractRenderState(T theEndPortalBlockEntity, S endPortalRenderState, float f, Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, CallbackInfo ci) {
        if (Plane.shouldCull(theEndPortalBlockEntity.getBlockPos(), TwoDimensionalReloadedClient.plane)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCube", at = @At("HEAD"), cancellable = true)
    private void renderSmallerCube(EnumSet<Direction> enumSet, Matrix4f matrix4f, VertexConsumer vertexConsumer, CallbackInfo ci) {
        float f = this.getOffsetDown();
        float g = this.getOffsetUp();
        this.renderFace(enumSet, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        this.renderFace(enumSet, matrix4f, vertexConsumer, 0.0F, 1.0F, g, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        this.renderFace(enumSet, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderFace(enumSet, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderFace(enumSet, matrix4f, vertexConsumer, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderFace(enumSet, matrix4f, vertexConsumer, 0.0F, 1.0F, g, g, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);

        ci.cancel();
    }
}


