package com.raus.wowozela;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin
{
	private static Main instance;
	private static NamespacedKey key;
	private List<NamespacedKey> keys = new ArrayList<NamespacedKey>();
	
	public Main()
	{
		instance = this;
	}
	
	@Override
	public void onEnable()
	{
		// Namespace
		key = new NamespacedKey(this, "wowozela");
		
		// Save config
		saveDefaultConfig();
		
		// Register command
		this.getCommand("wowozela").setExecutor(new ReloadCommand());
		
		// Listener
		getServer().getPluginManager().registerEvents(new InstrumentListener(), this);
		//getServer().getPluginManager().registerEvents(new GameModeListener(), this);
		
		buildRecipes();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static NamespacedKey getKey()
	{
		return key;
	}
	
	public void rebuildRecipes()
	{
		// Remove old ones first
		for (NamespacedKey key : keys)
		{
			Bukkit.removeRecipe(key);
		}
		keys.clear();
		
		// Reload config
		reloadConfig();
		
		buildRecipes();
	}
	
	private void buildRecipes()
	{
		// Build recipes
		ConfigurationSection instruments = getConfig().getConfigurationSection("instruments");
		
		for (String name : instruments.getKeys(false))
		{
			// Get material
			String material = getConfig().getString("instruments." + name);
			
			// Build item
			ItemStack item = new ItemStack(Material.getMaterial(material));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RESET + WordUtils.capitalizeFully(name.replace('_', ' ')));
			meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, name);
			item.setItemMeta(meta);
			
			// Generate key
			NamespacedKey key = new NamespacedKey(this, name);
			keys.add(key);
			
			// Create recipe
			ShapelessRecipe recipe = new ShapelessRecipe(key, item);
			recipe.addIngredient(Material.NOTE_BLOCK);
			recipe.addIngredient(Material.getMaterial(material));
			Bukkit.addRecipe(recipe);
		}
	}
}