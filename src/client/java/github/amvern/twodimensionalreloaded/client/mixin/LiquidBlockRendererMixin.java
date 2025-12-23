package github.amvern.twodimensionalreloaded.client.mixin;


import com.mojang.blaze3d.vertex.VertexConsumer;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {
    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void cullFluids(BlockAndTintGetter world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        if (Plane.shouldCull(pos, TwoDimensionalReloadedClient.plane)) {
            ci.cancel();
        }
    }

//    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
//    private static void enableCulledFluidSide(FluidState fluidState, BlockState blockState, Direction direction, FluidState fluidState2, CallbackInfoReturnable<Boolean> cir) {
//        if (Plane.shouldCull(pos.relative(direction), TwoDimensionalReloadedClient.plane)) {
//            cir.setReturnValue(true);
//        }
//    }
//
//    @Inject(method = "isFaceOccludedByState", at = @At("HEAD"), cancellable = true)
//    private static void enableCulledSides(Direction direction, float f, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
//        if (Plane.shouldCull(pos.relative(direction), TwoDimensionalReloadedClient.plane)) {
//            cir.setReturnValue(false);
//        }
//    }
}