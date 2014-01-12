package de.st_ddt.crazyutil.conditions.entity.converter;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import de.st_ddt.crazyutil.conditions.Condition;
import de.st_ddt.crazyutil.conditions.SimpleParameterConverter;

public class EntityToLocationConverter extends SimpleParameterConverter
{

	public EntityToLocationConverter(final String parameterName, final int targetIndex, final int sourceIndex)
	{
		super(parameterName, targetIndex, Location.class, sourceIndex, Entity.class);
	}

	public EntityToLocationConverter(final Condition condition, final String parameterName, final int targetIndex, final int sourceIndex)
	{
		super(condition, parameterName, targetIndex, Location.class, sourceIndex, Entity.class);
	}

	public EntityToLocationConverter(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes, Location.class, Entity.class);
	}

	@Override
	protected Object getValue(final Object parameter)
	{
		return ((Entity) parameter).getLocation();
	}
}
