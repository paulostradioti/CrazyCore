package de.st_ddt.crazyutil.conditions;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public final class Condition_TRUE extends BasicCondition
{

	public Condition_TRUE()
	{
		super();
	}

	public Condition_TRUE(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes);
	}

	@Override
	public boolean check(final Map<Integer, ? extends Object> parameter)
	{
		return true;
	}
}
