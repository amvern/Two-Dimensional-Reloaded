package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void changeBoatDimensions(CallbackInfoReturnable<EntityDimensions> cir) {
        EntityType<?> type = (EntityType<?>)(Object)this;

        //Am I aware of EntityTypeTags.BOAT? Yep.
        if (type == EntityType.OAK_BOAT
            || type == EntityType.SPRUCE_BOAT
            || type == EntityType.BIRCH_BOAT
            || type == EntityType.JUNGLE_BOAT
            || type == EntityType.ACACIA_BOAT
            || type == EntityType.DARK_OAK_BOAT
            || type == EntityType.MANGROVE_BOAT
            || type == EntityType.CHERRY_BOAT
            || type == EntityType.PALE_OAK_BOAT
            || type == EntityType.BAMBOO_RAFT

            || type == EntityType.OAK_CHEST_BOAT
            || type == EntityType.SPRUCE_CHEST_BOAT
            || type == EntityType.BIRCH_CHEST_BOAT
            || type == EntityType.JUNGLE_CHEST_BOAT
            || type == EntityType.ACACIA_CHEST_BOAT
            || type == EntityType.DARK_OAK_CHEST_BOAT
            || type == EntityType.MANGROVE_CHEST_BOAT
            || type == EntityType.CHERRY_CHEST_BOAT
            || type == EntityType.PALE_OAK_CHEST_BOAT
            || type == EntityType.BAMBOO_CHEST_RAFT

        ){
            cir.setReturnValue(EntityDimensions.fixed(1.0F, 0.5625F));
        }
    }
}