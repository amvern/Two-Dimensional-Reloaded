package github.mishkis.twodimensional.client.mixin;


import com.mojang.blaze3d.vertex.VertexConsumer;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
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
        if (Plane.shouldCull(pos, TwoDimensionalClient.plane)) {
            ci.cancel();
        }
    }

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void enableCulledFluidSide(BlockAndTintGetter world, BlockPos pos, FluidState fluidState, BlockState blockState, Direction direction, FluidState neighborFluidState, CallbackInfoReturnable<Boolean> cir) {
        if (Plane.shouldCull(pos.relative(direction), TwoDimensionalClient.plane)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isFaceOccludedByState(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/Direction;FLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private static void enableCulledSides(BlockGetter world, Direction direction, float height, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (Plane.shouldCull(pos.relative(direction), TwoDimensionalClient.plane)) {
            cir.setReturnValue(false);
        }
    }
}
