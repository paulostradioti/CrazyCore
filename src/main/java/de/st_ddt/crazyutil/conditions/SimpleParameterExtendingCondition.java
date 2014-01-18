package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

public abstract class SimpleParameterExtendingCondition extends ParameterExtendingCondition
{

	protected final String targetName;
	protected final int targetIndex;
	protected final Class<?> targetClass;

	public SimpleParameterExtendingCondition(final String targetName, final int targetIndex, final Class<?> targetClass)
	{
		super();
		this.targetName = targetName;
		this.targetIndex = targetIndex;
		this.targetClass = targetClass;
	}

	public SimpleParameterExtendingCondition(final Condition condition, final String targetName, final int targetIndex, final Class<?> targetClass)
	{
		super(condition);
		this.targetName = targetName;
		this.targetIndex = targetIndex;
		this.targetClass = targetClass;
	}

	public SimpleParameterExtendingCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes, final Class<?> targetClass) throws Exception
	{
		super(config, parameterIndexes);
		this.targetName = config.getString("target", targetClass.getSimpleName());
		this.targetIndex = parameterIndexes.size();
		this.targetClass = targetClass;
	}

	@Override
	protected Map<String, Integer> getParameterIndexes(final Map<String, Integer> parameterIndexes)
	{
		final Map<String, Integer> res = new HashMap<>(parameterIndexes);
		res.put(targetName, targetIndex);
		return res;
	}

	protected Map<Integer, Collection<Class<?>>> getParameterClasses(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		final Map<Integer, Collection<Class<?>>> res = new HashMap<>(classes);
		final Set<Class<?>> set = new HashSet<>();
		set.add(targetClass);
		res.put(targetIndex, set);
		return res;
	}

	protected abstract Object getValue(final Map<Integer, ? extends Object> parameters);

	@Override
	public abstract Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes);

	@Override
	protected Map<Integer, ? extends Object> getParameters(final Map<Integer, ? extends Object> parameters)
	{
		final Map<Integer, Object> res = new HashMap<>();
		res.put(targetIndex, getValue(parameters));
		return res;
	}

	@Override
	protected Map<Integer, String> getParameterNames(final Map<Integer, String> parameterNames)
	{
		final Map<Integer, String> res = new HashMap<>(parameterNames);
		res.put(targetIndex, targetName);
		return res;
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		config.set("target", targetName);
	}
}
