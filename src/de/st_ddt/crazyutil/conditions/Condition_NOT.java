package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class Condition_NOT extends SubConditionedCondition
{

	public Condition_NOT()
	{
		super();
	}

	public Condition_NOT(final Condition condition)
	{
		super(condition);
	}

	public Condition_NOT(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
	}

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		return new Condition_NOT(condition.secure(classes));
	}

	@Override
	public boolean check(final Map<Integer, ? extends Object> parameters)
	{
		return !condition.check(parameters);
	}
}
