package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

/**
 * Mixin to allow right-clicking NetherPortalBlock to teleport
 * */
@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin extends Block {

    @Shadow protected abstract void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean bl);

    public NetherPortalBlockMixin(Properties properties) {
        super(properties);
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && player.getPortalCooldown() == 0) {
            this.entityInside(state, level, pos, player, new InsideBlockEffectApplier() {
                @Override
                public void apply(InsideBlockEffectType insideBlockEffectType) {

                }

                @Override
                public void runBefore(InsideBlockEffectType insideBlockEffectType, Consumer<Entity> consumer) {

                }

                @Override
                public void runAfter(InsideBlockEffectType insideBlockEffectType, Consumer<Entity> consumer) {

                }
            },true);
        }

        //TODO: set this every tick to allow overlay to render
        player.setAsInsidePortal((Portal) this, pos);
        return InteractionResult.SUCCESS;
    }
}





