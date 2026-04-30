package github.amvern.twodimensionalreloaded.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    //Todo: implement gui or other method of setting block rotation, enable keybind
//    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
//    private BlockState updateGetPlacementState(BlockState original) {
//        if (original == null) return null;
//
//        Direction forcedDir = Direction.NORTH;
//
//        if (original.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
//            return original.setValue(BlockStateProperties.HORIZONTAL_FACING, forcedDir);
//        }
//
//        if (original.hasProperty(BlockStateProperties.FACING)) {
//            return original.setValue(BlockStateProperties.FACING, forcedDir);
//        }
//
//        return original;
//    }
}