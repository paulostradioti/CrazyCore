package de.st_ddt.crazyutil.conditions.player;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.st_ddt.crazyutil.conditions.SimpleCondition;

public abstract class SimplePlayerCondition extends SimpleCondition
{

	public SimplePlayerCondition(final int index)
	{
		super(index, Player.class);
	}

	public SimplePlayerCondition(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes, Player.class);
	}

	protected abstract boolean check(Player parameter);

	@Override
	protected final boolean check(final Object parameter)
	{
		return check((Player) parameter);
	}
}
