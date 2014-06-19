package de.st_ddt.crazyutil.modules.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomySystem implements EconomySystem
{

	private net.milkbowl.vault.economy.Economy economy;

	public VaultEconomySystem()
	{
		super();
		try
		{
			final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null)
				economy = economyProvider.getProvider();
			else
				economy = null;
			if (economy != null)
				return;
		}
		catch (final Exception e)
		{}
		throw new IllegalArgumentException("VaultEconomy not enabled!");
	}

	@Override
	public String getName()
	{
		return "Vault";
	}

	@Override
	public double getMoney(final Player player)
	{
		return economy.getBalance(player);
	}

	@Override
	public void changeMoney(final Player player, final double money)
	{
		if (money > 0)
			economy.bankDeposit(player.getName(), money);
		else if (money < 0)
			economy.bankWithdraw(player.getName(), -money);
	}
}
