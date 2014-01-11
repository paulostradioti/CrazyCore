package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

public abstract class SimpleParameterExtendingCondition extends ParameterExtendingCondition
{

	protected final String parameterName;
	protected final int index;
	protected final Class<?> clazz;

	public SimpleParameterExtendingCondition(final String parameterName, final int index, final Class<?> clazz)
	{
		super();
		this.parameterName = parameterName;
		this.index = index;
		this.clazz = clazz;
	}

	public SimpleParameterExtendingCondition(final Condition condition, final String parameterName, final int index, final Class<?> clazz)
	{
		super(condition);
		this.parameterName = parameterName;
		this.index = index;
		this.clazz = clazz;
	}

	public SimpleParameterExtendingCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes, final Class<?> clazz) throws Exception
	{
		super(config, parameterIndexes);
		this.parameterName = config.getString("parameterName", clazz.getSimpleName());
		this.index = parameterIndexes.size();
		this.clazz = clazz;
	}

	@Override
	protected Map<String, Integer> getParameterIndexes(final Map<String, Integer> parameterIndexes)
	{
		final Map<String, Integer> res = new HashMap<>(parameterIndexes);
		res.put(parameterName, index);
		return res;
	}

	protected Map<Integer, Collection<Class<?>>> getParameterClasses(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		final Map<Integer, Collection<Class<?>>> res = new HashMap<>(classes);
		final Set<Class<?>> set = new HashSet<>();
		set.add(clazz);
		res.put(index, set);
		return res;
	}

	protected abstract Object getValue();

	@Override
	public abstract Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes);

	@Override
	protected Map<Integer, ? extends Object> getParameters(final Map<Integer, ? extends Object> parameters)
	{
		final Map<Integer, Object> res = new HashMap<>();
		res.put(index, getValue());
		return res;
	}

	@Override
	protected Map<Integer, String> getParameterNames(final Map<Integer, String> parameterNames)
	{
		final Map<Integer, String> res = new HashMap<>(parameterNames);
		res.put(index, parameterName);
		return res;
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		config.set("parameterName", parameterName);
	}
}
