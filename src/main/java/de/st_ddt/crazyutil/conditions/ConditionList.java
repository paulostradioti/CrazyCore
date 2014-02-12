package de.st_ddt.crazyutil.conditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class ConditionList extends BasicCondition
{

	protected final List<Condition> conditions = new ArrayList<Condition>();

	public ConditionList()
	{
		super();
	}

	public ConditionList(final Condition... conditions)
	{
		super();
		for (final Condition condition : conditions)
			this.conditions.add(condition);
	}

	public ConditionList(final Collection<Condition> conditions)
	{
		super();
		this.conditions.addAll(conditions);
	}

	public ConditionList(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes);
		final ConfigurationSection entryConfig = config.getConfigurationSection("conditions");
		for (final String key : entryConfig.getKeys(false))
			conditions.add(ConditionHelper.load(entryConfig.getConfigurationSection(key), parameterIndexes));
	}

	abstract ConditionList newInstance();

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		final ConditionList newInstance = newInstance();
		for (final Condition condition : conditions)
			newInstance.conditions.add(condition.secure(classes));
		return newInstance;
	}

	@Override
	public abstract boolean check(Map<Integer, ? extends Object> parameter);

	@Override
	public final void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		int a = 0;
		config.set(path + "conditions", null);
		for (final Condition condition : conditions)
			condition.save(config, path + "conditions." + condition.getClass().getSimpleName().replace("Condition_", "") + (a++) + ".", parameterNames);
	}

	public final List<Condition> getConditions()
	{
		return conditions;
	}
}
