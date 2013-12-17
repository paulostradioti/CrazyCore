package de.st_ddt.crazyutil.modules.permissiongroups;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;

class bPermissionsPermissionSystem implements PermissionSystem
{

	public bPermissionsPermissionSystem()
	{
		super();
		final Plugin plugin = Bukkit.getPluginManager().getPlugin("bPermissions");
		if (plugin == null)
			throw new IllegalArgumentException("bPermissions plugin cannot be null!");
		ApiLayer.class.getName();
	}

	@Override
	public String getName()
	{
		return "bPermissions";
	}

	@Override
	public boolean hasGroup(final Player player, final String name)
	{
		return ApiLayer.hasGroupRecursive(player.getWorld().getName(), CalculableType.USER, player.getName(), name);
	}

	@Override
	public String getGroup(final Player player)
	{
		final String[] groups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
		if (groups.length == 0)
			return null;
		else
			return groups[0];
	}

	@Override
	public String getGroupPrefix(final Player player)
	{
		return ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), "prefix");
	}

	@Override
	public String getGroupSuffix(final Player player)
	{
		return ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), "suffix");
	}

	@Override
	public Set<String> getGroups(final Player player)
	{
		final Set<String> groups = new LinkedHashSet<String>();
		for (final String group : ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName()))
			groups.add(group);
		return groups;
	}
}
