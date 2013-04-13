package org.bitbucket.MrTwiggy.ContentLib.Profiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bitbucket.MrTwiggy.ContentLib.Content.Content;
import org.bitbucket.MrTwiggy.ContentLib.Managers.Manager;
import org.bitbucket.MrTwiggy.ProfileLib.Profiles.SpecificPlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ContentProfile extends SpecificPlayerProfile
{

	private String playerName; // The name of the player profile whose this belongs to
	private Player player; // The player object
	
	private Manager managerSelection; // The player's current content selection
	private Content contentSelection;
	
	/*
	 ----------CONSTRUCTORS--------
	 */
	
	
	/**
	 * Constructor
	 * @param playerName
	 */
	public ContentProfile(String playerName)
	{
		super(playerName);
		
		this.playerName = playerName;
		this.player = Bukkit.getPlayer(playerName);
		
		this.managerSelection = null;
		this.contentSelection = null;
	}
	
	/**
	 * Called when the player quits the server
	 */
	public void onPlayerQuit()
	{
		
	}
	
	public Manager getManagerSelection()
	{
		return managerSelection;
	}
	
	public void setManagerSelection(Manager managerSelection)
	{
		this.managerSelection = managerSelection;
	}
	
	public Content getContentSelection()
	{
		return contentSelection;
	}
	
	public void setContentSelection(Content contentSelection)
	{
		this.contentSelection = contentSelection;
	}
	
	/*
	 ----------PLAYER MANIPULATION LOGIC--------
	 */
	
	/**
	 * Send the player the message
	 * @param message
	 */
	public void sendMessage(String message)
	{
		player.sendMessage(message);
	}
	
	/**
	 * Send the player all the messages contained in the array
	 * @param messages
	 */
	public void sendMessage(String[] messages)
	{
		for (String message : messages)
		{
			sendMessage(message);
		}
	}
	
	/**
	 * Send the player all the messages contained in the list
	 * @param messages
	 */
	public void sendMessage(List<String> messages)
	{
		for (String message : messages)
		{
			sendMessage(message);
		}
	}

	
	/*
	 ----------PLAYER INVENTORY MANIPULATION--------
	 */
	
	/**
	 * Attempts to remove a specific material of given amount from this player's inventory
	 */
	@SuppressWarnings("deprecation")
	public boolean removeItem(int amount, Material material, String itemName)
	{
		HashMap<Integer,? extends ItemStack> inventoryMaterials = player.getInventory().all(material);

		int materialsToRemove = amount;
		for(Entry<Integer,? extends ItemStack> entry : inventoryMaterials.entrySet())
		{
			if (materialsToRemove <= 0)
				break;

			if (itemName == null || (entry.getValue().getItemMeta().getDisplayName() != null &&
					entry.getValue().getItemMeta().getDisplayName().equalsIgnoreCase(itemName)))
			{
				if(entry.getValue().getAmount() == materialsToRemove)
				{
					player.getInventory().setItem(entry.getKey(), new ItemStack(Material.AIR, 0));
					materialsToRemove = 0;
				}
				else if(entry.getValue().getAmount() > materialsToRemove)
				{
					ItemStack replacement = new ItemStack(material, (entry.getValue().getAmount() - materialsToRemove));
					ItemMeta replacementMeta = replacement.getItemMeta();
					replacementMeta.setDisplayName(itemName);
					replacement.setItemMeta(replacementMeta);
					
					player.getInventory().setItem(entry.getKey(), replacement);
					materialsToRemove = 0;
				}
				else
				{
					int inStack = entry.getValue().getAmount();
					player.getInventory().setItem(entry.getKey(), new ItemStack(Material.AIR, 0));
					materialsToRemove -= inStack;
				}
			}
		}

		player.updateInventory();
		return materialsToRemove == 0;
	}

	/**
	 * Attempts to remove a specific material of given amount from this player's inventory
	 */
	public boolean removeItem(ItemStack item)
	{
		return removeItem(item.getAmount(), item.getType(), item.getItemMeta().getDisplayName());
	}
	
	/**
	 * Checks if a specific material of given amount is available in this player's inventory
	 */
	public boolean hasItem(int amount, Material material, String itemName)
	{
		HashMap<Integer,? extends ItemStack> inventoryMaterials = player.getInventory().all(material);

		int totalMaterial = 0;
		for(Entry<Integer,? extends ItemStack> entry : inventoryMaterials.entrySet())
		{
			if (itemName == null || (entry.getValue().getItemMeta().getDisplayName() != null &&
					entry.getValue().getItemMeta().getDisplayName().equalsIgnoreCase(itemName)))
			{
				totalMaterial += entry.getValue().getAmount();
			}
		}

		return (totalMaterial >= amount);
	}
	
	/**
	 * Checks if a specific material of given amount is available in this player's inventory
	 */
	public boolean hasItem(ItemStack item)
	{
		return hasItem(item.getAmount(), item.getType(), item.getItemMeta().getDisplayName());
	}
 	
	/*
	 ----------PUBLIC ACCESSORS--------
	 */
	
	/**
	 * Public accessor for 'playerName'
	 * @return playerName
	 */
	public String getPlayerName()
	{
		return playerName;
	}

	/**
	 * Public accessor for 'player'
	 * @return player
	 */
	public Player getPlayer()
	{
		return player;
	}
}
