package com.raus.wowozela;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GameModeListener implements Listener
{
	@EventHandler
	public void onSwitch(PlayerItemHeldEvent event)
	{
		// Get stuff
		Player ply = event.getPlayer();
		ItemStack item = ply.getInventory().getItemInMainHand();
		ItemStack off_item = ply.getInventory().getItemInOffHand();
		
		ItemMeta meta;
		
		if (item != null)
		{
			// Get meta
			meta = item.getItemMeta();
		}
		else if (off_item != null)
		{
			// Get meta
			meta = off_item.getItemMeta();
		}
		else
		{
			// Nothing to see here
			return;
		}
		
		// Is it an instrument?
		PersistentDataContainer container = meta.getPersistentDataContainer();
		if (container.has(Main.getKey(), PersistentDataType.STRING))
		{
			ply.setGameMode(GameMode.ADVENTURE);
		}
		else
		{
			ply.setGameMode(GameMode.SURVIVAL);
		}
	}
}