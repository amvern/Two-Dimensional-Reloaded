package github.mishkis.twodimensional.utils;

import net.minecraft.network.encryption.ClientPlayerSession;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Plane {
    public static double CULL_DIST = -0.5;

    private Vec3d offset;
    private double yaw;

    private Vec3d normal;
    private double slope;

    public Plane(Vec3d offset, double yaw) {
        this.offset = offset;
        this.yaw = yaw;

        this.slope = Math.tan(yaw);
        updateValues();
    }

    public void setOffset(Vec3d offset) {
        this.offset = offset;
        updateValues();
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
        updateValues();
    }

    public Vec3d getOffset() {
        return offset;
    }

    public double getYaw() {
        return yaw;
    }

    public Vec3d getNormal() {
        return normal;
    }

    public double getSlope() {
        return slope;
    }

    private void updateValues() {
        this.slope = Math.tan(yaw);
        if (this.slope == 0) {
            // prevent division by 0
            this.slope = 0.00001;
        }
        this.normal = offset.add(-Math.sin(yaw), 0, Math.cos(yaw));
    }

    // Positive is defined as being counter-clockwise
    public double sdf(Vec3d point) {
        Vec3d intersect = intersectPoint(point);

        Vec3d to_point = new Vec3d(point.x - intersect.x, 0, point.z - intersect.z);
        return to_point.length() * MathHelper.sign(to_point.dotProduct(normal));
    }

    public Vec3d intersectPoint(Vec3d point) {
        // slope(x - offset.x) + offset.z = (-1/slope)(x - point.x) + point.z
        double x = (slope * offset.x - offset.z + point.x / slope + point.z) / (slope + 1 / slope);
        double z = slope * (x - offset.x) + offset.z;

        return new Vec3d(x, 0., z);
    }

    @Override
    public String toString() {
        return "Plane{" +
                "offset=" + offset +
                ", yaw=" + yaw +
                '}';
    }
}
