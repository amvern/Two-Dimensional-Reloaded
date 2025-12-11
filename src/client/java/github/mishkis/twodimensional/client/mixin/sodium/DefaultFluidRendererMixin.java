package github.mishkis.twodimensional.client.mixin.sodium;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultFluidRenderer.class)
public class DefaultFluidRendererMixin {

    /**
     * Cancel rendering entirely if the block is culled by our plane
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cullFluids(
            LevelSlice level,
            BlockState blockState,
            FluidState fluidState,
            BlockPos blockPos,
            BlockPos offset,
            TranslucentGeometryCollector collector,
            ChunkModelBuilder meshBuilder,
            Material material,
            ColorProvider<FluidState> colorProvider,
            TextureAtlasSprite[] sprites,
            CallbackInfo ci
    ) {
        if (Plane.shouldCull(blockPos, TwoDimensionalClient.plane)) {
            ci.cancel();
        }
    }

    /**
     * Force rendering of fluid sides that would normally have neighboring block
     */
    @Inject(method = "isSideExposed", at = @At("HEAD"), cancellable = true)
    private void enableCulledFluidSide(
            BlockAndTintGetter world,
            int x, int y, int z,
            Direction dir,
            float height,
            CallbackInfoReturnable<Boolean> cir
    ) {
        BlockPos pos = new BlockPos(x, y, z).relative(dir);
        if (Plane.shouldCull(pos, TwoDimensionalClient.plane)) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Treat faces adjacent to culled blocks as not occluded
     */
    @Inject(method = "isFullBlockFluidOccluded", at = @At("HEAD"), cancellable = true)
    private void enableCulledSides(
            BlockAndTintGetter world,
            BlockPos pos,
            Direction dir,
            BlockState blockState,
            FluidState fluid,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (Plane.shouldCull(pos.relative(dir), TwoDimensionalClient.plane)) {
            cir.setReturnValue(false);
        }
    }
}
