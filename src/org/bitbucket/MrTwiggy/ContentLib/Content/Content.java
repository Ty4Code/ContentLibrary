package org.bitbucket.MrTwiggy.ContentLib.Content;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.main__.util.SerializationConfig.MissingAnnotationException;
import me.main__.util.SerializationConfig.NoSuchPropertyException;
import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;
import me.main__.util.SerializationConfig.Serializor;
import org.bitbucket.MrTwiggy.ComponentLib.ComponentHolder;
import org.bitbucket.MrTwiggy.ComponentLib.ComponentMessage;
import org.bitbucket.MrTwiggy.ContentLib.Managers.ContentManager.CommandType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Content extends SerializationConfig
{
	
	/* FIELD: name */
	//The name of the content
	@Property
	String name;
	public String getName(){ return this.name; }
	public void setName(String name){ this.name = name; }
	
	/* FIELD: descrption */
	//The description for this content
	@Property
	String description;
	public String getDescription(){ return this.description; }
	public void setDescription(String description){ this.description = description; }
	
	/* FIELD: componentHolder */
	//The component holder for this content.
	//Used to attach/detach components, as well as handling messages
	ComponentHolder componentHolder;
	
	/**
	 * Constructor
	 * @param contentName - The name representing this content
	 * @param contentDescription - The description representing this content
	 */
	public Content(String name, String description)
	{
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Constructor
	 * @param contentValues - The previously saved values for this content
	 */
	public Content(Map<String,Object> contentValues)
	{
		loadValues(contentValues);
	}
	
	/**
	 * Constructor
	 */
	public Content()
	{
		
	}
		
	/**
	 * Load values for this Content from a serialized map with properties
	 * @param values - The values to be loaded
	 */
	public void loadValues(Map<String,Object> values)
	{
		super.loadValues(values);
	}
	
	/**
	 * Set default property values
	 */
	public void setDefaults()
	{
		this.componentHolder = new ComponentHolder();
		this.name = new String();
		this.description = new String();
	}
	
	/**
	 * Public accessor for 'componentHolder'
	 * @return componentHolder
	 */
	public ComponentHolder getComponentHolder()
	{
		return componentHolder;
	}
	
	/**
	 * Handle a global Content message
	 * @param message
	 * @return true, if the message was handled by any component, false otherwise
	 */
	public boolean handleMessage(ComponentMessage message)
	{
		return componentHolder.handleMessage(message);
	}
	
	public boolean removeContentProperty(String property, int removeIndex)
	{
		ArrayList<Field> fields = new ArrayList<Field>();
        
        //Allows for serialization of more complex class hierarchies
        getAllFields(fields, this.getClass());
        
        for (Field field : fields)
        {
        	field.setAccessible(true);
            Property propertyInfo = field.getAnnotation(Property.class);
            String propertyName = getPropertyName(propertyInfo, field);
            
            if (propertyName.equalsIgnoreCase(property) || field.getName().equalsIgnoreCase(property)) 
            {
            	Object object;
            	
				try 
				{
					object = field.get(this);
				} 
				catch (Exception e)
				{
					return false;
				}
				
				if (object != null && object instanceof List)
                {
					try
					{
						((List)object).remove(removeIndex);
	                	field.set(this, object);
	                	return true;
					}
					catch (Exception e)
					{
						return false;
					}
                }
            }
        }
        
        return false;
	}
	
	public boolean addContentProperty(String property, String value, Serializor serializor)
	{
		ArrayList<Field> fields = new ArrayList<Field>();
        
		System.out.println("mdsfklds");
		
        //Allows for serialization of more complex class hierarchies
        getAllFields(fields, this.getClass());
        
        for (Field field : fields)
        {
        	field.setAccessible(true);
            Property propertyInfo = field.getAnnotation(Property.class);
            String propertyName = getPropertyName(propertyInfo, field);
            
            if (propertyName.equalsIgnoreCase(property) || field.getName().equalsIgnoreCase(property)) 
            {
            	Object object;
				try 
				{
					object = field.get(this);
				} 
				catch (Exception e)
				{
					return false;
				}
				
				if (object != null && object instanceof List)
                {
					
                    Object oVal;
                    
                    List fObject = (List)object;
                    try 
                    {
                        oVal = serializor.deserialize(value, propertyInfo.type());
                    } 
                    catch (Exception e)
                    {
                    	return false;
                    }
                    
                	((List)fObject).add(oVal);
                	
                	try 
                	{
						field.set(this, fObject);
					}
                	catch (Exception e) 
					{
						return false;
					}
                	
                	return true;
                }
            }
        }
        
        return false;
	}
	
	public boolean clearContentProperty(String property)
	{
		ArrayList<Field> fields = new ArrayList<Field>();
        
        //Allows for serialization of more complex class hierarchies
        getAllFields(fields, this.getClass());
        
        for (Field field : fields)
        {
        	field.setAccessible(true);
            Property propertyInfo = field.getAnnotation(Property.class);
            String propertyName = getPropertyName(propertyInfo, field);
            
            if (propertyName.equalsIgnoreCase(property) || field.getName().equalsIgnoreCase(property)) 
            {
            	Object object;
            	
				try 
				{
					object = field.get(this);
				} 
				catch (Exception e)
				{
					return false;
				}
				
				if (object != null && object instanceof List)
                {
                	((List)object).clear();
                	
                	return true;
                }
            }
        }
        
        return false;
	}
	
	/**
	 * Get the correct property name
	 * @param propertyInfo
	 * @param field
	 * @return propertyName
	 */
	public String getPropertyName(Property propertyInfo, Field field)
	{
		String propertyName = "";
		
		if (propertyInfo != null)
        {
        	if (!propertyInfo.alias().equals(Property.NO_ALIAS))
        	{
        		propertyName = propertyInfo.alias();
        	}
        	else
        	{
        		propertyName = field.getName();
        	}
        }
		
		return propertyName;
	}
	
    public boolean setContentProperty(String property, Object value, Player player) throws NoSuchPropertyException
    {
    	ArrayList<Field> fields = new ArrayList<Field>();
    	
    	//value = ArrayList<InventoryItemStack>
        
        //Allows for serialization of more complex class hierarchies
        getAllFields(fields, this.getClass());
        
        for (Field field : fields)
        {
        	field.setAccessible(true);
            Property propertyInfo = field.getAnnotation(Property.class);
            String propertyName = getPropertyName(propertyInfo, field);
           
            if (propertyName.equalsIgnoreCase(property) || field.getName().equalsIgnoreCase(property)) 
            {
            	try
            	{
                    if (field.isAnnotationPresent(Property.class)) 
                    {
                        Class<? extends Serializor<?, ?>> serializorClass = (Class<? extends Serializor<?, ?>>) propertyInfo.serializor();
                        Serializor serializor = serializorCache.getInstance(serializorClass, this);
                        Object oVal;
                        
                        ///SPECIAL CONTENT CASES
                        Object raw = field.get(this);
                        if (raw instanceof Location)
                        {
                        	oVal = player.getLocation();
                        }
                        else
                        {
                        	if (value instanceof List)
                            {
                            	ArrayList rawValues = new ArrayList();
                            	
                            	for (Object rawElement : ((List)value))
                            	{
                            		try
                            		{
                            			Object deserializedV = serializor.deserialize(rawElement, getFieldType(field));
                            			rawValues.add(deserializedV);
                            		}
                            		catch (Exception e)
                            		{
                            			rawValues.add(rawElement);
                            		}
                            	}
                            	
                            	oVal = rawValues;
                            }
                            else
                            {
                            	oVal = serializor.deserialize(value, getFieldType(field));
                            }
                        }
                        
                        return validateAndDoChange(field, oVal);
                    }
                    else
                    {
                        throw new MissingAnnotationException("Property");
                    }
                }
            	catch (MissingAnnotationException e)
            	{
                    throw new NoSuchPropertyException(e);
                } 
            	catch (NoSuchFieldException e)
            	{
                    throw new NoSuchPropertyException(e);
                }
            	catch (Exception e)
            	{
                    return false;
                } 
            	finally
            	{
                    if (field != null)
                    {
                        field.setAccessible(false);
                    }
            	}
            }
        }
        
        return false;
    }
	
	/**
	 * Get whether the player has permission to access this Content using Command of type commandType
	 * @param player - The player attempting to perform the command
	 * @param commandType - The type of Command being performed
	 * @return true, if player has permission, false otherwise
	 */
	public abstract boolean hasPermission(Player player, CommandType commandType);
}
