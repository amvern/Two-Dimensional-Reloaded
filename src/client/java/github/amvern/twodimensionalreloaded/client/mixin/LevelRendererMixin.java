package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderPlacementOutline(
        PoseStack poseStack, VertexConsumer vertexConsumer,
        double cameraX, double cameraY, double cameraZ,
        BlockOutlineRenderState blockOutlineRenderState,
        int color, float alpha,
        CallbackInfo ci
    ) {
        BlockPos targetPos = blockOutlineRenderState.pos();
        Player player = minecraft.player;
        if (player == null) return;

        if (Plane.shouldCull(targetPos) || targetPos.getZ() > 1 || !player.isWithinBlockInteractionRange(targetPos, 1)) {
            ci.cancel();
        }

       // ci.cancel();
    }
}