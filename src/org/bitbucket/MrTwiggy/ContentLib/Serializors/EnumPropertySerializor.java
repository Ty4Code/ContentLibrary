package org.bitbucket.MrTwiggy.ContentLib.Serializors;

import me.main__.util.SerializationConfig.IllegalPropertyValueException;
import me.main__.util.SerializationConfig.Serializor;

/**
 * Serializor for the color-property. Author(s): Multiverse-Core Github
 */
public class EnumPropertySerializor<T extends Enum<T>> implements Serializor<T, String> 
{
    @Override
    public String serialize(T from) 
    {
        return from.toString();
    }

    @Override
    public T deserialize(String serialized, Class<T> wanted) throws IllegalPropertyValueException
    {
        try 
        {
            return Enum.valueOf(wanted, serialized.toUpperCase());
        } catch (IllegalArgumentException e) 
        {
            throw new IllegalPropertyValueException(e);
        }
    }
}