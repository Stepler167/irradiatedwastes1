package com.example.irradiatedwastes;

import java.util.Collections;
import java.util.Random;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/** /wastes : teleports you to the nearest Irradiated Wastes biome (for testing). */
public class CommandWastes extends CommandBase {

    @Override
    public String getName() {
        return "wastes";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/wastes - teleport to the nearest Irradiated Wastes biome";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        World world = player.world;
        BlockPos here = player.getPosition();
        BlockPos found = world.getBiomeProvider().findBiomePosition(
                here.getX(), here.getZ(), 8000, Collections.singletonList(ModBiomes.WASTES), new Random());
        if (found == null) {
            throw new CommandException("No Irradiated Wastes found within 8000 blocks.");
        }
        BlockPos top = world.getTopSolidOrLiquidBlock(found);
        player.setPositionAndUpdate(found.getX() + 0.5, top.getY(), found.getZ() + 0.5);
        sender.sendMessage(new TextComponentString(
                "Teleported to Irradiated Wastes at " + found.getX() + ", " + found.getZ()));
    }
}
