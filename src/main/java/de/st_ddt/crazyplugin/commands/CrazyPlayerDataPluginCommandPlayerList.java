package de.st_ddt.crazyplugin.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.CrazyPlayerDataPluginInterface;
import de.st_ddt.crazyplugin.comparator.PlayerDataComparator;
import de.st_ddt.crazyplugin.data.PlayerDataFilterInterface;
import de.st_ddt.crazyplugin.data.PlayerDataInterface;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatHelperExtended;
import de.st_ddt.crazyutil.Filter;
import de.st_ddt.crazyutil.ListFormat;
import de.st_ddt.crazyutil.Tabbed;
import de.st_ddt.crazyutil.databases.PlayerDataDatabase;
import de.st_ddt.crazyutil.source.Permission;

public class CrazyPlayerDataPluginCommandPlayerList<T extends PlayerDataInterface> extends CrazyPlayerDataPluginCommandExecutor<T, CrazyPlayerDataPluginInterface<T, ? extends T>>
{

	private final Collection<? extends PlayerDataFilterInterface<T>> availableFilters;
	private final Map<String, PlayerDataComparator<T>> availableSorters;
	private final PlayerDataComparator<T> defaultSort;
	private final ListFormat format;

	public CrazyPlayerDataPluginCommandPlayerList(final CrazyPlayerDataPluginInterface<T, ? extends T> plugin)
	{
		super(plugin);
		this.availableFilters = plugin.getPlayerDataFilters();
		this.availableSorters = plugin.getPlayerDataComparators();
		this.defaultSort = plugin.getPlayerDataDefaultComparator();
		this.format = plugin.getPlayerDataListFormat();
	}

	@Override
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		final List<T> list;
		final PlayerDataDatabase<? extends T> database = owner.getCrazyDatabase();
		if (database == null)
			list = new ArrayList<T>();
		else
			synchronized (database.getDatabaseLock())
			{
				list = new ArrayList<T>(database.getAllEntries());
			}
		ChatHelperExtended.processFullListCommand(sender, args, owner.getChatHeader(), format, Filter.getFilterInstances(availableFilters), availableSorters, defaultSort, owner.getPlayerDataListModder(), list);
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		final Map<String, Tabbed> params = new HashMap<String, Tabbed>();
		final Tabbed pageTab = ChatHelperExtended.listTabHelp(params, sender, availableFilters, availableSorters);
		return ChatHelperExtended.tabHelpWithPipe(sender, args, params, pageTab);
	}

	@Override
	@Permission("{CRAZYPLAYERDATAPLUGIN}.player.list")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission(owner.getName() + ".player.list");
	}
}
