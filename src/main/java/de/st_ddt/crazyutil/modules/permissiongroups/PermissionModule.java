package de.st_ddt.crazyutil.modules.permissiongroups;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.st_ddt.crazycore.CrazyCore;

public final class PermissionModule
{

	private final static List<Class<? extends PermissionSystem>> PERMISSIONSYSTEMCLASSES = PermissionSystem.PERMISSIONSYSTEMCLASSES;
	private static PermissionSystem INSTANCE;
	static
	{
		registerPermissionSystem(VaultPermissionSystem.class);
		registerPermissionSystem(GroupManagerPermissionSystem.class);
		registerPermissionSystem(PermissionsExPermissionSystem.class);
		registerPermissionSystem(bPermissionsPermissionSystem.class);
		registerPermissionSystem(PermissionsBukkitPermissionSystem.class);
		registerPermissionSystem(NoPermissionSystem.class);
	}

	public static void registerPermissionSystem(final Class<? extends PermissionSystem> clazz)
	{
		PERMISSIONSYSTEMCLASSES.add(clazz);
	}

	public static void registerFallbackPermissionSystem(final Class<? extends PermissionSystem> clazz)
	{
		PERMISSIONSYSTEMCLASSES.add(PERMISSIONSYSTEMCLASSES.size() - 1, clazz);
	}

	public static void initialize()
	{
		if (INSTANCE != null)
			return;
		for (final Class<? extends PermissionSystem> clazz : PermissionSystem.PERMISSIONSYSTEMCLASSES)
			try
			{
				INSTANCE = clazz.newInstance();
				break;
			}
			catch (final Throwable e)
			{}
		Bukkit.getConsoleSender().sendMessage(CrazyCore.getPlugin().getChatHeader() + ChatColor.GREEN + "Activated " + INSTANCE.getName() + "-PermissionSystem-Module!");
	}

	public static void clear()
	{
		INSTANCE = null;
	}

	public static boolean hasGroup(final Player player, final String group)
	{
		if (INSTANCE == null)
			initialize();
		return INSTANCE.hasGroup(player, group);
	}

	public static String getGroup(final Player player)
	{
		if (INSTANCE == null)
			initialize();
		return INSTANCE.getGroup(player);
	}

	public static Set<String> getGroups(final Player player)
	{
		if (INSTANCE == null)
			initialize();
		return INSTANCE.getGroups(player);
	}

	public static String getGroupPrefix(final Player player)
	{
		if (INSTANCE == null)
			initialize();
		return INSTANCE.getGroupPrefix(player);
	}

	public static String getGroupSuffix(final Player player)
	{
		if (INSTANCE == null)
			initialize();
		return INSTANCE.getGroupSuffix(player);
	}

	private PermissionModule()
	{
		super();
	}
}
