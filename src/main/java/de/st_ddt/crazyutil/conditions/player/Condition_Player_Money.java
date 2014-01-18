package de.st_ddt.crazyutil.conditions.player;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.st_ddt.crazyutil.modules.economy.EconomyModule;

public class Condition_Player_Money extends SimplePlayerCondition
{

	private final double money;

	public Condition_Player_Money(final int index)
	{
		super(index);
		this.money = 0;
	}

	public Condition_Player_Money(final int index, final double money)
	{
		super(index);
		this.money = 0;
	}

	public Condition_Player_Money(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes);
		this.money = config.getDouble("money", 0);
	}

	@Override
	protected boolean check(final Player parameter)
	{
		return EconomyModule.getMoney(parameter) >= money;
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		config.set(path + "money", money);
	}
}
