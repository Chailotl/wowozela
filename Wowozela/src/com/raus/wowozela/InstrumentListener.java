package com.raus.wowozela;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InstrumentListener implements Listener
{
	@EventHandler
	public void onPlay(PlayerInteractEvent event)
	{
		// Get stuff
		Player ply = event.getPlayer();
		ItemStack item = event.getItem();
		Action act = event.getAction();
		
		// Valid action
		if (item != null
			&& (((act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.OFF_HAND)
			|| ((act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND)))
		{
			// Get meta
			ItemMeta meta = item.getItemMeta();
			PersistentDataContainer container = meta.getPersistentDataContainer();
			
			// Is it an instrument?
			if (container.has(Main.getKey(), PersistentDataType.STRING))
			{
				// Get instrument
				String instrument = container.get(Main.getKey(), PersistentDataType.STRING);
				
				Sound sound;
				if (instrument.equals("bass"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_BASS;
				}
				else if (instrument.equals("snare"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_SNARE;
				}
				else if (instrument.equals("hat"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_HAT;
				}
				else if (instrument.equals("bass_drum"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
				}
				else if (instrument.equals("glockenspiel"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_BELL;
				}
				else if (instrument.equals("flute"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_FLUTE;
				}
				else if (instrument.equals("chime"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_CHIME;
				}
				else if (instrument.equals("guitar"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_GUITAR;
				}
				else if (instrument.equals("xylophone"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_XYLOPHONE;
				}
				else if (instrument.equals("vibraphone"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
				}
				else if (instrument.equals("cow_bell"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_COW_BELL;
				}
				else if (instrument.equals("didgeridoo"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO;
				}
				else if (instrument.equals("bit"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_BIT;
				}
				else if (instrument.equals("banjo"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_BANJO;
				}
				else if (instrument.equals("electric_piano"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_PLING;
				}
				else if (instrument.equals("harp"))
				{
					sound = Sound.BLOCK_NOTE_BLOCK_HARP;
				}
				else
				{
					return;
				}
				
				// Location
				Location loc = ply.getLocation();
				
				// Get pitch
				float pitch = -loc.getPitch();
				pitch = (((pitch + 90f) * 1.5f) / 180f) + 0.5f;
				
				ply.getWorld().playSound(loc, sound, 1, pitch);
				
				// Swing arm
				if (event.getHand() == EquipmentSlot.OFF_HAND)
				{
					ply.swingOffHand();
				}
				
				// Show note particle
				double note = (-loc.getPitch() + 90f) / 180D;
				loc.add(0, 2, 0);
				ply.getWorld().spawnParticle(Particle.NOTE, loc, 0, note, 0, 0, 1);
				
				// Cancel event
				event.setCancelled(true);
			}
		}
	}
}