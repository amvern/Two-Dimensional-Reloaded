package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import github.amvern.twodimensionalreloaded.utils.Plane;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: unfuck lighting

@Mixin(LightDataAccess.class)
public abstract class LightDataAccessMixin {
    @Shadow protected BlockAndTintGetter level;
    @Shadow @Final private BlockPos.MutableBlockPos pos;

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true)
    private void plane$computeAsTransparentAir(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        if (!Plane.shouldCull(new BlockPos(x, y, z))) {
            return;
        }

        BlockPos p = this.pos.set(x, y, z);

        int light = LevelRenderer.getLightCoords(LevelRenderer.BrightnessGetter.DEFAULT, this.level, this.level.getBlockState(p), p);
        int bl = LightCoordsUtil.block(light);
        int sl = LightCoordsUtil.sky(light);

        BlockState state = this.level.getBlockState(p);
        boolean fc = state.isCollisionShapeFullBlock(level, p);
        boolean fo = state.isSolidRender();
        boolean op = state.isViewBlocking(level, p) && state.getLightDampening() != 0;
        float ao = state.getShadeBrightness(level, p);


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