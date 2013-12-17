package de.st_ddt.crazyutil.modules.permissiongroups;

import java.util.Set;

import org.bukkit.entity.Player;

class NoPermissionSystem implements PermissionSystem
{

	public NoPermissionSystem()
	{
		super();
	}

	@Override
	public String getName()
	{
		return "Bukkit";
	}

	@Override
	public boolean hasGroup(final Player player, final String name)
	{
		return player.hasPermission("group." + name);
	}

	@Override
	public String getGroup(final Player player)
	{
		return null;
	}

	@Override
	public String getGroupPrefix(final Player player)
	{
		return null;
	}

	@Override
	public String getGroupSuffix(final Player player)
	{
		return null;
	}

	@Override
	public Set<String> getGroups(final Player player)
	{
		return null;
	}
}
