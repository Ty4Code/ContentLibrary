package org.bitbucket.MrTwiggy.ContentLib;

import org.bitbucket.MrTwiggy.ContentLib.Listeners.PlayerListener;
import org.bitbucket.MrTwiggy.ContentLib.Managers.ContentManager;
import org.bitbucket.MrTwiggy.ContentLib.Profiles.ContentProfile;
import org.bitbucket.MrTwiggy.ContentLib.Profiles.ContentProfiler;
import org.bitbucket.MrTwiggy.ProfileLib.Managers.PlayerProfileManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ContentLib extends JavaPlugin
{
	private static ContentLib instance; // Singleton instance of Content Library
	
	public static final String PLUGIN_NAME = "ContentLib"; // The plugin name used for profiling and logging
	
	/**
	 * Called when plugin is enabled
	 */
	public void onEnable()
	{
		//Set up the singleton instance of this plugin
		instance = this;
		
		//Register listeners
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		
		//Initialize the content manager
		ContentManager.initializeInstance();
		
		//Initialize the profiler for ContentLibrary
		PlayerProfileManager.getInstance().initializeProfiler(new ContentProfiler(PLUGIN_NAME));
	}
	
	/**
	 * Called when plugin is disabled
	 */
	public void onDisable()
	{
		System.out.println("Saving content data...");
		ContentManager.getInstance().saveContent();
		System.out.println("Content data saved!");
	}
	
	/**
	 * Called when a player issues a command
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			return true;
		}
		
		ContentProfile playerProfile = (ContentProfile) PlayerProfileManager.getInstance().getSpecificPlayerProfile(sender.getName(), PLUGIN_NAME);
		
		if (!playerProfile.getPlayer().isOp())
		{
			playerProfile.sendMessage(ChatColor.RED + "You do not have permission!");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("preview"))
		{
			ContentManager.getInstance().preview(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("set"))
		{
			ContentManager.getInstance().set(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("select"))
		{
			ContentManager.getInstance().select(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("delete"))
		{
			ContentManager.getInstance().delete(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("add"))
		{
			ContentManager.getInstance().add(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("clear"))
		{
			ContentManager.getInstance().clear(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("list"))
		{
			ContentManager.getInstance().list(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("contentinfo") || cmd.getName().equalsIgnoreCase("cinfo"))
		{
			ContentManager.getInstance().info(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("rename"))
		{
			ContentManager.getInstance().rename(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("create"))
		{
			ContentManager.getInstance().create(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("remove"))
		{
			ContentManager.getInstance().remove(playerProfile, args);
		}
		else if(cmd.getName().equalsIgnoreCase("save"))
		{
			ContentManager.getInstance().save(playerProfile, args);
		}
		
		return true; 
	}
	
	public static ContentLib getInstance()
	{
		return instance;
	}
}
