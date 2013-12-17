package de.st_ddt.crazyplugin.commands;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPluginInterface;
import de.st_ddt.crazyutil.modes.Mode;
import de.st_ddt.crazyutil.source.Permission;

public class CrazyPluginCommandMainMode extends CrazyCommandModeEditor<CrazyPluginInterface> implements CrazyPluginCommandExecutorInterface<CrazyPluginInterface>
{

	public CrazyPluginCommandMainMode(final CrazyPluginInterface plugin)
	{
		super(plugin);
	}

	@Override
	@Permission("$CRAZYPLUGIN$.mode")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission(owner.getName() + ".mode");
	}

	@Override
	public final CrazyPluginInterface getPlugin()
	{
		return owner;
	}

	@Override
	@Permission({ "$CRAZYPLUGIN$.mode.*", "$CRAZYPLUGIN$.mode.<MODENAME>" })
	public boolean hasAccessPermission(final CommandSender sender, final Mode<?> mode)
	{
		return sender.hasPermission(owner.getName() + ".mode.*") || sender.hasPermission(owner.getName() + ".mode." + mode.getName());
	}
}
