package de.st_ddt.crazyutil.conditions;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class Condition_FALSE extends BasicCondition
{

	public Condition_FALSE()
	{
		super();
	}

	public Condition_FALSE(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes);
	}

	@Override
	public boolean check(final Map<Integer, ? extends Object> parameter)
	{
		return false;
	}
}
