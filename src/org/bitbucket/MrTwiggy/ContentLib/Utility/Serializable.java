package org.bitbucket.MrTwiggy.ContentLib.Utility;

public abstract class Serializable 
{

	public static final String PARSE_START = "[";
	public static final String PARSE_SEPERATOR = ":";
	public static final String PARSE_END = "]";
	
	/**
     * Serialize a value from it's identifier
     * @param identifier
     * @param value
     * @return
     */
	public static String serializeValue(String identifier, Object value)
	{
		return PARSE_START + identifier + PARSE_SEPERATOR
						+ value.toString() + PARSE_END + " ";
	}
	
	/**
	 * Deserialize a value from it's identifier
	 * @param serialized
	 * @param identifier
	 * @return
	 */
	public static String deserializeValue(String serialized, String identifier)
	{
		String modified = "" + serialized;
		
		boolean haveValue = false;
		String value = "";
		
		//[keyframe:[x:0] [y:0] [z:0] [yaw:0]]
		//{keyframe
		while (!haveValue)
		{
			if (modified.contains(PARSE_START) && modified.contains(PARSE_END))
			{
				int start = modified.indexOf(PARSE_START);
				int end = modified.indexOf(PARSE_END) + 1;
				
				String[] values = modified.substring(start, end).replace(PARSE_START, "")
						.replace(PARSE_END, "").split(PARSE_SEPERATOR);
				
				if (values.length == 2 && values[0].equalsIgnoreCase(identifier))
				{
					value = values[1];
					haveValue = true;
				}
				else
				{
					modified = modified.substring(end);
				}
			}
		}
		
		return value;
	}
}
