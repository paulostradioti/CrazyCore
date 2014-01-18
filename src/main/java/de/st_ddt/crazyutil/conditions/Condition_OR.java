package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class Condition_OR extends ConditionList
{

	public Condition_OR()
	{
		super();
	}

	public Condition_OR(final Condition... conditions)
	{
		super(conditions);
	}

	public Condition_OR(final Collection<Condition> conditions)
	{
		super(conditions);
	}

	public Condition_OR(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
	}

	@Override
	ConditionList newInstance()
	{
		return new Condition_OR();
	}

	@Override
	public boolean check(final Map<Integer, ? extends Object> parameters)
	{
		for (final Condition condition : conditions)
			if (condition.check(parameters))
				return true;
		return false;
	}
}
