package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class SimpleParameterConverter extends SimpleParameterExtendingCondition
{

	protected final int sourceIndex;
	protected final Class<?> sourceClass;

	public SimpleParameterConverter(final String parameterName, final int targetIndex, final Class<?> targetClass, final int sourceIndex, final Class<?> sourceClass)
	{
		super(parameterName, targetIndex, targetClass);
		this.sourceIndex = sourceIndex;
		this.sourceClass = sourceClass;
	}

	public SimpleParameterConverter(final Condition condition, final String parameterName, final int targetIndex, final Class<?> targetClass, final int sourceIndex, final Class<?> sourceClass)
	{
		super(condition, parameterName, targetIndex, targetClass);
		this.sourceIndex = sourceIndex;
		this.sourceClass = sourceClass;
	}

	public SimpleParameterConverter(final ConfigurationSection config, final Map<String, Integer> parameterIndexes, final Class<?> targetClass, final Class<?> sourceClass) throws Exception
	{
		super(config, parameterIndexes, targetClass);
		this.sourceIndex = parameterIndexes.get(config.getString("source", sourceClass.getSimpleName()));
		this.sourceClass = sourceClass;
	}

	@Override
	public final Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		final Collection<Class<?>> givenClasses = classes.get(sourceIndex);
		for (final Class<?> givenClass : givenClasses)
			if (sourceClass.isAssignableFrom(givenClass))
				return this;
		return new Condition_Class(sourceIndex, sourceClass, this);
	}

	protected abstract Object getValue(Object parameter);

	@Override
	protected Object getValue(final Map<Integer, ? extends Object> parameters)
	{
		return getValue(parameters.get(sourceIndex));
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		config.set(path + "source", parameterNames.get(sourceIndex));
	}
}
