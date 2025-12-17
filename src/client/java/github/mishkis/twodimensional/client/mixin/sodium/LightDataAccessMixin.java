package github.mishkis.twodimensional.client.mixin.sodium;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/***
 * Mixin to effectively omit culled blocks from lighting calculations
 */
@Mixin(LightDataAccess.class)
public abstract class LightDataAccessMixin {

    @Shadow protected BlockAndTintGetter level;
    @Shadow private BlockPos.MutableBlockPos pos;

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true)
    private void plane$computeAsTransparentAir(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        Plane plane = TwoDimensionalClient.plane;
        if (!plane.shouldCull(new BlockPos(x, y, z), plane)) {
            return;
        }

        BlockPos p = this.pos.set(x, y, z);

        int light = LevelRenderer.getLightColor(this.level, this.level.getBlockState(p), p);
        int bl = LightTexture.block(light);
        int sl = LightTexture.sky(light);

        int packedLightData  =
                LightDataAccess.packFC(false) |
                LightDataAccess.packFO(false) |
                LightDataAccess.packOP(false) |
                LightDataAccess.packEM(false) |
                LightDataAccess.packAO(1.0f) |
                LightDataAccess.packLU(0) |
                LightDataAccess.packSL(sl) |
                LightDataAccess.packBL(bl);

        cir.setReturnValue(packedLightData);
    }
}



