package com.doo.xrefill.menu;

import com.doo.xrefill.Refill;
import com.doo.xrefill.menu.screen.ModMenuScreen;
import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.Map;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuScreen::get;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of(Refill.ID, getModConfigScreenFactory());
    }
}
