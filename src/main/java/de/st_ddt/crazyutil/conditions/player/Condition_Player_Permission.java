package de.st_ddt.crazyutil.conditions.player;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Condition_Player_Permission extends SimplePlayerCondition
{

	private final String permission;

	public Condition_Player_Permission(final int index)
	{
		super(index);
		this.permission = null;
	}

	public Condition_Player_Permission(final int index, final String permission)
	{
		super(index);
		this.permission = permission;
	}

	public Condition_Player_Permission(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes);
		final String permission = config.getString("permission", "<Permission>");
		if (permission.equalsIgnoreCase("<Permission>"))
			this.permission = null;
		else
			this.permission = permission;
	}

	@Override
	public boolean check(final Player parameter)
	{
		if (permission == null)
			return true;
		else
			return parameter.hasPermission(permission);
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		if (permission == null)
			config.set(path + "permission", "<Permission>");
		else
			config.set(path + "permission", permission);
	}
}
