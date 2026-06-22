package github.amvern.twodimensionalreloaded.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.amvern.twodimensionalreloaded.client.TwoDimensionalReloadedClient;
import github.amvern.twodimensionalreloaded.client.config.ClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class BlockPlacementGuide {
    private static final RandomSource RANDOM = RandomSource.create(42L);
    private static final List<BlockStateModelPart> PARTS_CACHE = new ArrayList<>();

    public static void renderPlacementGuide(PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (level == null || player == null) return;
        Camera camera = minecraft.gameRenderer.getMainCamera();

        HitResult hitResult = player.raycastHitResult(0.0f, player);
        if (hitResult.getType().equals(HitResult.Type.ENTITY)) return;
        BlockHitResult blockHit = (BlockHitResult) hitResult;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;
        Block block = blockItem.getBlock();

        BlockPlaceContext context = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack, blockHit);
        BlockPlaceContext adjacentContext = BlockPlaceContext.at(context, blockHit.getBlockPos(), blockHit.getDirection());
        BlockPos placePos = adjacentContext.getClickedPos();
        Vec3 renderPos = posRelativeToCamera(placePos, camera);

        BlockState stateToPlace = block.getStateForPlacement(context);
        if (stateToPlace == null) stateToPlace = block.defaultBlockState();

        VoxelShape shape = stateToPlace.getShape(level, placePos);
        if (shape.isEmpty()) return;

        AABB blockBox = shape.bounds().move(placePos.getX(), placePos.getY(), placePos.getZ());
        boolean collidesWithEntity = !level.getEntities(null, blockBox).isEmpty();

        boolean placeable = stateToPlace.canSurvive(level, placePos)
            && adjacentContext.canPlace()
            && player.isWithinBlockInteractionRange(placePos, 1)
            && !collidesWithEntity;

        boolean isFullBlockRender = TwoDimensionalReloadedClient.CONFIG.blockRenderMode.equals(ClientConfig.RenderStyle.FULL_BLOCK);
        boolean isOutlineOnlyRender = TwoDimensionalReloadedClient.CONFIG.blockRenderMode.equals(ClientConfig.RenderStyle.OUTLINE_ONLY);
        int outlineColor = placeable ? TwoDimensionalReloadedClient.CONFIG.getPlaceableOutlineColor() : TwoDimensionalReloadedClient.CONFIG.getNonPlaceableOutlineColor();
        int blockColor;
            if(placeable) {
                blockColor= isFullBlockRender
                    ? ARGB.white(1.0f)
                    : TwoDimensionalReloadedClient.CONFIG.getPlaceableBlockColor();
            } else {
                blockColor = isFullBlockRender
                    ? ARGB.color(1.0f, TwoDimensionalReloadedClient.CONFIG.getNonPlaceableBlockColor())
                    : TwoDimensionalReloadedClient.CONFIG.getNonPlaceableBlockColor();
            }

        float outlineWidth = TwoDimensionalReloadedClient.CONFIG.placementOutlineWidth;

        if (TwoDimensionalReloadedClient.CONFIG.shouldRenderPlacementOutline) {
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.lines());
            renderPlacementOutline(poseStack, vertexConsumer, bufferSource, shape, renderPos, outlineColor, outlineWidth);
        }

        if(!isOutlineOnlyRender) {
            renderPlacementPreview(minecraft, poseStack, placePos, renderPos, stateToPlace, blockColor);
        }
    }

    private static Vec3 posRelativeToCamera(BlockPos pos, Camera cam) {
        return new Vec3(
            pos.getX() - cam.position().x,
            pos.getY() - cam.position().y,
            pos.getZ() - cam.position().z
        );
    }

    public static void renderPlacementOutline(PoseStack poseStack, VertexConsumer vertexConsumer, MultiBufferSource.BufferSource bufferSource, VoxelShape shape, Vec3 pos, int blockColor, float outlineWidth) {
        poseStack.pushPose();

        ShapeRenderer.renderShape(poseStack, vertexConsumer, shape, pos.x, pos.y, pos.z, blockColor, outlineWidth);

        poseStack.popPose();
        bufferSource.endBatch(RenderTypes.lines());
    }

    public static void renderPlacementPreview(
            Minecraft minecraft,
            PoseStack poseStack,
            BlockPos placePos,
            Vec3 guideRenderPos,
            BlockState stateToPlace,
            int blockColor
    ) {
        BlockStateModelSet modelSet = minecraft.getModelManager().getBlockStateModelSet();
        BlockStateModel model = modelSet.get(stateToPlace);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.translucentMovingBlock());

        poseStack.pushPose();
        poseStack.translate(guideRenderPos.x, guideRenderPos.y, guideRenderPos.z);

//        int light = LightCoordsUtil.FULL_BRIGHT;
        int light = LevelRenderer.getLightCoords(minecraft.level, placePos);
        int overlay = OverlayTexture.NO_OVERLAY;

        PARTS_CACHE.clear();
        model.collectParts(RANDOM, PARTS_CACHE);

        QuadInstance instance = new QuadInstance();
        instance.setLightCoords(light);
        instance.setOverlayCoords(overlay);
        instance.setColor(blockColor);

        for (BlockStateModelPart part : PARTS_CACHE) {
            for (Direction dir : Direction.values()) {
                for (BakedQuad quad : part.getQuads(dir)) {
                    buffer.putBakedQuad(poseStack.last(), quad, instance);
                }
            }

            for (BakedQuad quad : part.getQuads(null)) {
                buffer.putBakedQuad(poseStack.last(), quad, instance);
            }
        }

        poseStack.popPose();
        bufferSource.endBatch();
    }
}