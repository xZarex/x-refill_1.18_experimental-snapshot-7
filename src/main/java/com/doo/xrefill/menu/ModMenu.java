package com.doo.xrefill.menu;

import com.doo.xrefill.menu.screen.ModMenuScreen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuScreen::get;
    }
}
