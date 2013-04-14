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
		super(ContentLib.PLUGIN_NAME, ContentProfile.class);
	}
	
}
