package de.st_ddt.crazyutil.modules.permissions;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Deprecated
public class PermissionModule
{

	@Deprecated
	public static boolean hasPermission(final CommandSender sender, final String permission)
	{
		return sender.hasPermission(permission);
	}

	@Deprecated
	public static boolean hasGroup(final Player player, final String group)
	{
		return de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule.hasGroup(player, group);
	}

	@Deprecated
	public static String getGroup(final Player player)
	{
		return de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule.getGroup(player);
	}

	@Deprecated
	public static String getGroupPrefix(final Player player)
	{
		return de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule.getGroupPrefix(player);
	}

	@Deprecated
	public static String getGroupSuffix(final Player player)
	{
		return de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule.getGroupSuffix(player);
	}

	@Deprecated
	public static Set<String> getGroups(final Player player)
	{
		return de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule.getGroups(player);
	}
}
