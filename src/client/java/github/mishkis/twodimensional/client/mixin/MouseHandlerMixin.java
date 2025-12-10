package github.mishkis.twodimensional.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.platform.InputConstants;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.client.access.MouseNormalizedGetter;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin implements MouseNormalizedGetter {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private double xpos;
    @Shadow private double ypos;
    @Unique
    private Double twoDimensional$normalizedX = 0d;
    @Unique
    private Double twoDimensional$normalizedY = 0d;

    @Override
    public double twoDimensional$getNormalizedX() {
        return Objects.requireNonNullElse(twoDimensional$normalizedX, 0d);
    }

    @Override
    public double twoDimensional$getNormalizedY() {
        return Objects.requireNonNullElse(twoDimensional$normalizedY, 0d);
    }

    @Inject(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;sensitivity()Lnet/minecraft/client/OptionInstance;"))
    public void updateNormalizedPos(CallbackInfo ci) {
        double width = this.minecraft.getWindow().getScreenWidth() / 2f;
        double height = this.minecraft.getWindow().getScreenHeight() / 2f;

        twoDimensional$normalizedX = (width - this.xpos) / width;
        twoDimensional$normalizedY = (height - this.ypos) / height;

        if (twoDimensional$normalizedX.isInfinite() || twoDimensional$normalizedX.isNaN()) {
            twoDimensional$normalizedX = 0d;
        }

        if (twoDimensional$normalizedY.isInfinite() || twoDimensional$normalizedY.isNaN()) {
            twoDimensional$normalizedY = 0d;
        }
    }

    @WrapWithCondition(method = "grabMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(JIDD)V"))
    public boolean grabMouse(long handler, int inputModeValue, double x, double y) {
        if (TwoDimensionalClient.plane != null) {
            InputConstants.grabOrReleaseMouse(handler, GLFW.GLFW_CURSOR_HIDDEN, x, y);
            return false;
        }

        return true;
    }
}
