package de.st_ddt.crazyutil.conditions.fixed.adapter;

import java.util.Date;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.conditions.Condition;
import de.st_ddt.crazyutil.conditions.SubConditionedCondition;
import de.st_ddt.crazyutil.conditions.checker.ConditionChecker;
import de.st_ddt.crazyutil.conditions.checker.DateConditionChecker;
import de.st_ddt.crazyutil.conditions.checker.DateConditionChecker.SimpleDateConditionChecker;

public class ServerDateConditionAdapter extends SubConditionedCondition
{

	public ServerDateConditionAdapter()
	{
		super();
	}

	public ServerDateConditionAdapter(final Condition condition)
	{
		super(condition);
	}

	public ServerDateConditionAdapter(final ConfigurationSection config) throws Exception
	{
		super(config);
	}

	@Override
	public String getType()
	{
		return "SERVERDATEADAPTER";
	}

	@Override
	public boolean isApplicable(final Class<? extends ConditionChecker> clazz)
	{
		return condition.isApplicable(DateConditionChecker.class);
	}

	@Override
	public boolean check(final ConditionChecker checker)
	{
		return condition.check(new SimpleDateConditionChecker(new Date()));
	}
}
