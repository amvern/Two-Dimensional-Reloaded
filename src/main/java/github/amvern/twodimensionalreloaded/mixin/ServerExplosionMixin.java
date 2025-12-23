package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerExplosion.class)
public class ServerExplosionMixin {

    @Inject(method = "calculateExplodedPositions", at = @At("RETURN"), cancellable = true)
    private void clampToZ0(CallbackInfoReturnable<List<BlockPos>> cir) {
        List<BlockPos> original = cir.getReturnValue();
        List<BlockPos> filtered = new ArrayList<>(original.stream()
                .filter(pos -> pos.getZ() == 0)
                .toList());
        cir.setReturnValue(filtered);
    }
}

