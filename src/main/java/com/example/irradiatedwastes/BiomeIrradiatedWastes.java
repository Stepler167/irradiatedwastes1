package com.example.irradiatedwastes;

import net.minecraft.block.BlockDirt;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeIrradiatedWastes extends Biome {

    public BiomeIrradiatedWastes() {
        super(new BiomeProperties("Irradiated Wastes")
                .setBaseHeight(0.12F)
                .setHeightVariation(0.12F)
                .setTemperature(1.4F)
                .setRainfall(0.0F)
                .setRainDisabled()
                .setWaterColor(0x7CFF3A));

        topBlock = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
        fillerBlock = topBlock;

        spawnableCreatureList.clear();
        spawnableMonsterList.add(new SpawnListEntry(EntityHusk.class, 25, 2, 4));

        decorator.treesPerChunk = -999;
        decorator.flowersPerChunk = 0;
        decorator.grassPerChunk = 0;
        decorator.deadBushPerChunk = 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos) {
        return 0x7A8B3A;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
        return 0x5C6B22;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature) {
        return 0x9FB86A;
    }
}
