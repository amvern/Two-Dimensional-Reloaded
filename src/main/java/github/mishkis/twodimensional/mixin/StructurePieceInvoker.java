package github.mishkis.twodimensional.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Mixin to expose StructurePiece#setOrientation
 * */
@Mixin(StructurePiece.class)
public interface StructurePieceInvoker {
    @Invoker("setOrientation")
    void callSetOrientation(Direction direction);
}
