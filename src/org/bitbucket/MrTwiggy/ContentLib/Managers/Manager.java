package org.bitbucket.MrTwiggy.ContentLib.Managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bitbucket.MrTwiggy.ContentLib.Content.Content;
import org.bitbucket.MrTwiggy.ContentLib.Managers.ContentManager.CommandType;
import org.bukkit.entity.Player;

public abstract class Manager
{
	private String managerName; // Name of the content collection being managed (Ex: 'Sounds')
	private String managerDescription; // Description of the content collection being managed (Ex: 'Collection of custom sounds')
	private ArrayList<Content> contents; // List of available Content (Ex: 'Sound1, Sound2, Sound3')
	
	/**
	 * Constructor
	 * @param contentName
	 * @param contentDescription
	 * @param contentClass
	 */
	public Manager(String managerName, String managerDescription)
	{
		this.managerName = managerName;
		this.managerDescription = managerDescription;
		contents = new ArrayList<Content>();
	}
	
	/**
	 * Get the content with name
	 * @param name - content name of the Content being searched for
	 * @return content, if one exists with the name, null otherwise
	 */
	public Content getContent(String name)
	{
		Content result = null;
		
		@SuppressWarnings("unchecked")
		ArrayList<Content> contents = (ArrayList<Content>) this.contents.clone();
		
		while (contents.size() >= 3)
		{
			int index = (int) Math.floor(contents.size() / 2D);
			
			Content content = contents.get(index);
			
			if (content.getName().equalsIgnoreCase(name))
			{
				result = content;
				break;
			}
			else if (name.compareToIgnoreCase(content.getName()) < 0)
			{
				contents.subList(index + 1, contents.size()).clear();
			}
			else
			{
				contents.subList(0, index).clear();
			}
		}
		
		if (result == null)
		{
			for (Content content : contents)
			{
				if (content.getName().equalsIgnoreCase(name))
				{
					result = content;
					break;
				}
			}
		}
		
		return result;
	}

	/**
	 * Get whether this manager has content with a matching name in it's collection
	 * @param contentName - The name of the content being searched for
	 * @return true, if there is content with a matching name in the Content Collection, false otherwise
	 */
	public boolean hasContent(String contentName)
	{
		return getContent(contentName) != null;
	}
	
	/**
	 * Add content to this manager
	 * @param newContent
	 * @return true, if the content was added successfully, false otherwse
	 */
	public boolean addContent(Content newContent)
	{
		return addContent(newContent, false);
	}
	
	/**
	 * Add the content object to the contents list being managed
	 * @param content - The content to be added
	 * @return true, if the content was successfully added, false otherwise
	 */
	public boolean addContent(Content newContent, boolean force)
	{
		boolean hasContent = hasContent(newContent.getName());
		
		if (hasContent && force)
		{
			removeContent(newContent.getName());
			hasContent = false;
		}
		
		if (!hasContent)
		{
			int i = 0;
			
			for (; i < contents.size(); i++)
			{
				Content content = contents.get(i);
				
				if (newContent.getName().compareToIgnoreCase(content.getName()) <= 0)
				{
					break;
				}
			}
			
			contents.add(i, newContent);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Remove the content with 'name' from the contents being managed
	 * @param name - The name of the content to be removed
	 */
	public boolean removeContent(String name)
	{
		Content content = getContent(name);
		
		if (content != null)
		{
			contents.remove(content);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Load in contents retrieved from SerializedConfiguration
	 * @param newContents - the new contents to be loaded
	 */
	public void loadContent(ArrayList<Content> newContents)
	{
		contents.clear();
		
		for (Content newContent : newContents)
		{
			this.addContent(newContent);
		}
	}

	/**
	 * Serialize this Content Manager and it's contents
	 * @return the serialized form of this Manager and it's content in Map<String,Object> format
	 */
	public Map<String, Object> serialize()
	{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		Map<String, Object> contentMap = new LinkedHashMap<String, Object>();
		
		for (Content content : contents)
		{
			contentMap.put(content.getName(), content.serialize());
		}
		
		result.put(getManagerName(), contentMap);
		
		return result;
	}
	
	/**
	 * Public accessor for 'contentName'
	 * @return contentName
	 */
	public String getManagerName()
	{
		return managerName;
	}
	
	/**
	 * Public accessor for 'contentDescription'
	 * @return contentDescription
	 */
	public String getManagerDescription()
	{
		return managerDescription;
	}

	/**
	 * Public accessor for 'contents'
	 * @return contents
	 */
	public ArrayList<Content> getContents()
	{
		return contents;
	}

	/**
	 * Get whether the player has permission to access this content collection through CommandType Command
	 * @param player - The player seeking permission to execute CommandType
	 * @return true, if player has permission, false otherwise
	 */
	public abstract boolean hasPermission(Player player, CommandType commandType);
		
	/**
	 * Create corresponding content when given the appropriate content values
	 * @param contentValues - The values and propreties of the content to be loaded/created
	 * @return content object
	 */
	public abstract Content createContent(Map<String,Object> contentValues);

	/**
	 * Create content with matching name when given specific chat-based arguments
	 * @param contentName - The name of the content to be created
	 * @param args - The chat based arguments being passed in
	 * @return the new content created with matching name
	 */
	public abstract Content createContent(String contentName, String[] args);
	
	/**
	 * Initialize the singleton instance of this Manager
	 */
	public abstract void initializeInstance(Manager manager);
}
