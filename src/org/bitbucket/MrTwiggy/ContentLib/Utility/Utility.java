package org.bitbucket.MrTwiggy.ContentLib.Utility;

public class Utility
{

	public static String MergeEnding(String[] args, int startingIndex)
	{
		String value = "";
		
		for (int i = startingIndex; i < args.length; i++)
		{
			if (i > 1)
				value += " ";
			
			value += args[i];
		}
		
		return value;
	}
}
