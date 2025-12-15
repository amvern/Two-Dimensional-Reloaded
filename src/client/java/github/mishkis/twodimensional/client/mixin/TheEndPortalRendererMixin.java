package github.mishkis.twodimensional.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin culls the EndPortalBlocks at the plane
 */
@Mixin(TheEndPortalRenderer.class)
public class TheEndPortalRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cull(TheEndPortalBlockEntity entity, float partialTicks, PoseStack poseStack,
                      MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (Plane.shouldCull(entity.getBlockPos(), TwoDimensionalClient.plane)) {
            ci.cancel();
        }
    }
}


