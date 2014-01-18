package de.st_ddt.crazyutil.modules.economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.st_ddt.crazycore.CrazyCore;

public final class EconomyModule
{

	protected static EconomySystem INSTANCE;
	static
	{
		EconomySystem.ECONOMYSYSTEMCLASSES.add(VaultEconomySystem.class);
		EconomySystem.ECONOMYSYSTEMCLASSES.add(NoEconomySystem.class);
	}

	public static void initialize()
	{
		for (final Class<? extends EconomySystem> clazz : EconomySystem.ECONOMYSYSTEMCLASSES)
			try
			{
				INSTANCE = clazz.newInstance();
				break;
			}
			catch (final Throwable e)
			{}
		Bukkit.getConsoleSender().sendMessage(CrazyCore.getPlugin().getChatHeader() + ChatColor.GREEN + "Activated " + INSTANCE.getName() + "-EconomySystem-Module!");
	}

	public static String getName()
	{
		if (INSTANCE == null)
			initialize();
		return INSTANCE.getName();
	}

	public static double getMoney(final Player player)
	{
		if (INSTANCE == null)
			initialize();
		if (player == null)
			return 0;
		else
			return INSTANCE.getMoney(player);
	}

	public static void changeMoney(final Player player, final double money)
	{
		if (INSTANCE == null)
			initialize();
		if (player != null)
			INSTANCE.changeMoney(player, money);
	}

	private EconomyModule()
	{
	}
}
