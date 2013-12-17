package de.st_ddt.crazyutil.conditions.fixed.adapter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.conditions.Condition;
import de.st_ddt.crazyutil.conditions.SubConditionedCondition;
import de.st_ddt.crazyutil.conditions.checker.ConditionChecker;
import de.st_ddt.crazyutil.conditions.checker.WorldConditionChecker;
import de.st_ddt.crazyutil.conditions.checker.WorldConditionChecker.SimpleWorldConditionChecker;

public class FixedWorldConditionAdapter extends SubConditionedCondition
{

	protected final World world;

	public FixedWorldConditionAdapter()
	{
		super();
		this.world = getFirstWorld();
	}

	public FixedWorldConditionAdapter(final Condition condition, final World world)
	{
		super(condition);
		this.world = world;
	}

	public FixedWorldConditionAdapter(final ConfigurationSection config) throws Exception
	{
		super(config);
		final String worldName = config.getString("world", null);
		if (worldName == null)
			this.world = getFirstWorld();
		else
		{
			final World world = Bukkit.getWorld(worldName);
			if (world == null)
				this.world = getFirstWorld();
			else
				this.world = world;
		}
	}

	private World getFirstWorld()
	{
		return Bukkit.getWorlds().get(0);
	}

	@Override
	public String getType()
	{
		return "FixedWorldConditionAdapter";
	}

	@Override
	public boolean isApplicable(final Class<? extends ConditionChecker> clazz)
	{
		return condition.isApplicable(WorldConditionChecker.class);
	}

	@Override
	public boolean check(final ConditionChecker checker)
	{
		return condition.check(new SimpleWorldConditionChecker(world));
	}
}
