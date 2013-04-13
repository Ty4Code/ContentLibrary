package org.bitbucket.MrTwiggy.ContentLib.Serializors;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import me.main__.util.SerializationConfig.IllegalPropertyValueException;
import me.main__.util.SerializationConfig.Serializor;

public class ItemStackSerializor implements Serializor<ArrayList<ItemStack>, String> 
{
	
    @Override
    public String serialize(ArrayList<ItemStack> from) 
    {
        return from.toString();
    }

    @Override
    public ArrayList<ItemStack> deserialize(String serialized, Class<ArrayList<ItemStack>> wanted) throws IllegalPropertyValueException
    {
        try 
        {
        	
        	return null;
        }
        catch (IllegalArgumentException e) 
        {
            throw new IllegalPropertyValueException(e);
        }
    }
}
