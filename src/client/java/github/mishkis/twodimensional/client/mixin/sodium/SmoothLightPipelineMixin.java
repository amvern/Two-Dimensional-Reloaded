package github.mishkis.twodimensional.client.mixin.sodium;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.utils.Plane;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmoothLightPipeline.class)
public abstract class SmoothLightPipelineMixin {

    /**
     * Check if block is on plane and needs to be lit
     * */
    @Inject(method = "calculate", at = @At("HEAD"), cancellable = true)
    private void fullbrightPlane(ModelQuadView quad, BlockPos pos, QuadLightData out, Direction cullFace,
                                 Direction lightFace, boolean shade, boolean enhanced, CallbackInfo ci) {

        Plane plane = TwoDimensionalClient.plane;
        //boolean isPlaneBlock = plane != null && Math.abs(pos.getZ() - plane.getZ()) <= 0.5;
        boolean isPlaneBlock = plane != null && pos.getZ() == 0;

        if (isPlaneBlock) {
            propagatePlaneLighting(pos, out);
            ci.cancel();
        }
    }

    /**
     * Set lighting for blocks on our plane, try to set light as if the culled blocks were air
     * */
    private void propagatePlaneLighting(BlockPos pos, QuadLightData out) {
        int maxSky = 0;
        int maxBlock = 0;
        float ao = 1f;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos neighbor = pos.offset(dx, dy, dz);
                    int sky = Minecraft.getInstance().level.getBrightness(LightLayer.SKY, neighbor);
                    int block = Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, neighbor);

                    maxSky = Math.max(maxSky, sky);
                    maxBlock = Math.max(maxBlock, block);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            out.br[i] = ao;
            out.lm[i] = ((maxSky & 0xF) << 20) | ((maxBlock & 0xF) << 4);
        }
    }
}
