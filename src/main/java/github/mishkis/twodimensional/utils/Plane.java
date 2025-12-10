package github.mishkis.twodimensional.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Plane {
    public static double CULL_DIST = -0.5;
    public List<Entity> containedEntities = new ArrayList<Entity>();

    private Vec3 offset;
    private double yaw;

    private Vec3 normal;
    private double slope;

    public Plane(Vec3 offset, double yaw) {
        this.offset = offset;
        this.yaw = yaw;

        this.slope = Math.tan(yaw);
        updateValues();
    }

    public void setOffset(Vec3 offset) {
        this.offset = offset;
        updateValues();
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
        updateValues();
    }

    public Vec3 getOffset() {
        return offset;
    }

    public double getYaw() {
        return yaw;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public double getSlope() {
        return slope;
    }

    private void updateValues() {
        this.slope = Math.tan(yaw);
        if (slope == 0) {
            // prevent division by 0
            this.slope = 0.0000001;
        }

        this.slope = Mth.clamp(slope, -99, 99);

        this.normal = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
    }

    public Vec3 intersectPoint(Vec3 point) {
        // slope(x - offset.x) + offset.z = (-1/slope)(x - point.x) + point.z
        double x = (slope * offset.x - offset.z + point.x / slope + point.z) / (slope + 1 / slope);
        double z = slope * (x - offset.x) + offset.z;

        return new Vec3(x, point.y, z);
    }

    // Positive is defined as being counter-clockwise
    public double sdf(Vec3 point) {
        Vec3 intersect = intersectPoint(point);

        Vec3 to_point = new Vec3(point.x - intersect.x, 0, point.z - intersect.z);
        return to_point.length() * Mth.sign(to_point.dot(getNormal()));
    }

    public static boolean shouldCull(BlockPos blockPos, Plane plane) {
        if (plane != null) {
            double dist = plane.sdf(blockPos.getCenter());
            return dist <= Plane.CULL_DIST || dist > 32;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "offset=" + offset +
                ", yaw=" + yaw +
                '}';
    }
}
