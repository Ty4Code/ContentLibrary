package org.bitbucket.MrTwiggy.ContentLib.Profiles;

import org.bitbucket.MrTwiggy.ContentLib.Content.Content;
import org.bitbucket.MrTwiggy.ContentLib.Managers.Manager;
import org.bitbucket.MrTwiggy.ProfileLib.Profiles.SpecificPlayerProfile;

public class ContentProfile extends SpecificPlayerProfile
{
	
	private Manager managerSelection; // The player's current manager selection
	public Manager getManagerSelection(){ return managerSelection; }
	public void setManagerSelection(Manager managerSelection){ this.managerSelection = managerSelection; }
	
	private Content contentSelection; // The player's current content selection
	public Content getContentSelection(){ return contentSelection; }
	public void setContentSelection(Content contentSelection){ this.contentSelection = contentSelection; }
	
	/*
	 ----------CONSTRUCTORS--------
	 */
	
	/**
	 * Constructor
	 * @param playerName - The name of the player this profile is being created for
	 */
	public ContentProfile(String playerName)
	{
		super(playerName);
		
		this.managerSelection = null;
		this.contentSelection = null;
	}
}
