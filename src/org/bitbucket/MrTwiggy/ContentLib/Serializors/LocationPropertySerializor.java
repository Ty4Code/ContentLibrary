package org.bitbucket.MrTwiggy.ContentLib.Serializors;

import org.bitbucket.MrTwiggy.ContentLib.Utility.LocationSerializable;
import org.bukkit.Location;
import me.main__.util.SerializationConfig.IllegalPropertyValueException;
import me.main__.util.SerializationConfig.Serializor;



public class LocationPropertySerializor implements Serializor<Location, String> 
{
	public final String PARSE_START = "[";
	public final String PARSE_SEPERATOR = ":";
	public final String PARSE_END = "]";
	
	//Example serialized: [world:nether] [x:204] [y:192.4] [z:-12523] [yaw:34]
	
	@Override
    public String serialize(Location from) 
    {
		return LocationSerializable.serialize(from);
    }

    @Override
    public Location deserialize(String serialized, Class<Location> wanted) throws IllegalPropertyValueException
    {
    	try
    	{
    		return LocationSerializable.deserialize(serialized);
    	}
    	catch(NumberFormatException e)
    	{
    		throw new IllegalPropertyValueException(e);
    	}
    }

}
