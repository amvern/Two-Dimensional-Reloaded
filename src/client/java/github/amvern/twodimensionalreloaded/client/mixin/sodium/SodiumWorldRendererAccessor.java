package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Consumer;

@Mixin(SodiumWorldRenderer.class)
public interface SodiumWorldRendererAccessor {

    @Invoker("iterateVisibleBlockEntities")
    void callIterateVisibleBlockEntities(Consumer<BlockEntity> consumer);
}

