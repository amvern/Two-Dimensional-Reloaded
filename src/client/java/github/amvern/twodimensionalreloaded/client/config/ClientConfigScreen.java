package github.amvern.twodimensionalreloaded.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientConfigScreen {

    public static Screen create(Screen parent, ClientConfig config) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("Two Dimensional: Reloaded Options"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory placementOutlineCategory = builder.getOrCreateCategory(Component.literal("Placement Outline"));

        placementOutlineCategory.addEntry(entryBuilder.startTextDescription(
                Component.literal("Camera Options")
        ).build());

        placementOutlineCategory.addEntry(entryBuilder.startEnumSelector(
                        Component.literal("Camera Mode"),
                        ClientConfig.CameraMode.class, config.cameraMode)
                .setDefaultValue(ClientConfig.CameraMode.DYNAMIC)
                .setSaveConsumer(value ->config.cameraMode = value)
                .setEnumNameProvider(style -> Component.nullToEmpty(style.name().replace("_", " ")))
                .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startTextDescription(
            Component.literal("Block Placement Guide Options. Defaults to 32-bit ARGB color. Hex colors like #RRGGBB will automatically become opaque.")
        ).build());

        placementOutlineCategory.addEntry(entryBuilder.startBooleanToggle(Component.literal("Render Block Placement Guide"), config.renderBlockPlacementGuide)
            .setDefaultValue(false)
            .setSaveConsumer(value -> config.renderBlockPlacementGuide = value)
            .setTooltip(Component.literal("Render placement preview"))
            .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startEnumSelector(
            Component.literal("Placement Preview Style"),
            ClientConfig.RenderStyle.class, config.blockRenderMode)
            .setDefaultValue(ClientConfig.RenderStyle.GHOST_BLOCK)
            .setSaveConsumer(value -> config.blockRenderMode = value)
            .setEnumNameProvider(style -> Component.nullToEmpty(style.name().replace("_", " ")))
            .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Show Outline"),
            config.shouldRenderPlacementOutline)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.shouldRenderPlacementOutline = value)
            .setTooltip(Component.literal("Render outline around placement preview"))
            .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startIntSlider(
            Component.literal("Placement Outline Width"),
            (int)(config.placementOutlineWidth * 10),
            (1 * 10),
            (8 * 10))
            .setSaveConsumer(value -> config.placementOutlineWidth = value / (float) 10)
            .setDefaultValue((int)(4f * 10))
            .setTextGetter(value -> Component.literal(String.format("%.1f", value / (float) 10)))
            .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startColorField(
            Component.literal("Placeable Outline"),
            config.placeableOutlineColor)
            .setDefaultValue(0x8000FF00)
            .setAlphaMode(true)
            .setSaveConsumer(value -> config.placeableOutlineColor = fixAlpha(value))
            .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startColorField(
            Component.literal("Non-Placeable Outline"),
            config.nonPlaceableOutlineColor)
            .setDefaultValue(0x80FF0000)
            .setAlphaMode(true)
            .setSaveConsumer(value -> config.nonPlaceableOutlineColor = fixAlpha(value))
            .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startTextDescription(
            Component.literal("Toggle Fog Environments for Water/Lava/Powerdered Snow."))
            .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Render Fog"),
            config.renderFogEnvironments)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.renderFogEnvironments = value)
            .build()
        );

        return builder.build();
    }

    private static int fixAlpha(int color) {
        if ((color & 0xFF000000) == 0) {
            return color | 0xFF000000;
        }
        return color;
    }
}