package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class ParameterExtendingCondition extends SubConditionedCondition
{

	public ParameterExtendingCondition()
	{
		super();
	}

	public ParameterExtendingCondition(final Condition condition)
	{
		super(condition);
	}

	public ParameterExtendingCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
	}

	@Override
	protected abstract Map<String, Integer> getParameterIndexes(final Map<String, Integer> parameterIndexes);

	@Override
	public abstract Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes);

	protected abstract Map<Integer, ? extends Object> getParameters(Map<Integer, ? extends Object> parameters);

	@Override
	public boolean check(final Map<Integer, ? extends Object> parameters)
	{
		return condition.check(getParameters(parameters));
	}

	protected abstract Map<Integer, String> getParameterNames(final Map<Integer, String> parameterNames);

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, getParameterNames(parameterNames));
	}
}
