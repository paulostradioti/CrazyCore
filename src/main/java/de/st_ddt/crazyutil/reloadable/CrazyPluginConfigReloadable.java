package de.st_ddt.crazyutil.reloadable;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPlugin;

public class CrazyPluginConfigReloadable implements Reloadable
{

	private final CrazyPlugin plugin;

	public CrazyPluginConfigReloadable(final CrazyPlugin plugin)
	{
		super();
		this.plugin = plugin;
	}

	@Override
	public void reload(final CommandSender sender)
	{
		plugin.reloadConfig();
		plugin.loadConfiguration();
		plugin.sendLocaleMessage("RELOAD.CONFIG", sender);
	}

	@Override
	public boolean hasReloadPermission(final CommandSender sender)
	{
		return sender.hasPermission(plugin.getName() + ".reload.config");
	}

	@Override
	public void save(final CommandSender sender)
	{
		plugin.saveConfiguration();
		plugin.sendLocaleMessage("SAVE.CONFIG", sender);
	}

	@Override
	public boolean hasSavePermission(final CommandSender sender)
	{
		return sender.hasPermission(plugin.getName() + ".save.config");
	}
}
