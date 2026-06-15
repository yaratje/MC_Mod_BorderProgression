package com.yara.borderprogression;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        BorderHandler handler = new BorderHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }
}
