package github.amvern.twodimensionalreloaded.mixin;

import com.mojang.authlib.GameProfile;
import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import github.amvern.twodimensionalreloaded.access.EntityPlaneGetterSetter;
import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements InteractionLayerGetterSetter {
    @Shadow @Final private MinecraftServer server;
    private LayerMode currentLayer = LayerMode.BASE;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void copyPlane(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        Plane plane = ((EntityPlaneGetterSetter) oldPlayer).twoDimensional$getPlane();
        ((EntityPlaneGetterSetter) this).twoDimensional$setPlane(plane);

        if (plane != null) {
            TwoDimensionalReloaded.setPlayerPlane(this.server, (ServerPlayer) (Player) this);
        }
    }

    @Override
    public void setInteractionLayer(LayerMode mode) {
        this.currentLayer = mode;
    }

    @Override
    public LayerMode getInteractionLayer() {
        return currentLayer;
    }

}
