package com.m4ssive.totemcounterv2;

import com.m4ssive.totemcounterv2.gui.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent, TotemCounterV2Mod.getInstance().getConfig());
    }
}























