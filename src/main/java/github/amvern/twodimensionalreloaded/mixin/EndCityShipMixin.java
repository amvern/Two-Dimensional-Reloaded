package github.amvern.twodimensionalreloaded.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EndCityPieces.class)
public class EndCityShipMixin {

//    @ModifyArg(
//        method = "addPiece",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/world/level/levelgen/structure/structures/EndCityPieces$EndCityPiece;<init>(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Ljava/lang/String;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Z)V"
//        ),
//        index = 2
//    )
//    private static BlockPos shiftEndCityShip(BlockPos position, @Local(argsOnly = true) final String templateName) {
//        if ("ship".equals(templateName)) {
//            return new BlockPos(position.getX(), position.getY(), 0);
//        }
//        return position;
//    }

}