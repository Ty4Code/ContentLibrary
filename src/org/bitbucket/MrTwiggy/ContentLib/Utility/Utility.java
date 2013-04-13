package org.bitbucket.MrTwiggy.ContentLib.Utility;

public class Utility
{

	/**
	 * Get a final string from the sum of all elements
	 * in a String array starting from 'startingIndex' to the end, inclusive
	 * @param args - The arguments that compose the string array
	 * @param startingIndex - The starting index for the merge
	 * @return the sum of all elements in string form from startingIndex
	 */
	public static String MergeEnding(String[] args, int startingIndex)
	{
		String value = "";
		
		for (int i = startingIndex; i < args.length; i++)
		{
			if (i > startingIndex)
				value += " ";
			
			value += args[i];
		}
		
		return value;
	}
}
