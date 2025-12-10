package github.mishkis.twodimensional.client.mixin;

import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.client.access.MouseNormalizedGetter;
import github.mishkis.twodimensional.utils.Plane;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    double twoDimensional$xMouseOffset = 0;

    @Unique
    double twoDimensional$yMouseOffset = 0;

    @Shadow private boolean detached;

    @Shadow protected abstract void setPosition(double x, double y, double z);

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow protected abstract void move(double x, double y, double z);

    @Shadow private float eyeHeight;

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"), cancellable = true)
    public void setup(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        Plane plane = TwoDimensionalClient.plane;
        if (plane != null) {
            this.detached = true;

            this.setRotation((float) (plane.getYaw() * Mth.RAD_TO_DEG), 0);

            Vec3 pos = new Vec3(Mth.lerp(tickDelta, focusedEntity.xo, focusedEntity.getX()), Mth.lerp(tickDelta, focusedEntity.yo, focusedEntity.getY()) + focusedEntity.getEyeHeight(), Mth.lerp(tickDelta, focusedEntity.zo, focusedEntity.getZ()));
            this.setPosition(pos.x, pos.y, pos.z);

            MouseNormalizedGetter mouse = (MouseNormalizedGetter) Minecraft.getInstance().mouseHandler;

            float mouseOffsetScale = twoDimensional$getMouseOffsetScale(Minecraft.getInstance().player);
            double delta = 0.2 - (0.15 * mouseOffsetScale/40);

            twoDimensional$xMouseOffset = Mth.lerp(delta, twoDimensional$xMouseOffset, mouse.twoDimensional$getNormalizedX() * mouseOffsetScale);
            twoDimensional$yMouseOffset = Mth.lerp(delta, twoDimensional$yMouseOffset, mouse.twoDimensional$getNormalizedY() * mouseOffsetScale);

            this.move(-8, twoDimensional$yMouseOffset, twoDimensional$xMouseOffset);

            ci.cancel();
        }
    }

    @Unique
    private float twoDimensional$getMouseOffsetScale(Player player) {
        if (player == null || !player.isUsingItem()) {
            return 1;
        }

        return switch (player.getUseItem().getItem().getDescriptionId()) {
            case "item.minecraft.bow" -> 10;
            case "item.minecraft.spyglass" -> 40;
            default -> 1;
        };
    }
}
