package com.yara.borderprogression;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = MyMod.MODID, version = "1.0", name = "BorderProgression", acceptedMinecraftVersions = "[1.7.10]")
public class MyMod {

    public static final String MODID = "borderprogression";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(
        clientSide = "com.yara.borderprogression.ClientProxy",
        serverSide = "com.yara.borderprogression.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        BorderChunkUpdater borderChunkUpdater = new BorderChunkUpdater();
        MinecraftForge.EVENT_BUS.register(borderChunkUpdater);
        MinecraftForge.EVENT_BUS.register(new ChunksUnlockedManager());
        MinecraftForge.EVENT_BUS.register(new CancelUnclaim());
        MinecraftForge.EVENT_BUS.register(new BorderOutsideProtection());
        FMLCommonHandler.instance()
            .bus()
            .register(new ServerTickHandler());

        // FMLCommonHandler.instance().bus().register(borderHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(borderChunkUpdater);

    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
