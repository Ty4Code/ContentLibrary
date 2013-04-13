package org.bitbucket.MrTwiggy.ContentLib.Utility;

import me.main__.util.SerializationConfig.IllegalPropertyValueException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializable extends Serializable
{
	/**
	 * Serialize the Location to string
	 * @param from
	 * @return serialized string
	 */
    public static String serialize(Location from) 
    {
		String serialized = "";
	
		serialized += serializeValue("world", from.getWorld().getName())
		+ serializeValue("x", from.getX())
		+ serializeValue("y", from.getY())
		+ serializeValue("z", from.getZ())
		+ serializeValue("yaw", from.getYaw());
		
        return serialized;
    }
	
	/**
	 * Deserialize a location from it's string
	 * @param serialized
	 * @return
	 * @throws IllegalPropertyValueException
	 */
    public static Location deserialize(String serialized) throws IllegalPropertyValueException
    {
    	try
    	{
    		double x = Double.parseDouble(deserializeValue(serialized, "x"));
        	double y = Double.parseDouble(deserializeValue(serialized, "y"));
        	double z = Double.parseDouble(deserializeValue(serialized, "z"));
        	float yaw = Float.parseFloat(deserializeValue(serialized, "yaw"));
        	String worldName = deserializeValue(serialized, "world");
        	World world = Bukkit.getWorld(worldName);
        	
        	if (world == null)
        	{
        		throw new IllegalPropertyValueException();
        	}
        	
        	return new Location(world, x, y, z, yaw, (float)0);
    	}
    	catch(NumberFormatException e)
    	{
    		throw new IllegalPropertyValueException(e);
    	}
    }
}
