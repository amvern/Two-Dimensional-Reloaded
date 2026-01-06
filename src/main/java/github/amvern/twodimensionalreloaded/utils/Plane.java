package github.amvern.twodimensionalreloaded.utils;

import com.mojang.serialization.Codec;
import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class Plane {
    private static final double CULL_DIST = -0.5;
    private static final double z = 0.5;

    public Plane() {}

    public static double getZ() { return z; }
    public static double getCullDist() { return CULL_DIST; }

    public static Vec3 intersectPoint(Vec3 point) {
        return new Vec3(point.x, point.y, z);
    }

    public static double sdf(Vec3 point) {
        return point.z - z;
    }

    public static boolean shouldCull(BlockPos blockPos) {
        double dist = Plane.sdf(blockPos.getCenter());
        return dist <= CULL_DIST;
    }

    @Override
    public String toString() {
        return "Plane{z= " + z + " }";
    }

    public static final AttachmentType<Boolean> PLANE_ENTITY_FLAG = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "isOnPlane"),
            builder -> builder
                    .initializer(()-> false)
                    .persistent(Codec.BOOL)
                    .syncWith(
                            ByteBufCodecs.BOOL,
                            AttachmentSyncPredicate.all()
                    )
                    .copyOnDeath()
    );
}