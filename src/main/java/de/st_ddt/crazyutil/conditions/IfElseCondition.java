package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class IfElseCondition extends SubConditionedCondition
{

	protected final Condition failCondition;

	public IfElseCondition()
	{
		this(new Condition_TRUE());
	}

	public IfElseCondition(final Condition successCondition)
	{
		this(successCondition, new Condition_FALSE());
	}

	public IfElseCondition(final Condition successCondition, final Condition failCondition)
	{
		super(successCondition);
		this.failCondition = failCondition;
	}

	public IfElseCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
		this.failCondition = BasicCondition.load(config.getConfigurationSection("failCondition"), parameterIndexes);
	}

	@Override
	public abstract Condition secure(Map<Integer, ? extends Collection<Class<?>>> classes);

	public abstract boolean checkCondition(Object... parameter);

	@Override
	public boolean check(final Map<Integer, ? extends Object> parameter)
	{
		if (checkCondition(parameter))
			return condition.check(parameter);
		else
			return failCondition.check(parameter);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{successCondition: " + condition + "; failCondition: " + failCondition + "}";
	}
}
