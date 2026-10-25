package com.example.irradiatedwastes;

import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

// "after:hbm" = load after HBM if it is present, but it is not required.
@Mod(modid = IrradiatedWastes.MODID, name = "Irradiated Wastes", version = "1.0.0",
        acceptedMinecraftVersions = "[1.12.2]", dependencies = "after:hbm")
public class IrradiatedWastes {
    public static final String MODID = "irradiatedwastes";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LootTableList.register(WastelandStructures.BUNKER_LOOT);
        LootTableList.register(WastelandStructures.CACHE_LOOT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new WastelandStructures(), 20);
        MinecraftForge.EVENT_BUS.register(new RadiationHandler());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandWastes());
    }
}
