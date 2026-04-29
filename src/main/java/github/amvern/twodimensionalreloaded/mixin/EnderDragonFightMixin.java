package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EnderDragonFight.class)
public abstract class EnderDragonFightMixin {
    @Shadow private ServerLevel level;

    @ModifyArgs(method = "spawnNewGateway()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;<init>(III)V"))
    private void setIslandGatewayPos(Args args) {
        args.set(0, 100);

        int baseY = ServerLevel.END_SPAWN_POINT.getY();
        args.set(1, baseY + 6);

        args.set(2, 0);
    }

    @Inject(method = "spawnNewGateway(Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"), cancellable = true)
    private void blockGatewayPortalEntrance(BlockPos pos, CallbackInfo ci) {
       buildGatewayWarning(pos);
    }

    @Unique
    private void buildGatewayWarning(BlockPos gatewayPos) {
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(gatewayPos.mutable().move(1, 0, 0), Blocks.REDSTONE_BLOCK.defaultBlockState());
            level.setBlockAndUpdate(gatewayPos.mutable().move(-1, 0, 0), Blocks.REDSTONE_BLOCK.defaultBlockState());

            BlockPos signPos = gatewayPos.mutable().move(0, 3, 0);
            level.setBlockAndUpdate(signPos, Blocks.OAK_SIGN.defaultBlockState());
            BlockEntity be = level.getBlockEntity(signPos);

            if (be instanceof SignBlockEntity sign) {
                SignText text = sign.getFrontText();
                text = text.setMessage(0, Component.literal("=== WARNING ==="));
                text = text.setMessage(1, Component.literal("entering gateway"));
                text = text.setMessage(2, Component.literal("will likely KILL you"));
                text = text.setMessage(3, Component.literal("=== WARNING ==="));
                sign.setText(text, true);

                level.sendBlockUpdated(signPos, level.getBlockState(signPos), level.getBlockState(signPos), 3);
            }
        }
    }
}