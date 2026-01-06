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
}
