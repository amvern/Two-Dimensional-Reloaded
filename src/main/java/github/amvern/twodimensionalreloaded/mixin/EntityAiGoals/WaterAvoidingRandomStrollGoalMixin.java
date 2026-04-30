package github.amvern.twodimensionalreloaded.mixin.EntityAiGoals;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaterAvoidingRandomStrollGoal.class)
public class WaterAvoidingRandomStrollGoalMixin {

    @ModifyReturnValue(method = "getPosition", at = @At("RETURN"))
    private Vec3 restrictStrollZMovement(Vec3 original) {
        if (original == null) return null;

        return new Vec3(original.x, original.y, Plane.getZ());
    }
}