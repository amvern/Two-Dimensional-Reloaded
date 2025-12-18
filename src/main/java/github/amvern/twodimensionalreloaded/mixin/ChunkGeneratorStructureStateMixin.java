package github.amvern.twodimensionalreloaded.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Mixin that tries to constrain vanilla stronghold placement to Plane.
 * Trying to make 2D placement still have proper "ring" distance just without spread
 * */
@Mixin(ChunkGeneratorStructureState.class)
public abstract class ChunkGeneratorStructureStateMixin {

    @Shadow @Final private Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> ringPositions;

    @Inject(method = "generateRingPositions", at = @At("HEAD"), cancellable = true)
    private void generateLinearStrongholds(
            Holder<StructureSet> holder,
            ConcentricRingsStructurePlacement placement,
            CallbackInfoReturnable<CompletableFuture<List<ChunkPos>>> cir
    ) {
        int distance = placement.distance();
        int count = placement.count();

        List<ChunkPos> positions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int ringIndex = i;
            int rChunks = 4 * distance + distance * ringIndex * 6;
            int x = rChunks;
            int z = 0;

            positions.add(new ChunkPos(x, z));
        }

        this.ringPositions.put(placement, CompletableFuture.completedFuture(positions));

        cir.setReturnValue(CompletableFuture.completedFuture(positions));
    }
}




