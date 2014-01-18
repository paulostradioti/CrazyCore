package de.st_ddt.crazyplugin.commands;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPluginInterface;
import de.st_ddt.crazyutil.source.Permission;

public class CrazyPluginCommandMainReload extends CrazyCommandReload<CrazyPluginInterface> implements CrazyPluginCommandExecutorInterface<CrazyPluginInterface>
{

	public CrazyPluginCommandMainReload(final CrazyPluginInterface plugin)
	{
		super(plugin, plugin);
	}

	@Override
	public final CrazyPluginInterface getPlugin()
	{
		return owner;
	}

	@Override
	@Permission({ "{CRAZYPLUGIN}.reload", "{CRAZYPLUGIN}.reload.*" })
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission(owner.getName() + ".reload");
	}
}
