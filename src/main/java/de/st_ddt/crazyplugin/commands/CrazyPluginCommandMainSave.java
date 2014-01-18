package de.st_ddt.crazyplugin.commands;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPluginInterface;
import de.st_ddt.crazyutil.source.Permission;

public class CrazyPluginCommandMainSave extends CrazyCommandSave<CrazyPluginInterface>
{

	public CrazyPluginCommandMainSave(final CrazyPluginInterface plugin)
	{
		super(plugin, plugin);
	}

	@Override
	@Permission("{CRAZYPLUGIN}.save")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission(owner.getName() + ".save");
	}
}
