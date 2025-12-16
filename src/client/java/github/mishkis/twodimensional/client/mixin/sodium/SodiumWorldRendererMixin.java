package github.mishkis.twodimensional.client.mixin.sodium;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SodiumWorldRenderer.class)
public class SodiumWorldRendererMixin {

    @Inject(method = "iterateVisibleBlockEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private void wrapBlockEntityConsumer(Consumer<BlockEntity> consumer, CallbackInfo ci) {
        Plane plane = TwoDimensionalClient.plane;

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


