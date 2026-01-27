package github.amvern.twodimensionalreloaded.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import static github.amvern.twodimensionalreloaded.util.ModelBlockAlphaRenderer.renderModelWithAlpha;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.client.config.ClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockPlacementGuide {
    public static void renderPlacementGuide(PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if(level == null) return;

        LocalPlayer player = minecraft.player;
        if (player == null) return;

        HitResult hitResult = player.raycastHitResult(0.0f, player);
        if (hitResult.getType().equals(HitResult.Type.ENTITY)) return;
        BlockHitResult blockHit = (BlockHitResult) hitResult;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;
        Block block = blockItem.getBlock();

        BlockPlaceContext context = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack, blockHit);
        BlockPlaceContext adjacentContext = BlockPlaceContext.at(context, blockHit.getBlockPos(), blockHit.getDirection());
        BlockPos placePos = adjacentContext.getClickedPos();

        BlockState stateToPlace = block.getStateForPlacement(context);
        if (stateToPlace == null) stateToPlace = block.defaultBlockState();

        VoxelShape shape = stateToPlace.getShape(level, placePos);

        AABB blockBox = shape.bounds().move(placePos.getX(), placePos.getY(), placePos.getZ());
        boolean collidesWithEntity = !level.getEntities(null, blockBox).isEmpty();

        boolean placeable = stateToPlace.canSurvive(level, placePos)
            && adjacentContext.canPlace()
            && player.isWithinBlockInteractionRange(placePos, 1)
            && !collidesWithEntity;

        int outlineColor = placeable ? TwoDimensionalReloadedClient.CONFIG.placeableOutlineColor : TwoDimensionalReloadedClient.CONFIG.nonPlaceableOutlineColor;
        float outlineWidth = TwoDimensionalReloadedClient.CONFIG.placementOutlineWidth;

        if (TwoDimensionalReloadedClient.CONFIG.shouldRenderPlacementOutline) {
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.lines());
            Camera camera = minecraft.gameRenderer.getMainCamera();
            renderPlacementOutline(poseStack, vertexConsumer, shape, placePos, camera, outlineColor, outlineWidth);
        }

        if(!TwoDimensionalReloadedClient.CONFIG.blockRenderMode.equals(ClientConfig.RenderStyle.OUTLINE_ONLY)) {
            renderPlacementPreview(poseStack, placePos, stateToPlace, placeable);
        }
    }

    public static void renderPlacementOutline(PoseStack poseStack, VertexConsumer vertexConsumer, VoxelShape shape, BlockPos pos, Camera camera, int outlineColor, float outlineWidth) {
        ShapeRenderer.renderShape(
            poseStack,
            vertexConsumer,
            shape,
            pos.getX() - camera.position().x,
            pos.getY() - camera.position().y,
            pos.getZ() - camera.position().z,
            outlineColor,
            outlineWidth
        );
    }

    public static void renderPlacementPreview(PoseStack poseStack, BlockPos placePos, BlockState stateToPlace, boolean placeable) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(Sheets.translucentBlockItemSheet());
        BlockRenderDispatcher blockRenderDispatcher = minecraft.getBlockRenderer();
        BlockStateModel model = blockRenderDispatcher.getBlockModel(stateToPlace);

        float r = placeable ? 0.0f : 1.0f;
        float g = placeable ? 1.0f : 0.0f;
        float b = 0.0f;
        float a = 0.5f;

        poseStack.pushPose();
        poseStack.translate(placePos.getX() - camera.position().x(), placePos.getY() - camera.position().y(), placePos.getZ() - camera.position().z());

        if(TwoDimensionalReloadedClient.CONFIG.blockRenderMode.equals(ClientConfig.RenderStyle.FULL_BLOCK)) {
            ModelBlockRenderer.renderModel(poseStack.last(), vertexConsumer, model, r, g, b, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        } else if(TwoDimensionalReloadedClient.CONFIG.blockRenderMode.equals(ClientConfig.RenderStyle.GHOST_BLOCK)) {
            renderModelWithAlpha(poseStack.last(), vertexConsumer, model, r, g, b, a, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }
}