package github.amvern.twodimensionalreloaded.mixin;

import com.mojang.authlib.GameProfile;
import github.amvern.twodimensionalreloaded.access.InteractionLayerGetterSetter;
import github.amvern.twodimensionalreloaded.utils.LayerMode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements InteractionLayerGetterSetter {
    private LayerMode currentLayer = LayerMode.BASE;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
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