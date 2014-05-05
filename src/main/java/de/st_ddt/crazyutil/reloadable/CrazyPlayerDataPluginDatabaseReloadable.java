package de.st_ddt.crazyutil.reloadable;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPlayerDataPluginInterface;
import de.st_ddt.crazyutil.source.Localized;
import de.st_ddt.crazyutil.source.Permission;

public class CrazyPlayerDataPluginDatabaseReloadable implements Reloadable
{

	private final CrazyPlayerDataPluginInterface plugin;

	public CrazyPlayerDataPluginDatabaseReloadable(final CrazyPlayerDataPluginInterface plugin)
	{
		super();
		this.plugin = plugin;
	}

	@Override
	@Localized("{CRAZYPLAYERDATAPLUGIN}.COMMAND.DATABASE.RELOADED")
	public void reload(final CommandSender sender)
	{
		plugin.loadDatabase();
		plugin.saveDatabase();
		plugin.sendLocaleMessage("COMMAND.DATABASE.RELOADED", sender);
	}

	@Override
	@Permission("{CRAZYPLAYERDATAPLUGIN}.reload.database")
	public boolean hasReloadPermission(final CommandSender sender)
	{
		return sender.hasPermission(plugin.getName() + ".reload.database") || sender.hasPermission(plugin.getName() + ".reload.*");
	}

	@Override
	@Localized("{CRAZYPLAYERDATAPLUGIN}.COMMAND.DATABASE.SAVED")
	public void save(final CommandSender sender)
	{
		plugin.saveDatabase();
		plugin.sendLocaleMessage("COMMAND.DATABASE.SAVED", sender);
	}

	@Override
	@Permission("{CRAZYPLAYERDATAPLUGIN}.save.database")
	public boolean hasSavePermission(final CommandSender sender)
	{
		return sender.hasPermission(plugin.getName() + ".save.database") || sender.hasPermission(plugin.getName() + ".save.*");
	}
}
