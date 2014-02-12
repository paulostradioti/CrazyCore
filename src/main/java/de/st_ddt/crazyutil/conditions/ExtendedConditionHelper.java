package de.st_ddt.crazyutil.conditions;

import java.util.Collection;
import java.util.Map;

public class ExtendedConditionHelper extends ConditionHelper
{

	public static Condition secure(final Condition condition, final int index, final Class<?> clazz, final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		final Collection<Class<?>> givenClasses = classes.get(index);
		for (final Class<?> givenClass : givenClasses)
			if (clazz.isAssignableFrom(givenClass))
				return condition;
		return new Condition_Class(index, clazz, condition);
	}
}
