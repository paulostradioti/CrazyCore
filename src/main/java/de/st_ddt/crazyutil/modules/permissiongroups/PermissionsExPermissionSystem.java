package de.st_ddt.crazyutil.modules.permissiongroups;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

class PermissionsExPermissionSystem implements PermissionSystem
{

	private final PermissionManager plugin;

	public PermissionsExPermissionSystem()
	{
		super();
		plugin = PermissionsEx.getPermissionManager();
		if (plugin == null)
			throw new IllegalArgumentException("PermissionsEx plugin cannot be null!");
	}

	@Override
	public String getName()
	{
		return "PermissionsEx";
	}

	@Override
	public boolean hasGroup(final Player player, final String name)
	{
		final PermissionUser user = getUser(player);
		if (user == null)
			return false;
		for (final PermissionGroup group : user.getParents())
			if (name.equals(group.getName()))
				return true;
		for (final PermissionGroup group : user.getParents(player.getWorld().getName()))
			if (name.equals(group.getName()))
				return true;
		return false;
	}

	@Override
	public String getGroup(final Player player)
	{
		final PermissionUser user = getUser(player);
		if (user == null)
			return null;
		else
		{
			final List<PermissionGroup> globalGroups = user.getParents();
			if (globalGroups.size() == 0)
			{
				final List<PermissionGroup> worldGroups = user.getParents(player.getWorld().getName());
				if (worldGroups.size() == 0)
					return null;
				else
					return worldGroups.get(0).getName();
			}
			else
				return globalGroups.get(0).getName();
		}
	}

	@Override
	public String getGroupPrefix(final Player player)
	{
		final PermissionUser user = getUser(player);
		if (user == null)
			return null;
		final String prefix = user.getPrefix();
		if (prefix == null)
			return user.getPrefix(player.getWorld().getName());
		else
			return prefix;
	}

	@Override
	public String getGroupSuffix(final Player player)
	{
		final PermissionUser user = getUser(player);
		if (user == null)
			return null;
		final String suffix = user.getSuffix();
		if (suffix == null)
			return user.getSuffix(player.getWorld().getName());
		else
			return suffix;
	}

	@Override
	public Set<String> getGroups(final Player player)
	{
		final PermissionUser user = getUser(player);
		if (user == null)
			return null;
		final Set<String> groups = new LinkedHashSet<String>();
		for (final PermissionGroup group : user.getParents())
			groups.add(group.getName());
		for (final PermissionGroup group : user.getParents(player.getWorld().getName()))
			groups.add(group.getName());
		return groups;
	}

	private PermissionUser getUser(final Player player)
	{
		return plugin.getUser(player);
	}
}
