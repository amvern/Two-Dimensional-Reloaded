package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderHitOutline", at = @At(value = "HEAD"), cancellable = true)
    private void disableCulledBlockOutline(PoseStack poseStack, VertexConsumer vertexConsumer, double d, double e, double f, BlockOutlineRenderState blockOutlineRenderState, int i, float g, CallbackInfo ci) {
        BlockPos blockPos = blockOutlineRenderState.pos();

        if (Plane.shouldCull(blockPos, TwoDimensionalReloadedClient.plane)) {
            ci.cancel();
        }
    }

}
