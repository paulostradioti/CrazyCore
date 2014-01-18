package de.st_ddt.crazyutil.modules.economy;

import org.bukkit.entity.Player;

public class NoEconomySystem implements EconomySystem
{

	public NoEconomySystem()
	{
		super();
	}

	@Override
	public String getName()
	{
		return "NoEconomy";
	}

	@Override
	public double getMoney(final Player player)
	{
		return 0;
	}

	@Override
	public void changeMoney(final Player player, final double money)
	{
	}
}
