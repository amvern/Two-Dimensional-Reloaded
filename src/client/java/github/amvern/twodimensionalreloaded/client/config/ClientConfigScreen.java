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
                Component.literal("Configure Block Placement Guide options. Defaults to 32-bit ARGB color. Hex colors like #RRGGBB will automatically become opaque.")
        ).build());

        placementOutlineCategory.addEntry(entryBuilder.startBooleanToggle(Component.literal("Render Block Placement Guide"), config.renderBlockPlacementGuide)
                .setDefaultValue(false)
                .setSaveConsumer(value -> config.renderBlockPlacementGuide = value)
                .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startColorField(Component.literal("Placeable Outline"), config.placeableOutlineColor)
                .setDefaultValue(0x8000FF00)
                .setAlphaMode(true)
                .setSaveConsumer(value -> config.placeableOutlineColor = fixAlpha(value))
                .build()
        );

        placementOutlineCategory.addEntry(entryBuilder.startColorField(Component.literal("Non-Placeable Outline"), config.nonPlaceableOutlineColor)
                .setDefaultValue(0x80FF0000)
                .setAlphaMode(true)
                .setSaveConsumer(value -> config.nonPlaceableOutlineColor = fixAlpha(value))
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