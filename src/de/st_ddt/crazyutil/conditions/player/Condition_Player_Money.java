package de.st_ddt.crazyutil.conditions.player;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.conditions.checker.PlayerConditionChecker;
import de.st_ddt.crazyutil.modules.economy.EconomyModule;

public class Condition_Player_Money extends BasicPlayerCondition
{

	private final double money;

	public Condition_Player_Money()
	{
		super();
		this.money = 0;
	}

	public Condition_Player_Money(final double money)
	{
		super();
		this.money = 0;
	}

	public Condition_Player_Money(final ConfigurationSection config)
	{
		super(config);
		this.money = config.getDouble("money", 0);
	}

	@Override
	public String getType()
	{
		return "PLAYER_MONEY";
	}

	@Override
	public boolean check(final PlayerConditionChecker checker)
	{
		return EconomyModule.getMoney(checker.getEntity()) >= money;
	}

	@Override
	public void save(final ConfigurationSection config, final String path)
	{
		super.save(config, path);
		config.set(path + "money", money);
	}
}
