package com.example.irradiatedwastes;

import java.util.Random;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

/**
 * Procedural structures (no NBT files needed). Each one stays within 7 blocks of its center,
 * which keeps it inside the 16x16 area Forge lets a world generator safely touch.
 */
public class WastelandStructures implements IWorldGenerator {

    public static final ResourceLocation BUNKER_LOOT =
            new ResourceLocation(IrradiatedWastes.MODID, "chests/wasteland_bunker");
    public static final ResourceLocation CACHE_LOOT =
            new ResourceLocation(IrradiatedWastes.MODID, "chests/wasteland_cache");

    /** 1 in N chunks tries to place a structure. Lower = more structures. */
    private static final int CHUNK_CHANCE = 10;

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world,
                         IChunkGenerator generator, IChunkProvider provider) {
        if (world.provider.getDimension() != 0) return;
        if (rand.nextInt(CHUNK_CHANCE) != 0) return;

        BlockPos probe = new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16);
        if (world.getBiome(probe) != ModBiomes.WASTES) return;

        BlockPos ground = world.getTopSolidOrLiquidBlock(probe);
        if (world.getBlockState(ground.down()).getMaterial().isLiquid()) return;

        int roll = rand.nextInt(10);
        if (roll < 3) bunker(world, rand, ground);
        else if (roll < 5) coolingTower(world, rand, ground);
        else if (roll < 8) barrelField(world, rand, ground);
        else crater(world, rand, ground);
    }

    // ---------------------------------------------------------------- bunker

    /** Underground ruined bunker: two rooms, a ladder shaft, two loot chests. */
    private void bunker(World w, Random r, BlockPos g) {
        IBlockState air = Blocks.AIR.getDefaultState();
        final int b = -6; // floor level, relative to ground

        // shell
        for (int x = -5; x <= 5; x++)
            for (int z = -5; z <= 5; z++)
                for (int y = b - 1; y <= b + 4; y++)
                    set(w, g.add(x, y, z), brick(r));

        // interior
        fill(w, g, -4, b, -4, 4, b + 3, 4, air);

        // partition wall with a doorway and a few holes
        for (int z = -4; z <= 4; z++) {
            for (int y = b; y <= b + 3; y++) {
                if (z == 0 && y <= b + 2) continue;
                if (r.nextInt(6) == 0) continue;
                set(w, g.add(0, y, z), brick(r));
            }
        }

        // access shaft (x 3..4, z 3..4) from the ceiling up to the surface
        fill(w, g, 3, b + 4, 3, 4, -1, 4, air);
        IBlockState ladder = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.WEST);
        for (int y = b; y <= -1; y++) set(w, g.add(4, y, 4), ladder);

        // rubble around the hatch
        for (int x = 2; x <= 5; x++)
            for (int z = 2; z <= 5; z++) {
                if (x >= 3 && x <= 4 && z >= 3 && z <= 4) continue;
                BlockPos p = g.add(x, 0, z);
                if (w.isAirBlock(p) && r.nextInt(3) == 0) set(w, p, Blocks.COBBLESTONE.getDefaultState());
            }

        // furnishing
        chest(w, r, g.add(-3, b, -3), EnumFacing.EAST, BUNKER_LOOT);
        chest(w, r, g.add(-3, b, 3), EnumFacing.EAST, BUNKER_LOOT);
        set(w, g.add(-3, b + 3, 0), Blocks.GLOWSTONE.getDefaultState());
        set(w, g.add(2, b + 3, -2), Blocks.GLOWSTONE.getDefaultState());
        for (int i = 0; i < 8; i++) {
            BlockPos p = g.add(r.nextInt(9) - 4, b + r.nextInt(4), r.nextInt(9) - 4);
            if (w.isAirBlock(p)) set(w, p, Blocks.WEB.getDefaultState());
        }
    }

    // ----------------------------------------------------------- cooling tower

    /** Ruined cooling tower: tall concrete ring, taller on one side, glowing green pool inside. */
    private void coolingTower(World w, Random r, BlockPos g) {
        IBlockState silver = Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
        IBlockState gray = Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);

        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                double d = Math.sqrt(dx * dx + dz * dz);
                if (d > 6.4) continue;
                int top = ground(w, g, dx, dz).getY();

                if (d >= 5.0) {
                    int h = 5 + (int) ((dx + 6) * 0.8) + r.nextInt(3);
                    for (int y = top - 2; y < top + h; y++) {
                        if (y > top + 3 && r.nextInt(8) == 0) continue; // broken holes
                        set(w, new BlockPos(g.getX() + dx, y, g.getZ() + dz), r.nextInt(5) == 0 ? gray : silver);
                    }
                } else if (d < 3.2) {
                    set(w, new BlockPos(g.getX() + dx, top - 2, g.getZ() + dz), Blocks.GLOWSTONE.getDefaultState());
                    set(w, new BlockPos(g.getX() + dx, top - 1, g.getZ() + dz), Blocks.WATER.getDefaultState());
                }
            }
        }
    }

    // ------------------------------------------------------------ barrel field

    /** Scattered waste drums (cauldrons) with a hidden cache in the middle. */
    private void barrelField(World w, Random r, BlockPos g) {
        IBlockState drum = Blocks.CAULDRON.getDefaultState().withProperty(BlockCauldron.LEVEL, 3);
        int n = 8 + r.nextInt(6);
        for (int i = 0; i < n; i++) {
            BlockPos p = ground(w, g, r.nextInt(13) - 6, r.nextInt(13) - 6);
            if (w.isAirBlock(p)) set(w, p, drum);
        }
        BlockPos c = ground(w, g, 0, 0);
        chest(w, r, c, EnumFacing.values()[2 + r.nextInt(4)], CACHE_LOOT);
        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            BlockPos p = ground(w, g, f.getFrontOffsetX(), f.getFrontOffsetZ());
            if (w.isAirBlock(p)) set(w, p, drum);
        }
    }

    // ------------------------------------------------------------------ crater

    /** Bowl-shaped crater with a scorched floor and a supply crate at the center. */
    private void crater(World w, Random r, BlockPos g) {
        final int radius = 6;
        final int depth = 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double d = Math.sqrt(dx * dx + dz * dz);
                if (d > radius) continue;
                BlockPos top = ground(w, g, dx, dz);
                int dep = (int) Math.round(depth * (1.0 - d / radius));
                int floorY = top.getY() - 1 - dep;

                if (dep == 0) {
                    if (r.nextInt(2) == 0) set(w, top.down(), Blocks.COAL_BLOCK.getDefaultState());
                    continue;
                }
                for (int y = floorY + 1; y < top.getY(); y++)
                    set(w, new BlockPos(top.getX(), y, top.getZ()), Blocks.AIR.getDefaultState());

                IBlockState floor = d < 2.2 ? Blocks.OBSIDIAN.getDefaultState()
                        : d < 4.2 ? Blocks.COAL_BLOCK.getDefaultState()
                        : Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
                set(w, new BlockPos(top.getX(), floorY, top.getZ()), floor);
            }
        }
        BlockPos center = new BlockPos(g.getX(), g.getY() - 1 - depth, g.getZ());
        chest(w, r, center.up(), EnumFacing.NORTH, BUNKER_LOOT);
    }

    // ----------------------------------------------------------------- helpers

    private static BlockPos ground(World w, BlockPos g, int dx, int dz) {
        return w.getTopSolidOrLiquidBlock(new BlockPos(g.getX() + dx, 0, g.getZ() + dz));
    }

    private static void set(World w, BlockPos p, IBlockState s) {
        w.setBlockState(p, s, 2);
    }

    private static void fill(World w, BlockPos o, int x1, int y1, int z1, int x2, int y2, int z2, IBlockState s) {
        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                for (int z = z1; z <= z2; z++)
                    set(w, o.add(x, y, z), s);
    }

    private static IBlockState brick(Random r) {
        int n = r.nextInt(10);
        BlockStoneBrick.EnumType t = n < 5 ? BlockStoneBrick.EnumType.DEFAULT
                : n < 8 ? BlockStoneBrick.EnumType.CRACKED : BlockStoneBrick.EnumType.MOSSY;
        return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, t);
    }

    private static void chest(World w, Random r, BlockPos p, EnumFacing facing, ResourceLocation table) {
        set(w, p, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing));
        TileEntity te = w.getTileEntity(p);
        if (te instanceof TileEntityChest) {
            ((TileEntityChest) te).setLootTable(table, r.nextLong());
        }
    }
}
