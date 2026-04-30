package github.amvern.twodimensionalreloaded.mixin.EntityAiGoals;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultRandomPos.class)
public class DefaultRandomPosMixin {

    @ModifyReturnValue(
        method = {
            "getPos",
            "getPosTowards",
            "getPosAway"
        },
        at = @At("RETURN")
    )
    private static Vec3 clampRandomPosZ(Vec3 original, PathfinderMob mob) {
        if (original == null) return null;

        return new Vec3(original.x, original.y, mob.getZ());
    }
}