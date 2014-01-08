package de.st_ddt.crazyplugin.commands;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPluginInterface;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.source.Permission;

public class CrazyPluginCommandMainHelp extends CrazyPluginCommandExecutor<CrazyPluginInterface>
{

	public CrazyPluginCommandMainHelp(final CrazyPluginInterface plugin)
	{
		super(plugin);
	}

	@Override
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		owner.show(sender, owner.getChatHeader(), true);
	}

	@Override
	@Permission("{CRAZYPLUGIN}.help")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission(owner.getName() + ".help");
	}
}
