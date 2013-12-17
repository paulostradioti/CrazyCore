package de.st_ddt.crazycore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.st_ddt.crazycore.CrazyCore;
import de.st_ddt.crazycore.tasks.PluginUpdateCheckTask;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.source.Localized;
import de.st_ddt.crazyutil.source.Permission;

public class CommandUpdateCheck extends CommandExecutor
{

	public CommandUpdateCheck(final CrazyCore owner)
	{
		super(owner);
	}

	@Override
	@Localized({ "CRAZYCORE.COMMAND.UPDATECHECK", "CRAZYCORE.COMMAND.UPDATECHECK.DISABLED", })
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (owner.isCheckingForUpdatesEnabled())
		{
			owner.sendLocaleMessage("COMMAND.UPDATECHECK", sender);
			Bukkit.getScheduler().runTaskAsynchronously(owner, new PluginUpdateCheckTask(owner, sender, true));
		}
		else
			owner.sendLocaleMessage("UPDATECHECK.DISABLED", sender);
	}

	@Override
	@Permission("crazycore.updatecheck")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission("crazycore.updatecheck");
	}
}
