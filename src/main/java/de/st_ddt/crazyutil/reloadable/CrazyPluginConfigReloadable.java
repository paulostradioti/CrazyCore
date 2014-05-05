package de.st_ddt.crazyutil.reloadable;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPlugin;
import de.st_ddt.crazyutil.source.Localized;

public class CrazyPluginConfigReloadable implements Reloadable
{

	private final CrazyPlugin plugin;

	public CrazyPluginConfigReloadable(final CrazyPlugin plugin)
	{
		super();
		this.plugin = plugin;
	}

	@Override
	@Localized("{CRAZYPLUGIN}.CONFIG.RELOADED")
	public void reload(final CommandSender sender)
	{
		plugin.reloadConfig();
		plugin.loadConfiguration();
		plugin.saveConfiguration();
		plugin.sendLocaleMessage("CONFIG.RELOADED", sender);
	}

	@Override
	public boolean hasReloadPermission(final CommandSender sender)
	{
		return sender.hasPermission(plugin.getName() + ".reload.config");
	}

	@Override
	@Localized("{CRAZYPLUGIN}.CONFIG.SAVED")
	public void save(final CommandSender sender)
	{
		plugin.saveConfiguration();
		plugin.sendLocaleMessage("CONFIG.SAVED", sender);
	}

	@Override
	public boolean hasSavePermission(final CommandSender sender)
	{
		return sender.hasPermission(plugin.getName() + ".save.config");
	}
}
