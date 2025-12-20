package github.amvern.twodimensionalreloaded.client.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlockRenderContext.class)
public interface AbstractBlockRenderContextAccessor {
    @Accessor("pos")
    BlockPos getPos();
}