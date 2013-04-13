package org.bitbucket.MrTwiggy.ContentLib.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.main__.util.SerializationConfig.*;

import org.bitbucket.MrTwiggy.ContentLib.ContentLib;
import org.bitbucket.MrTwiggy.ContentLib.Content.Content;
import org.bitbucket.MrTwiggy.ContentLib.Content.Previewable;
import org.bitbucket.MrTwiggy.ContentLib.Profiles.ContentProfile;
import org.bitbucket.MrTwiggy.ContentLib.Serializors.LocationPropertySerializor;
import org.bitbucket.MrTwiggy.ContentLib.Utility.LocationSerializable;
import org.bitbucket.MrTwiggy.ContentLib.Utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ContentManager
{

	public enum CommandType
	{
		LIST,
		CREATE,
		REMOVE,
		SET,
		PREVIEW,
		RENAME,
		DELETE,
		SAVE;
	}
	
	public static final String CONTENT_SECTION = "content_saves";
	
	private static ContentManager instance; // Singleton instance
	
	ArrayList<Manager> contentManagers;
	
	/**
	 * Constructor
	 */
	private ContentManager()
	{
		contentManagers = new ArrayList<Manager>();
	}
	
	/**
	 * Loads a manager and checks to see if it has any saved Content data to load
	 * @param manager
	 * @return true, if manager was successfully loaded, false otherwise
	 */
	public boolean loadManager(Manager manager)
	{
		if (getManager(manager.getManagerName()) == null)
		{			
			String managerName = manager.getManagerName();
			
			ConfigurationSection config = ContentLib.getInstance().getConfig();
			
			String contentSectionName = "content_saves";
			
			for (String key : config.getKeys(false))
			{
				contentSectionName = key;
			}
			
			ConfigurationSection savesConfig = config.getConfigurationSection(contentSectionName);
			ConfigurationSection contentSection = savesConfig.getConfigurationSection(managerName);
			
			if (contentSection != null)
			{
				//New content to be loaded
				ArrayList<Content> newContents = new ArrayList<Content>();
				
				for (String key : contentSection.getKeys(false))
				{												
					ConfigurationSection specificContentSection = contentSection.getConfigurationSection(key);
					
					Map<String,Object> contentValues = new HashMap<String,Object>();
					
					for (String key2 : specificContentSection.getKeys(false))
					{						
						contentValues.put(key2, specificContentSection.get(key2));
					}
					
					//Create, load, and add the new content
					Content newContent = manager.createContent(contentValues);
					
					if (newContent != null)
					{
						newContent.setName(key);
						newContents.add(newContent);
					}
				}
				
				manager.loadContent(newContents);
			}
			
			//Add the manager to the list of managers
			contentManagers.add(manager);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Save all the content data to file
	 */
	public void saveContent()
	{
		ContentLib.getInstance().getConfig().set(CONTENT_SECTION, null);
		
		for (Manager contentManager : contentManagers)
		{
			saveContent(contentManager);
		}
	}
	
	/**
	 * Save the content for a specific manager
	 * @param contentSectionName
	 * @param contentManager
	 */
	public void saveContent(Manager contentManager)
	{
		ContentLib.getInstance().getConfig().set(CONTENT_SECTION + "." + contentManager.getManagerName(), null);
		
		for (Content content : contentManager.getContents())
		{
			if (content.getName().equalsIgnoreCase("Lava Tomb"))
				continue;
			
			ContentLib.getInstance().getConfig().set(CONTENT_SECTION + "." + contentManager.getManagerName() + "."
					+ content.getName(), content.serialize());
		}
		
		ContentLib.getInstance().saveConfig();
	}
	
	/**
	 * Get the manager with matching name
	 * @param name
	 * @return manager, if one exists with matching name, false otherwise
	 */
	public Manager getManager(String name)
	{
		for (Manager contentManager : contentManagers)
		{
			if (contentManager.getManagerName().equalsIgnoreCase(name))
			{
				return contentManager;
			}
		}
		
		return null;
	}
	
	/*
	 * COMMAND STUFF
	 */
	
	/**
	 * The Clear Command
	 * @param playerProfile
	 * @param args
	 */
	public void clear(ContentProfile playerProfile, String[] args)
	{
		Content content = playerProfile.getContentSelection();
		
		if (content != null)
		{
			if (content.clearContentProperty(args[0]))
			{
				playerProfile.sendMessage(ChatColor.GREEN + "You successfully cleared " + ChatColor.GRAY + args[0]);
			}
			else
			{
				playerProfile.sendMessage(ChatColor.RED + "Unable to clear " + ChatColor.GRAY + args[0]);
			}
		}
		else
		{
			playerProfile.sendMessage(ChatColor.RED + "You must have content selected!");
		}
	}
	
	/**
	 * The Save Command
	 * @param playerProfile
	 * @param args
	 */
	public void save(ContentProfile playerProfile, String[] args)
	{
		//save --- Player must have manager selected
		//save all
		//save sounds ---- Manager must exist by the name sounds
		Manager manager = playerProfile.getManagerSelection();
		
		if (args.length == 1)
		{
			manager = getManager(args[0]);
		}
		
		
		if (args.length == 1 && args[0].equalsIgnoreCase("all"))
		{
			saveContent();
			playerProfile.sendMessage(ChatColor.GREEN + "You have successfully saved all content!");
		}
		else
		{
			if (manager != null)
			{
				if (manager.hasPermission(playerProfile.getPlayer(), CommandType.SAVE))
				{
					saveContent(manager);
					playerProfile.sendMessage(ChatColor.GREEN + "You have successfully saved the "
							+ ChatColor.AQUA + manager.getManagerName() + ChatColor.GREEN + " collection!");
				}
				else
				{
					playerProfile.sendMessage(ChatColor.RED + "You do not have permission!");
				}
			}
			else
			{
				playerProfile.sendMessage(ChatColor.RED + "You must have a content collection specified or selected!");
			}
		}
	}
	
	/**
	 * The Add Command
	 * @param playerProfile
	 * @param args
	 */
	public void add(ContentProfile playerProfile, String[] args)
	{
		if (args.length < 1)
		{
			playerProfile.sendMessage(ChatColor.RED + "Invalid format!");
			return;
		}
		
		Content content = playerProfile.getContentSelection();
		
		if (content != null)
		{
			try
			{	
				//set PROPERTY --- Special case for locations ONLY
				
				String property = args[0];
				
				String value = "";
				Class<? extends Serializor<?, ?>> serializorClass;
				
				if (args.length == 1)
				{
					value = LocationSerializable.serialize(playerProfile.getPlayer().getLocation());
					serializorClass = LocationPropertySerializor.class;
				}
				else
				{
					value = Utility.MergeEnding(args, 1);
					serializorClass = (Class<? extends Serializor<?, ?>>) org.bitbucket.MrTwiggy.ContentLib.Serializors.DefaultSerializor.class;
				}
				
                Serializor serializor = content.serializorCache.getInstance(serializorClass, this);
				
				if (content.addContentProperty(property, value, serializor))
				{
					playerProfile.sendMessage(ChatColor.GREEN + "You have added "
							+ ChatColor.AQUA + value
							+ ChatColor.GREEN + " to " 
							+ ChatColor.GOLD + args[0]);
				}
				else
				{
					playerProfile.sendMessage(ChatColor.RED + "Unable to add to "
							+ ChatColor.GRAY + args[0]);
				}
			}
			catch (Exception e)
			{
				playerProfile.sendMessage(ChatColor.RED + "Unable to add to "
						+ ChatColor.GRAY + args[0]);
				
				return;
			}
		}
		else
		{
			playerProfile.sendMessage(ChatColor.RED + "You must have content selected!");
		}
	}
	
	/**
	 * The Preview Command
	 * @param playerProfile
	 * @param args
	 */
	public void preview(ContentProfile playerProfile, String[] args)
	{
		//preview CONTENT
		//preview MANAGER CONTENT
		//preview
		
		Content contentPreview = playerProfile.getContentSelection();
		
		int argsUsed = 0;
		
		if (contentPreview == null && args.length >= 1)
		{
			Manager managerSelection = playerProfile.getManagerSelection();
			
			if (managerSelection != null)
			{
				contentPreview = managerSelection.getContent(args[0]);
				argsUsed = 1;
			}
		}
		
		if (contentPreview == null && args.length >= 2)
		{
			Manager managerSelection = getManager(args[0]);
			
			if (managerSelection != null)
			{
				contentPreview = managerSelection.getContent(args[1]);
				argsUsed = 2;
			}
		}
		
		//PREVIEW AND MESSAGE THE PLAYER
		if (contentPreview instanceof Previewable)
		{
			Previewable previewable = (Previewable)contentPreview;
			
			if (previewable.canPreview(playerProfile.getPlayer()))
			{
				String[] previewArguments = new String[args.length - argsUsed];
				
				for (int i = argsUsed; i < args.length; i++)
				{
					previewArguments[i - argsUsed] = args[i];
				}
				
				previewable.preview(playerProfile.getPlayer(), previewArguments);
				
				playerProfile.sendMessage(ChatColor.GREEN + "Previewing " 
						+ ChatColor.AQUA + contentPreview.getName() + ChatColor.GREEN
						+ "...");
			}
		}
		else if (contentPreview == null)
		{
			playerProfile.sendMessage(ChatColor.RED + "Unable to find content to preview with your input!");
		}
		else
		{
			playerProfile.sendMessage(ChatColor.RED + "The content "
					 + ChatColor.AQUA + contentPreview.getName()
					 + ChatColor.RED + " is not previewable!");
		}
	}
	
	/**
	 * The Select Command
	 * @param playerProfile
	 * @param args
	 */
	public void select(ContentProfile playerProfile, String[] args)
	{	
		Manager manager = null;
		Content content = null;
		//select 74mm Javelin Missile
		//SELECT THE CONTENT
		if (args.length >= 1)
		{
			Manager managerSelection = getManager(args[0]);
			
			if (managerSelection != null)
			{
				manager = managerSelection;
			}
			else if (playerProfile.getManagerSelection() != null)
			{
				content = playerProfile.getManagerSelection().getContent(Utility.MergeEnding(args, 0));
			}
			
			if (content == null && args.length >= 2 && manager != null)
			{
				content = manager.getContent(Utility.MergeEnding(args, 1));
			}
		}
		
		
		//SEND THE PLAYER MESSAGE
		if (manager != null && content != null)
		{
			//You have selected 'content' from the 'manager' collection
			playerProfile.sendMessage(ChatColor.GREEN + "You have selected "
					+ ChatColor.AQUA + content.getName() + ChatColor.GREEN
					+ " from " + ChatColor.GOLD + manager.getManagerName());
			
			playerProfile.setManagerSelection(manager);
			playerProfile.setContentSelection(content);
		}
		else if (manager != null)
		{
			//You have select the 'manager' collection
			playerProfile.sendMessage(ChatColor.GREEN + "You have selected "
					+ ChatColor.GOLD + manager.getManagerName());
			playerProfile.setManagerSelection(manager);
		}
		else if (content != null)
		{
			//You have selected 'content'
			playerProfile.sendMessage(ChatColor.GREEN + "You have selected "
					+ ChatColor.AQUA + content.getName());
			playerProfile.setContentSelection(content);
		}
		else
		{
			//There were no collections or content matching your selection
			playerProfile.sendMessage(ChatColor.RED + "There were no collections or content matching your selection!");
		}
		
		// /select sounds hit1
		// /select sounds
		// /select hit1
		// 
	}
	
	/**
	 * The Create Command
	 * @param playerProfile
	 * @param args
	 */
	public void create(ContentProfile playerProfile, String[] args)
	{
		Manager manager = playerProfile.getManagerSelection();
		
		if (manager != null)
		{
			if (args.length >= 1)
			{
				String contentName = args[0];
				
				String[] contentArguments = new String[args.length - 1];
				
				for (int i = 1; i < args.length; i++)
				{
					contentArguments[i - 1] = args[i];
				}
				
				Content content = manager.createContent(contentName, contentArguments);
				
				if (content != null)
				{
					manager.addContent(content);
					playerProfile.sendMessage(ChatColor.GREEN + "You have successfully created "
							+ ChatColor.GOLD + contentName);
				}
				else
				{
					playerProfile.sendMessage(ChatColor.RED + "Incorrect format!");
				}

			}
		}
		else
		{
			playerProfile.sendMessage(ChatColor.RED + "You must have a content collection selected already!");
		}
	}
	
	public void remove(ContentProfile playerProfile, String[] args)
	{
		if (args.length == 2)
		{
			Content content = playerProfile.getContentSelection();
			
			if (content != null)
			{
				String property = args[0];
				
				try
				{
					int removeIndex = Integer.parseInt(args[1]);
					
					if (content.removeContentProperty(property, removeIndex))
					{
						playerProfile.sendMessage(ChatColor.GREEN + "You have successfully removed "
								+ ChatColor.GOLD + args[1] + ChatColor.GREEN + " from "
								+ ChatColor.GRAY + args[0]);
					}
					else
					{
						playerProfile.sendMessage(ChatColor.RED + "Unable to remove " + ChatColor.GOLD + args[1]);
					}
				}
				catch (Exception e)
				{
					playerProfile.sendMessage(ChatColor.RED + "Incorrect format!!");
					return;
				}
			}
			else
			{
				playerProfile.sendMessage(ChatColor.RED + "You must have content selected!");
			}
		}
		else
		{
			playerProfile.sendMessage(ChatColor.RED + "Incorrect format!");
		}
	}
	
	/**
	 * The Delete Command
	 * @param playerProfile
	 * @param args
	 */
	public void delete(ContentProfile playerProfile, String[] args)
	{
		if (args.length < 1)
		{
			playerProfile.sendMessage(ChatColor.RED + "Incorrect command format!");
			return;
		}
		
		if (playerProfile.getManagerSelection() != null)
		{
			if (playerProfile.getManagerSelection().hasPermission(playerProfile.getPlayer(), CommandType.DELETE))
			{
				if (playerProfile.getManagerSelection().removeContent(args[0]))
				{
					playerProfile.sendMessage(ChatColor.GREEN + "You have successfully removed " + ChatColor.GOLD + args[0]
							+ ChatColor.GREEN + " from " + ChatColor.AQUA + playerProfile.getManagerSelection().getManagerName());
				}
				else
				{
					playerProfile.sendMessage(ChatColor.RED + "Unable to find the content " + ChatColor.GOLD + args[0]);
				}
				
			}
			else
			{
				playerProfile.sendMessage(ChatColor.RED + "You do not have permission!");
			}
		}
		else
		{
			playerProfile.sendMessage(ChatColor.RED + "You must have a Content Collection selected first!");
		}
	}
	
	/**
	 * The Rename Command
	 * @param playerProfile
	 * @param args
	 */
	public void rename(ContentProfile playerProfile, String[] args)
	{
		if (args.length == 1)
		{
			Content contentRename = playerProfile.getContentSelection();
			
			if (contentRename != null)
			{
				if (contentRename.hasPermission(playerProfile.getPlayer(), CommandType.RENAME))
				{
					playerProfile.sendMessage(ChatColor.GREEN + "You have succesfully renamed " + ChatColor.GRAY + contentRename.getName()
							+ ChatColor.GREEN + " to " + ChatColor.GOLD + args[0]);
					contentRename.setName(args[0]);
				}
				else
				{
					playerProfile.sendMessage(ChatColor.RED + "You do not have permission!");
				}
			}
		}
	}
	
	/**
	 * The Set Command
	 * @param playerProfile
	 * @param args
	 */
	public void set(ContentProfile playerProfile, String[] args)
	{
		//set PROPERTY VALUE
		
		if (args.length >= 1)
		{
			Content contentSet = playerProfile.getContentSelection();
			
			if (contentSet != null)
			{
				try
				{	
					String property = args[0];
					
					String value = "";
					
					if (args.length >= 1)
					{
						value = Utility.MergeEnding(args, 1);
					}
					
					contentSet.setContentProperty(property, value, playerProfile.getPlayer());
					playerProfile.sendMessage(ChatColor.GREEN + "You have set "
							+ ChatColor.GOLD + args[0]
									+ ChatColor.GREEN + " to " 
									+ ChatColor.AQUA + value);
				}
				catch (NoSuchPropertyException e)
				{
					playerProfile.sendMessage(ChatColor.RED + "There is no property by the name of "
							+ ChatColor.GRAY + args[0]);
					
					return;
				}
			}
			else
			{
				playerProfile.sendMessage(ChatColor.RED + "You must have Content selected first!");
			}
		}
	}
	
	/**
	 * The Info Command Logic
	 * @param playerProfile
	 * @param contentInfo
	 */
	public void info(ContentProfile playerProfile, Content contentInfo)
	{
		if (contentInfo != null)
		{
			ArrayList<String> messages = new ArrayList<String>();
			messages.add(ChatColor.YELLOW + "----------------------");
			messages.add(ChatColor.GOLD + "Name: " + ChatColor.AQUA + contentInfo.getName());
			
			if (contentInfo.getDescription() != null && contentInfo.getDescription() != "")
			{
				messages.add(ChatColor.GOLD + "Description: " + ChatColor.AQUA + contentInfo.getDescription());
			}
			
			for (Entry<String,Object> value : contentInfo.serialize().entrySet())
			{
				if (value.getValue() != contentInfo.getName() && value.getValue() != contentInfo.getDescription())
				{
					messages.add(ChatColor.GOLD + value.getKey() + ": " + ChatColor.AQUA + value.getValue());
				}
			}
			
			messages.add(ChatColor.YELLOW + "----------------------");
			playerProfile.sendMessage(messages);
		}
	}
	
	/**
	 * The Info Command
	 * @param playerProfile
	 * @param args
	 */
	public void info(ContentProfile playerProfile, String[] args)
	{
		//info
		//info CONTENT
		//info MANAGER CONTENT
		
		Content contentInfo = null;
		
		if (args.length == 0)
		{
			contentInfo = playerProfile.getContentSelection();
		}
		else if (args.length == 1)
		{
			if (playerProfile.getManagerSelection() != null)
			{
				playerProfile.getManagerSelection().getContent(args[0]);
			}
		}
		else if (args.length == 2)
		{
			Manager managerSelection = getManager(args[0]);
			
			if (managerSelection != null)
			{
				contentInfo = managerSelection.getContent(args[1]);
			}
		}
		
		info(playerProfile, contentInfo);	
	}
	
	/**
	 * The List Command
	 * @param playerProfile
	 * @param args
	 */
	public void list(ContentProfile playerProfile, String[] args)
	{
		//list collections
		//list
		//list MANAGER
		
		//DETERMINE THE LIST SELECTION
		Manager managerList = null;
		
		if (args.length == 0)
		{
			managerList = playerProfile.getManagerSelection();
		}
		else if (args.length == 1)
		{
			if (!args[0].equalsIgnoreCase("collections") && !args[0].equalsIgnoreCase("all"))
			{
				managerList = getManager(args[0]);
			}
		}
		
		//MESSAGE THE PLAYER
		ArrayList<String> messages = new ArrayList<String>();
		messages.add(ChatColor.YELLOW + "----------------------");
		
		if (managerList != null)
		{
			
			for (Content content : managerList.getContents())
			{
				String managerOutput = ChatColor.GRAY + "- " 
						+ ((content instanceof Previewable) ? ChatColor.GREEN : ChatColor.AQUA) + content.getName() 
						+ ((content.getDescription() != null && content.getDescription() != "") 
								? ": " + ChatColor.GOLD + content.getDescription() : "");
				
				messages.add(managerOutput);
			}
		}
		else
		{
			
			for (Manager contentManager : contentManagers)
			{
				String managerOutput = ChatColor.GRAY + contentManager.getManagerName()
						+ " - " + ChatColor.AQUA + contentManager.getManagerDescription();
				
				messages.add(managerOutput);
			}
			
		}
		
		messages.add(ChatColor.YELLOW + "----------------------");
		playerProfile.sendMessage(messages);
	}
	
	/**
	 * Initialize a given Manager of Content
	 * @param manager
	 */
	public void initializeManager(Manager manager)
	{
		manager.initializeInstance(manager);
		loadManager(manager);
	}
	
	/**
	 * Initialize the singleton instance of content manager
	 */
	public static void initializeInstance()
	{
		if (instance == null)
		{
			instance = new ContentManager();
		}
	}
	
	/**
	 * Get singleton instance of ContentManager
	 * @return contentManager
	 */
	public static ContentManager getInstance()
	{
		return instance;
	}
	
}
