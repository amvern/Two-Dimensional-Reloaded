package github.amvern.twodimensionalreloaded.mixin;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This mixin adjusts the end island pillars to align to z = 0. Currently reduces the number and spread to ensure they fit on the island
 * */
@Mixin(SpikeFeature.class)
public class SpikeFeatureMixin {

    private static final int CUSTOM_SPIKE_COUNT = 5;
    private static final int MIN_DISTANCE_FROM_PORTAL = 20;
    private static final int SPIKE_DISTANCE = 35;

    @Inject(method = "getSpikesForLevel", at = @At("RETURN"), cancellable = true)
    private static void injectGetSpikesForLevel(WorldGenLevel worldGenLevel, CallbackInfoReturnable<List<SpikeFeature.EndSpike>> cir) {
        List<SpikeFeature.EndSpike> spikes = new ArrayList<>();
        Random random = new Random(worldGenLevel.getSeed());

        int totalLength = (CUSTOM_SPIKE_COUNT - 1) * SPIKE_DISTANCE;
        int startX = -totalLength / 2;

        for (int i = 0; i < CUSTOM_SPIKE_COUNT; i++) {
            int x = startX + i * SPIKE_DISTANCE;

            if (Math.abs(x) < MIN_DISTANCE_FROM_PORTAL) {
                x += MIN_DISTANCE_FROM_PORTAL * (x < 0 ? -1 : 1);
            }

            int radius = 2 + random.nextInt(4);
            int height = 76 + random.nextInt(10);
            boolean guarded = random.nextBoolean();

            spikes.add(new SpikeFeature.EndSpike(x, 0, radius, height, guarded));
        }

        cir.setReturnValue(spikes);
    }

    @ModifyArgs(method = "placeSpike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;snapTo(DDDFF)V"), require = 0)
    private void adjustCrystalPos(Args args) {
        args.set(2, (double) args.get(2) + 0.2);
    }

    @Inject(method = "placeSpike", at = @At("RETURN"))
    private void carveSpikeHole(ServerLevelAccessor level, RandomSource random, SpikeConfiguration config, SpikeFeature.EndSpike spike, CallbackInfo ci) {
        int baseY = level.getHeight(Heightmap.Types.WORLD_SURFACE, spike.getCenterX() + spike.getRadius() + 1,0);
        int tunnelTopY = baseY + 2;

        TwoDimensionalReloaded.LOGGER.info("Spike baseY " + baseY + " Spike topY " + tunnelTopY);

        for (int y = baseY; y < tunnelTopY; y++) {
            for (int x = spike.getCenterX() - spike.getRadius(); x <= spike.getCenterX() + spike.getRadius(); x++) {
                BlockPos pos = new BlockPos(x, y, 0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

}








