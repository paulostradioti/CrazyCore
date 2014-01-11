package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class SimpleCondition extends BasicCondition
{

	protected final int index;
	protected final Class<?> clazz;

	public SimpleCondition(final int index, final Class<?> clazz)
	{
		super();
		this.index = index;
		this.clazz = clazz;
	}

	public SimpleCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes, final Class<?> clazz)
	{
		super(config, parameterIndexes);
		this.index = parameterIndexes.get(config.getString("parameterName", "default"));
		this.clazz = clazz;
	}

	@Override
	public final Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		final Collection<Class<?>> givenClasses = classes.get(index);
		for (final Class<?> givenClass : givenClasses)
			if (clazz.isAssignableFrom(givenClass))
				return this;
		return new Condition_Class(index, clazz, this);
	}

	protected abstract boolean check(Object parameter);

	@Override
	public final boolean check(final Map<Integer, ? extends Object> parameters)
	{
		return check(parameters.get(index));
	}
}
