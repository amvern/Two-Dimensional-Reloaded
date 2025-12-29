package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.utils.Plane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderPlacementOutline(
            PoseStack poseStack, VertexConsumer vertexConsumer,
            double cameraX, double cameraY, double cameraZ,
            BlockOutlineRenderState blockOutlineRenderState,
            int color, float alpha,
            CallbackInfo ci
    ) {
        BlockPos targetPos = blockOutlineRenderState.pos();
        if (Plane.shouldCull(targetPos, TwoDimensionalReloadedClient.plane) || targetPos.getZ() > 1) {
            ci.cancel();
            return;
        }

        if(TwoDimensionalReloadedClient.CONFIG.renderBlockPlacementGuide) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof BlockItem)) return;

            BlockHitResult hitResult = (BlockHitResult) player.pick(5.0D, 0.0F, false);
            if (hitResult == null) return;

            BlockItem blockItem = (BlockItem) stack.getItem();
            Block block = blockItem.getBlock();

            Level level = player.level();


            BlockPlaceContext context = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack, hitResult);
            BlockState stateToPlace = block.getStateForPlacement(context);
            if (stateToPlace == null) stateToPlace = block.defaultBlockState();

            BlockPos clickedPos = hitResult.getBlockPos();
            BlockState clickedState = level.getBlockState(clickedPos);

            BlockPos placePos = clickedState.canBeReplaced(context)
                    ? clickedPos
                    : clickedPos.relative(hitResult.getDirection());

            VoxelShape shape = stateToPlace.getShape(level, placePos);
            BlockState worldState = level.getBlockState(placePos);
            boolean replaceable = worldState.canBeReplaced(new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack, hitResult));
            boolean canSurvive = stateToPlace.canSurvive(level, placePos);
            AABB blockBox = stateToPlace.getShape(level, placePos).bounds();
            boolean collidesWithEntity = !level.getEntities(null, blockBox.move(placePos.getX(), placePos.getY(), placePos.getZ())).isEmpty();

            boolean placeable = replaceable && canSurvive && !collidesWithEntity;

            int outlineColor = placeable ? TwoDimensionalReloadedClient.CONFIG.placeableOutlineColor : TwoDimensionalReloadedClient.CONFIG.nonPlaceableOutlineColor;

            ShapeRenderer.renderShape(
                    poseStack,
                    vertexConsumer,
                    shape,
                    placePos.getX() - cameraX,
                    placePos.getY() - cameraY,
                    placePos.getZ() - cameraZ,
                    outlineColor,
                    alpha
            );
        }

       // ci.cancel();
    }

}
