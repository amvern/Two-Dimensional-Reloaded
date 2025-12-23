package github.amvern.twodimensionalreloaded.client.mixin;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
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

    @Shadow @Final private static Identifier CROSSHAIR_SPRITE;

    @Shadow @Final private static Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE;

    @Shadow @Final private static Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE;

    @Shadow @Final private static Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE;

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
                    if (!this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)) {
                        guiGraphics.nextStratum();
                        int i = 15;
                        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SPRITE, (int)(mouseX) - 7, (int)(mouseY) - 7, 15, 15);
                        if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                            float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                            boolean bl = false;
                            if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                                bl = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                                bl &= this.minecraft.crosshairPickEntity.isAlive();
                                AttackRange attackRange = (AttackRange)this.minecraft.player.getActiveItem().get(DataComponents.ATTACK_RANGE);
                                bl &= attackRange == null || attackRange.isInRange(this.minecraft.player, this.minecraft.hitResult.getLocation());
                            }

                            int j = (int)(mouseX) - 7 + 16;
                            int k = (int)(mouseY) - 7 - 8;
                            if (bl) {
                                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, (int)(mouseX) - 7, (int)(mouseY) + 12, 16, 16);
                            } else if (f < 1.0F) {
                                int l = (int)(f * 17.0F);
                                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, (int)(mouseX) - 7, (int)(mouseY) + 12, 16, 4);
                                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, (int)(mouseX) - 7, (int)(mouseY) + 12, l, 4);
                            }
                        }
                    }
                }
            }
    }
}
