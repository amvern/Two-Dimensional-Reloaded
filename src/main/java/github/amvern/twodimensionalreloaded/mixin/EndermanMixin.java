package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EndermanMixin {

    @Inject(method = "isBeingStaredBy", at = @At("HEAD"), cancellable = true)
    private void overrideLookCheck(Player player, CallbackInfoReturnable<Boolean> cir) {

//        if (!LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM.test(player)) {
//            cir.setReturnValue(false);
//            return;
//        }
//
//        if (isCrosshairOnEndermanHead(player, (EnderMan)(Object)this)) {
//            cir.setReturnValue(true);
//        }
//        cir.setReturnValue(false);
    }

    @Unique
    private static boolean isCrosshairOnEndermanHead(Player player, EnderMan enderMan) {
        return false;
    }
}