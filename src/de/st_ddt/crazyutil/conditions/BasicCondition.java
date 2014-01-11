package de.st_ddt.crazyutil.conditions;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class BasicCondition implements Condition
{

	public static Condition load(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		if (config == null)
			throw new IllegalArgumentException("ConfigurationSection cannot be NULL!");
		final String type = config.getString("type");
		if (type == null)
			throw new IllegalArgumentException("ConditionType cannot be NULL!");
		Class<?> clazz;
		try
		{
			clazz = Class.forName(type);
		}
		catch (final ClassNotFoundException e1)
		{
			throw new IllegalArgumentException("The Condition's Class " + type + " was not found! Please fix your configuration!");
		}
		if (!Condition.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException("The Condition's Class " + clazz.getSimpleName() + " is no supported Condition! Please fix your configuration!");
		try
		{
			final Constructor<? extends Condition> constructor = clazz.asSubclass(Condition.class).getConstructor(ConfigurationSection.class, Map.class);
			return constructor.newInstance(config, parameterIndexes);
		}
		catch (final Exception e)
		{
			System.err.println("WARNING: Serious bug detected, please report this issue!");
			throw e;
		}
	}

	public BasicCondition()
	{
	}

	public BasicCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
	}

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		return this;
	}

	@Override
	public abstract boolean check(Map<Integer, ? extends Object> parameters);

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		config.set("type", getClass().getName());
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
