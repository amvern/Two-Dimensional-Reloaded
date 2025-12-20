package github.amvern.twodimensionalreloaded.client.mixin;

import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.client.access.MouseNormalizedGetter;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static github.amvern.twodimensionalreloaded.utils.PlaneAttachment.PLAYER_PLANE;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    double twoDimensional$xMouseOffset = 0;

    @Unique
    double twoDimensional$yMouseOffset = 0;

    @Shadow private boolean detached;

    @Shadow protected abstract void setPosition(double x, double y, double z);

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow protected abstract void move(float x, float y, float z);

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"), cancellable = true)
    public void setup(Level level, Entity entity, boolean bl, boolean bl2, float tickDelta, CallbackInfo ci) {
        Plane plane = TwoDimensionalReloadedClient.plane;
        if (entity.hasAttached(PLAYER_PLANE)) {
            this.detached = true;

            this.setRotation(0f, 0f);

            Vec3 pos = new Vec3(Mth.lerp(tickDelta, entity.xo, entity.getX()), Mth.lerp(tickDelta, entity.yo, entity.getY()) + entity.getEyeHeight(), Mth.lerp(tickDelta, entity.zo, entity.getZ()));
            this.setPosition(pos.x, pos.y, pos.z);

            MouseNormalizedGetter mouse = (MouseNormalizedGetter) Minecraft.getInstance().mouseHandler;

            float mouseOffsetScale = twoDimensional$getMouseOffsetScale(Minecraft.getInstance().player);
            double delta = 0.2 - (0.15 * mouseOffsetScale/40);

            twoDimensional$xMouseOffset = Mth.lerp(delta, twoDimensional$xMouseOffset, mouse.twoDimensional$getNormalizedX() * mouseOffsetScale);
            twoDimensional$yMouseOffset = Mth.lerp(delta, twoDimensional$yMouseOffset, mouse.twoDimensional$getNormalizedY() * mouseOffsetScale);

            this.move(-8, (float) twoDimensional$yMouseOffset, (float) -twoDimensional$xMouseOffset);

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
