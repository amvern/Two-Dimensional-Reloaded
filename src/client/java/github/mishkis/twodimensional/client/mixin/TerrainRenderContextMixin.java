package github.mishkis.twodimensional.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Injecting into fabric is kinda junk
@Mixin(TerrainRenderContext.class)
public class TerrainRenderContextMixin {
    // culls blocks like tall grass which get through the initial cull phase in BlockMixin
    @Inject(method = "tessellateBlock", at = @At("HEAD"), cancellable = true)
    public void cullBlocks(BlockState blockState, BlockPos blockPos, BakedModel model, PoseStack matrixStack, CallbackInfo ci) {
        if (Plane.shouldCull(blockPos, TwoDimensionalClient.plane)) {
            ci.cancel();
        }
    }
}
