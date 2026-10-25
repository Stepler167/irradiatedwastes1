package com.example.irradiatedwastes;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IrradiatedWastes.MODID)
public class ModBiomes {
    public static final Biome WASTES =
            new BiomeIrradiatedWastes().setRegistryName(IrradiatedWastes.MODID, "irradiated_wastes");

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        event.getRegistry().register(WASTES);
        BiomeDictionary.addTypes(WASTES,
                BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.DEAD,
                BiomeDictionary.Type.DRY, BiomeDictionary.Type.HOT);
        // Weight 12 vs. 30 for vanilla desert/savanna/plains. Raise it to make the biome more common.
        BiomeManager.addBiome(BiomeManager.BiomeType.DESERT, new BiomeManager.BiomeEntry(WASTES, 12));
    }
}
