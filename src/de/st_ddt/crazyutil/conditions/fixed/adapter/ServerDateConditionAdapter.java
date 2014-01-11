package de.st_ddt.crazyutil.conditions.fixed.adapter;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.conditions.Condition;
import de.st_ddt.crazyutil.conditions.SimpleParameterExtendingCondition;

public class ServerDateConditionAdapter extends SimpleParameterExtendingCondition
{

	public ServerDateConditionAdapter(final String parameterName, final int index)
	{
		super(parameterName, index, Date.class);
	}

	public ServerDateConditionAdapter(final Condition condition, final String parameterName, final int index)
	{
		super(condition, parameterName, index, Date.class);
	}

	public ServerDateConditionAdapter(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes, Date.class);
	}

	@Override
	protected Object getValue()
	{
		return new Date();
	}

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		return new ServerDateConditionAdapter(condition.secure(getParameterClasses(classes)), parameterName, index);
	}
}
