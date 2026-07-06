package github.amvern.twodimensionalreloaded.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TeleportRandomlyConsumeEffect.class)
public class TeleportRandomlyConsumeEffectMixin {

    @ModifyArg(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;randomTeleport(DDDZ)Z"), index = 2)
    private double applyRetainZ(double zz, @Local(argsOnly = true) LivingEntity user) {
        return user.getZ();
    }
}