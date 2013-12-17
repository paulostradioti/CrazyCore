package de.st_ddt.crazyutil.conditions.player;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.conditions.checker.PlayerConditionChecker;
import de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule;

public class Condition_Player_Group extends BasicPlayerCondition
{

	private final String group;

	public Condition_Player_Group()
	{
		super();
		this.group = null;
	}

	public Condition_Player_Group(final String group)
	{
		super();
		this.group = group;
	}

	public Condition_Player_Group(final ConfigurationSection config)
	{
		super(config);
		final String group = config.getString("group", "<Group>");
		if (group.equalsIgnoreCase("<Group>"))
			this.group = null;
		else
			this.group = group;
	}

	@Override
	public String getType()
	{
		return "PLAYER_GROUP";
	}

	@Override
	public boolean check(final PlayerConditionChecker checker)
	{
		if (group == null)
			return true;
		else
			return PermissionModule.hasGroup(checker.getEntity(), group);
	}

	@Override
	public void save(final ConfigurationSection config, final String path)
	{
		super.save(config, path);
		if (group == null)
			config.set(path + "group", "<Group>");
		else
			config.set(path + "group", group);
	}
}
