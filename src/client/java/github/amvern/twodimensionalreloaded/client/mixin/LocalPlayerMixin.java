package github.amvern.twodimensionalreloaded.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.client.access.MouseNormalizedGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Entity {
    @Shadow public ClientInput input;

    @Shadow @Final protected Minecraft minecraft;

    public LocalPlayerMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "canStartSprinting", at = @At("RETURN"))
    private boolean countSidewaysMovementOnPlane(boolean original) {
        if (TwoDimensionalReloadedClient.plane != null) {
            double moveX = this.input.getMoveVector().x;
            double mouseX = ((MouseNormalizedGetter) minecraft.mouseHandler).twoDimensional$getNormalizedX();
            return original || moveX * Math.signum(mouseX) >= 0.8;
        }

        return original;
    }
}
