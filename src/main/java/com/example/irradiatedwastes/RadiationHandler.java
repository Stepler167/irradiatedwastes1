package com.example.irradiatedwastes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Standalone "radiation": potion effects while standing in the biome without a full protective suit.
 * Swap applyRadiation() for a call into HBM's radiation system once you know its API.
 */
public class RadiationHandler {

    /** An armor piece counts as protective if its registry path contains one of these. */
    private static final String[] PROTECTIVE_KEYWORDS = { "hazmat" };

    private static final EntityEquipmentSlot[] ARMOR = {
            EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != Side.SERVER) return;
        EntityPlayer player = event.player;
        if (player.ticksExisted % 40 != 0) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (player.world.getBiome(player.getPosition()) != ModBiomes.WASTES) return;
        if (isProtected(player)) return;
        applyRadiation(player);
    }

    private void applyRadiation(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0));
        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 0));
        player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100, 0));
    }

    private boolean isProtected(EntityPlayer player) {
        for (EntityEquipmentSlot slot : ARMOR) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (stack.isEmpty()) return false;
            ResourceLocation name = stack.getItem().getRegistryName();
            if (name == null) return false;
            String path = name.getResourcePath();
            boolean ok = false;
            for (String key : PROTECTIVE_KEYWORDS) {
                if (path.contains(key)) { ok = true; break; }
            }
            if (!ok) return false;
        }
        return true;
    }
}
