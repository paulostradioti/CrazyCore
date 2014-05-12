package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class BasicCondition implements Condition
{

	public BasicCondition()
	{
		super();
	}

	public BasicCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super();
	}

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		return this;
	}

	@Override
	public abstract boolean check(Map<Integer, ? extends Object> parameters);

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		config.set(path + "type", getClass().getName());
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
