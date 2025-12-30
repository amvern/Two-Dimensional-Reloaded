package github.amvern.twodimensionalreloaded.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent ->  ClientConfigScreen.create(parent, AutoConfig.getConfigHolder(ClientConfig.class).getConfig());
    }

}