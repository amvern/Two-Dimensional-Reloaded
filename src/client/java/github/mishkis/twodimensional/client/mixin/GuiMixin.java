package github.mishkis.twodimensional.client.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Sorry you had to see this
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract boolean canRenderCrosshairForSpectator(@Nullable HitResult hitResult);

    @Shadow @Final private DebugScreenOverlay debugOverlay;

    @Shadow @Final private static ResourceLocation CROSSHAIR_SPRITE;

    @Shadow @Final private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE;

    @Shadow @Final private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE;

    @Shadow @Final private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE;

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    private void render2DCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
                MouseHandler mouse = minecraft.mouseHandler;
                Window window = minecraft.getWindow();

                double mouseX = mouse.xpos() * guiGraphics.guiWidth() / window.getScreenWidth();
                double mouseY = mouse.ypos() * guiGraphics.guiHeight() / window.getScreenHeight();
        Options options = this.minecraft.options;
        if (!options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                RenderSystem.enableBlend();
                if (this.debugOverlay.showDebugScreen() && !this.minecraft.player.isReducedDebugInfo() && !options.reducedDebugInfo().get()) {
                    Camera camera = this.minecraft.gameRenderer.getMainCamera();
                    Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
                    matrix4fStack.pushMatrix();
                    matrix4fStack.mul(guiGraphics.pose().last().pose());
                    matrix4fStack.translate((float) mouseX, (float) mouseY, 0.0F);
                    matrix4fStack.rotateX(-camera.getXRot() * (float) (Math.PI / 180.0));
                    matrix4fStack.rotateY(camera.getYRot() * (float) (Math.PI / 180.0));
                    matrix4fStack.scale(-1.0F, -1.0F, -1.0F);
                    RenderSystem.applyModelViewMatrix();
                    RenderSystem.renderCrosshair(10);
                    matrix4fStack.popMatrix();
                    RenderSystem.applyModelViewMatrix();
                } else {
                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ZERO
                    );
                    int i = 15;
                    guiGraphics.blitSprite(CROSSHAIR_SPRITE, (int)(mouseX) - 7, (int)(mouseY) - 7, 15, 15);
                    if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                        float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                        boolean bl = false;
                        if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                            bl = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                            bl &= this.minecraft.crosshairPickEntity.isAlive();
                        }

                        if (bl) {
                            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, (int)(mouseX) - 7, (int)(mouseY) + 12, 16, 16);
                        } else if (f < 1.0F) {
                            int l = (int)(f * 17.0F);
                            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, (int)(mouseX) - 7, (int)(mouseY) + 12, 16, 4);
                            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, (int)(mouseX) - 7, (int)(mouseY) + 12, l, 4);

                        }
                    }

                    RenderSystem.defaultBlendFunc();
                }

                RenderSystem.disableBlend();
            }
        }
    }
}
