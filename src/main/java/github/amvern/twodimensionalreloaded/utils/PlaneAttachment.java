package github.amvern.twodimensionalreloaded.utils;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class PlaneAttachment {
    public static final AttachmentType<Plane> PLAYER_PLANE = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(TwoDimensionalReloaded.MOD_ID, "plane"),
            builder -> builder
                    .initializer(() -> new Plane())                 // Default Plane instance
//                    .syncWith(
//                            null,
//                            AttachmentSyncPredicate.all()
//                    )
                    .copyOnDeath()
    );

    // Helper methods
    public static Plane get(Player player) {
        return player.getAttachedOrSet(PLAYER_PLANE, new Plane());
    }

    public static void set(Player player, Plane plane) {
        player.setAttached(PLAYER_PLANE, plane);
    }
}
