package github.mishkis.twodimensional.network;

import github.mishkis.twodimensional.TwoDimensional;
import github.mishkis.twodimensional.utils.LayerMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record InteractionLayerPayload(LayerMode mode) implements CustomPacketPayload {

    public static final Type<InteractionLayerPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TwoDimensional.MOD_ID, "interaction_layer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InteractionLayerPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeEnum(payload.mode()),
                    buf -> new InteractionLayerPayload(buf.readEnum(LayerMode.class))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
