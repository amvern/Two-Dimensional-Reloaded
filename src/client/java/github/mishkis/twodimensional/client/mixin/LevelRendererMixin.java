package github.mishkis.twodimensional.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @ModifyExpressionValue(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$CompiledSection;getRenderableBlockEntities()Ljava/util/List;")
    )
    private List<BlockEntity> cullBlockEntities(List<BlockEntity> original) {
        Plane plane = TwoDimensionalClient.plane;
        if (plane != null) {
            original = original.stream().filter(blockEntity ->
                            !Plane.shouldCull(blockEntity.getBlockPos(), plane))
                    .toList();
        }

        return original;
    }

    @Inject(method = "renderHitOutline", at = @At(value = "HEAD"), cancellable = true)
    private void disableCulledBlockOutline(PoseStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (Plane.shouldCull(pos, TwoDimensionalClient.plane)) {
            ci.cancel();
        }
    }

//    @ModifyVariable(method = "collectRenderableChunks", at = @At("HEAD"), argsOnly = true)
//    private boolean disableChunkCulling(boolean value) {
//        return value && TwoDimensionalClient.plane == null;
//    }
}
