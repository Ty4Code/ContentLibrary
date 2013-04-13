package org.bitbucket.MrTwiggy.ContentLib.Profiles;

import org.bitbucket.MrTwiggy.ContentLib.ContentLib;
import org.bitbucket.MrTwiggy.ProfileLib.Profiles.SpecificPlayerProfile;
import org.bitbucket.MrTwiggy.ProfileLib.Profiles.SpecificPlayerProfiler;

public class ContentProfiler extends SpecificPlayerProfiler
{

	/**
	 * Constructor
	 */
	public ContentProfiler() 
	{
		super(ContentLib.PLUGIN_NAME);
	}

	/**
	 * Get the default profile for a new player
	 * @param playerName - THe name of the player who will own the new profile
	 * @return the new specific player profile
	 */
	@Override
	public SpecificPlayerProfile getDefaultProfile(String playerName) 
	{
		return new ContentProfile(playerName);
	}

	
}
