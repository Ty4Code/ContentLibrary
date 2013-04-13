package org.bitbucket.MrTwiggy.ContentLib.Profiles;

import org.bitbucket.MrTwiggy.ProfileLib.Profiles.SpecificPlayerProfile;
import org.bitbucket.MrTwiggy.ProfileLib.Profiles.SpecificPlayerProfiler;

public class ContentProfiler extends SpecificPlayerProfiler
{

	public ContentProfiler(String pluginName) 
	{
		super(pluginName);
	}

	@Override
	public SpecificPlayerProfile getDefaultProfile(String playerName) 
	{
		return new ContentProfile(playerName);
	}

	
}
