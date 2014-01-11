package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class Condition_AND extends ConditionList
{

	public Condition_AND()
	{
		super();
	}

	public Condition_AND(final Condition... conditions)
	{
		super(conditions);
	}

	public Condition_AND(final Collection<Condition> conditions)
	{
		super(conditions);
	}

	public Condition_AND(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
	}

	@Override
	ConditionList newInstance()
	{
		return new Condition_AND();
	}

	@Override
	public boolean check(final Map<Integer, ? extends Object> parameter)
	{
		for (final Condition condition : conditions)
			if (!condition.check(parameter))
				return false;
		return true;
	}
}
