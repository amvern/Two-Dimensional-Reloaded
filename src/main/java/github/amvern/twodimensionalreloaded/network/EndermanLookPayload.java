package github.amvern.twodimensionalreloaded.network;

import github.amvern.twodimensionalreloaded.TwoDimensionalReloaded;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EndermanLookPayload(int entityId, boolean looking) implements CustomPacketPayload {

    public static final Type<EndermanLookPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    TwoDimensionalReloaded.MOD_ID,
                    "enderman_look"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, EndermanLookPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.entityId());
                        buf.writeBoolean(payload.looking());
                    },
                    buf -> new EndermanLookPayload(
                            buf.readInt(),
                            buf.readBoolean()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}