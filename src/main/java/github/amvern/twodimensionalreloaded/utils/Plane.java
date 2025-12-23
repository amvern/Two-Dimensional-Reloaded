package github.amvern.twodimensionalreloaded.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Plane {
    public static final double CULL_DIST = -0.5;
    private final double z = 0.5;
    private final Vec3 normal = new Vec3(0, 0, 1);

    public List<Entity> containedEntities = new ArrayList<Entity>();

    public Plane() {}

    public double getZ() {
        return z;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public Vec3 intersectPoint(Vec3 point) {
        return new Vec3(point.x, point.y, z);
    }

    public double sdf(Vec3 point) {
        return point.z - z;
    }

    public static boolean shouldCull(BlockPos blockPos, Plane plane) {
        if (plane != null) {
            double dist = plane.sdf(blockPos.getCenter());
            return dist <= CULL_DIST;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Plane{z= " + z + " }";
    }
}
