package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import com.mojang.blaze3d.vertex.PoseStack;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedSet;
import java.util.function.Consumer;

@Mixin(SodiumWorldRenderer.class)
public class SodiumWorldRendererMixin {

    @Inject(
            method = "extractBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;FLit/unimi/dsi/fastutil/longs/Long2ObjectMap;Lnet/minecraft/client/renderer/state/LevelRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cullExtractBlockEntity(BlockEntity blockEntity, PoseStack poseStack, Camera camera, float tickDelta, Long2ObjectMap<SortedSet<BlockDestructionProgress>> progression, LevelRenderState levelRenderState, CallbackInfo ci) {
        Plane plane = TwoDimensionalReloadedClient.plane;

        if(Plane.shouldCull(blockEntity.getBlockPos(), plane)) {
            ci.cancel();
        }
    }

    @Inject(method = "iterateVisibleBlockEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private void cullIterateVisibleBlockEntities(Consumer<BlockEntity> consumer, CallbackInfo ci) {
        Plane plane = TwoDimensionalReloadedClient.plane;

        if (plane == null) return;

        Consumer<BlockEntity> filtered = blockEntity -> {
            if (!Plane.shouldCull(blockEntity.getBlockPos(), plane)) {
                consumer.accept(blockEntity);
            }
        };

        ci.cancel();

        ((SodiumWorldRendererAccessor) this).callIterateVisibleBlockEntities(filtered);
    }
}