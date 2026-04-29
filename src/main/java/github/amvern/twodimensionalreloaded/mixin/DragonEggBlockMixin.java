package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.level.block.DragonEggBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(DragonEggBlock.class)
public class DragonEggBlockMixin {

    @ModifyArgs(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"))
    private void clampTeleportZ(Args args) {
        args.set(2, 0);
    }
}