package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class SubConditionedCondition extends BasicCondition
{

	protected final Condition condition;

	public SubConditionedCondition()
	{
		this(new Condition_TRUE());
	}

	public SubConditionedCondition(final Condition condition)
	{
		super();
		this.condition = condition;
	}

	public SubConditionedCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
		this.condition = BasicCondition.load(config.getConfigurationSection("condition"), getParameterIndexes(parameterIndexes));
	}

	protected Map<String, Integer> getParameterIndexes(final Map<String, Integer> parameterIndexes)
	{
		return parameterIndexes;
	}

	@Override
	public abstract Condition secure(Map<Integer, ? extends Collection<Class<?>>> classes);

	@Override
	public abstract boolean check(final Map<Integer, ? extends Object> parameters);

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		condition.save(config, path + "condition", parameterNames);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{condition: " + condition + "}";
	}
}
