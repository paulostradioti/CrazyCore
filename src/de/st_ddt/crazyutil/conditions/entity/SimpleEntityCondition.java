package de.st_ddt.crazyutil.conditions.entity;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import de.st_ddt.crazyutil.conditions.SimpleCondition;

public abstract class SimpleEntityCondition extends SimpleCondition
{

	public SimpleEntityCondition(final int index)
	{
		super(index, Entity.class);
	}

	public SimpleEntityCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes, Entity.class);
	}

	protected abstract boolean check(Entity parameter);

	@Override
	protected final boolean check(final Object parameter)
	{
		return check((Entity) parameter);
	}
}
