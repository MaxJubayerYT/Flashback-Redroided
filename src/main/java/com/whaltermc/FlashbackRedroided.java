package com.whaltermc;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashbackRedroided implements ModInitializer {

    public static final String MOD_ID = "flashback-redroided";

    public static final Logger LOGGER =
            LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info(
                "Flashback Redroided loaded - ImGui remap + Android patches"
        );
    }
}