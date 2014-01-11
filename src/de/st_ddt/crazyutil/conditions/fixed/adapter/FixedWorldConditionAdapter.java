package de.st_ddt.crazyutil.conditions.fixed.adapter;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.conditions.Condition;
import de.st_ddt.crazyutil.conditions.SimpleParameterExtendingCondition;

public class FixedWorldConditionAdapter extends SimpleParameterExtendingCondition
{

	protected final String worldName;

	public FixedWorldConditionAdapter(final Condition condition, final String parameterName, final int index, final World world)
	{
		this(condition, parameterName, index, world.getName());
	}

	public FixedWorldConditionAdapter(final Condition condition, final String parameterName, final int index, final String world)
	{
		super(condition, parameterName, index, World.class);
		this.worldName = world;
	}

	public FixedWorldConditionAdapter(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes, World.class);
		final String worldName = config.getString("world", null);
		if (worldName == null)
			this.worldName = getFirstWorld();
		else
			this.worldName = worldName;
	}

	private String getFirstWorld()
	{
		return Bukkit.getWorlds().get(0).getName();
	}

	@Override
	protected World getValue()
	{
		return Bukkit.getWorld(worldName);
	}

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		return new FixedWorldConditionAdapter(condition.secure(getParameterClasses(classes)), parameterName, index, worldName);
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		config.set("world", worldName);
	}
}
