package github.amvern.twodimensionalreloaded.utils;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class PlaneAttachment {
    public static final AttachmentType<Plane> ENTITY_PLANE = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "plane"),
            builder -> builder
                    .initializer(() -> new Plane())
//                    .syncWith(
//                            null,
//                            AttachmentSyncPredicate.all()
//                    )
                    .copyOnDeath()
    );

    public static Plane get(Entity entity) {
        return entity.getAttachedOrSet(ENTITY_PLANE, new Plane());
    }

    public static void set(Entity entity, Plane plane) {
        entity.setAttached(ENTITY_PLANE, plane);
    }
}
