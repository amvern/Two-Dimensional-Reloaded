package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @Redirect(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    private boolean twoDIsFirstPerson(CameraType instance) {
        return true;
    }

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void renderFire(PoseStack poseStack, MultiBufferSource multiBufferSource, TextureAtlasSprite textureAtlasSprite, CallbackInfo ci) {
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderTypes.fireScreenEffect(textureAtlasSprite.atlasLocation()));
        float f = textureAtlasSprite.getU0();
        float g = textureAtlasSprite.getU1();
        float h = textureAtlasSprite.getV0();
        float i = textureAtlasSprite.getV1();

        for (int k = 0; k < 4; k++) {
            poseStack.pushPose();
            poseStack.translate(-(k * 2 - 3) * 0.42F, -0.7F, -0.8F);

            float u0 = (k % 2 == 0) ? f : g;
            float u1 = (k % 2 == 0) ? g : f;

            Matrix4f matrix4f = poseStack.last().pose();
            vertexConsumer.addVertex(matrix4f, -0.5F, -0.5F, -0.5F).setUv(u1, i).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            vertexConsumer.addVertex(matrix4f, 0.5F, -0.5F, -0.5F).setUv(u0, i).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            vertexConsumer.addVertex(matrix4f, 0.5F, 0.5F, -0.5F).setUv(u0, h).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            vertexConsumer.addVertex(matrix4f, -0.5F, 0.5F, -0.5F).setUv(u1, h).setColor(1.0F, 1.0F, 1.0F, 0.9F);

            poseStack.popPose();
        }

        ci.cancel();
    }

}