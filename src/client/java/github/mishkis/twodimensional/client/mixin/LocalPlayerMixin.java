package github.mishkis.twodimensional.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.mishkis.twodimensional.client.TwoDimensionalClient;
import github.mishkis.twodimensional.client.access.MouseNormalizedGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Entity {
    @Shadow public Input input;

    @Shadow @Final protected Minecraft minecraft;

    public LocalPlayerMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "hasEnoughImpulseToStartSprinting", at = @At("RETURN"))
    private boolean countSidewaysMovementOnPlane(boolean original) {
        if (TwoDimensionalClient.plane != null) {
            return original || this.input.leftImpulse * Mth.sign(((MouseNormalizedGetter) minecraft.mouseHandler).twoDimensional$getNormalizedX()) >= 0.8;
        }

        return original;
    }
}
