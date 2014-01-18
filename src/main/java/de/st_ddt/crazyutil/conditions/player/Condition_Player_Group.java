package de.st_ddt.crazyutil.conditions.player;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule;

public class Condition_Player_Group extends SimplePlayerCondition
{

	private final String group;

	public Condition_Player_Group(final int index)
	{
		super(index);
		this.group = null;
	}

	public Condition_Player_Group(final int index, final String group)
	{
		super(index);
		this.group = group;
	}

	public Condition_Player_Group(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes);
		final String group = config.getString("group", "<Group>");
		if (group.equalsIgnoreCase("<Group>"))
			this.group = null;
		else
			this.group = group;
	}

	@Override
	protected boolean check(final Player parameter)
	{
		if (group == null)
			return true;
		else
			return PermissionModule.hasGroup(parameter, group);
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		if (group == null)
			config.set(path + "group", "<Group>");
		else
			config.set(path + "group", group);
	}
}
