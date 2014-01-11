package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

public class Condition_Class extends IfElseCondition
{

	protected final int index;
	protected final Class<?> clazz;

	public Condition_Class(final int index, final Class<?> clazz)
	{
		this(index, clazz, new Condition_TRUE());
	}

	public Condition_Class(final int index, final Class<?> clazz, final Condition successCondition)
	{
		this(index, clazz, successCondition, new Condition_FALSE());
	}

	public Condition_Class(final int index, final Class<?> clazz, final Condition successCondition, final Condition failCondition)
	{
		super(successCondition, failCondition);
		this.index = index;
		this.clazz = clazz;
	}

	public Condition_Class(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
		final Integer index = parameterIndexes.get(config.get("paramName", "null"));
		if (index == null)
			this.index = 0;
		else
			this.index = index;
		final String clazz = config.getString("class", getClass().getName());
		try
		{
			this.clazz = Class.forName(clazz);
		}
		catch (final ClassNotFoundException e)
		{
			throw new IllegalArgumentException("The Class " + clazz + " was not found! Please fix your configuration!");
		}
	}

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		final Map<Integer, Set<Class<?>>> newClasses = new HashMap<>();
		newClasses.putAll(newClasses);
		return new Condition_Class(index, clazz, condition.secure(newClasses), failCondition);
	}

	@Override
	public boolean checkCondition(final Object... parameter)
	{
		return clazz.isInstance(parameter[index]);
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		config.set(path + "paramName", parameterNames.get(index));
		config.set(path + "class", clazz.getName());
	}
}
