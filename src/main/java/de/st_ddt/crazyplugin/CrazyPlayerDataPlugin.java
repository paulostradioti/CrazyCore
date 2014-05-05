package de.st_ddt.crazyplugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyplugin.commands.CrazyPlayerDataPluginCommandPlayerTree;
import de.st_ddt.crazyplugin.data.PlayerDataFilter;
import de.st_ddt.crazyplugin.data.PlayerDataInterface;
import de.st_ddt.crazyutil.ChatHelper;
import de.st_ddt.crazyutil.ListFormat;
import de.st_ddt.crazyutil.ListOptionsModder;
import de.st_ddt.crazyutil.comparators.PlayerDataComparator;
import de.st_ddt.crazyutil.comparators.PlayerDataNameComparator;
import de.st_ddt.crazyutil.databases.PlayerDataDatabase;
import de.st_ddt.crazyutil.reloadable.CrazyPlayerDataPluginDatabaseReloadable;
import de.st_ddt.crazyutil.reloadable.Reloadable;
import de.st_ddt.crazyutil.source.Localized;
import de.st_ddt.crazyutil.source.LocalizedVariable;
import de.st_ddt.crazyutil.source.Permission;

@LocalizedVariable(variables = "CRAZYPLAYERDATAPLUGIN", values = "CRAZYPLUGIN")
public abstract class CrazyPlayerDataPlugin<T extends PlayerDataInterface, S extends T> extends CrazyPlugin implements CrazyPlayerDataPluginInterface<T, S>
{

	private final static Map<Class<? extends CrazyPlugin>, CrazyPlayerDataPlugin<? extends PlayerDataInterface, ? extends PlayerDataInterface>> playerDataPlugins = new LinkedHashMap<Class<? extends CrazyPlugin>, CrazyPlayerDataPlugin<? extends PlayerDataInterface, ? extends PlayerDataInterface>>();
	protected final Collection<PlayerDataFilter<T>> playerDataFilters = new ArrayList<PlayerDataFilter<T>>();
	protected final Map<String, PlayerDataComparator<T>> playerDataSorters = new HashMap<String, PlayerDataComparator<T>>();
	protected final CrazyPlayerDataPluginCommandPlayerTree<T> playerCommand = new CrazyPlayerDataPluginCommandPlayerTree<T>(this);
	protected PlayerDataDatabase<S> database;
	protected boolean saveDatabaseOnShutdown;
	private final ListFormat defaultPlayerListFormat = new ListFormat()
	{

		@Override
		@Localized("CRAZYPLUGIN.COMMAND.PLAYER.LIST.HEADER {CurrentPage} {MaxPage} {ChatHeader} {DateTime}")
		public String headFormat(final CommandSender sender)
		{
			return getLocale().getLanguageEntry("COMMAND.PLAYER.LIST.HEADER").getLanguageText(sender);
		}

		@Override
		@Localized("CRAZYPLUGIN.COMMAND.PLAYER.LIST.LISTFORMAT {Index} {Entry} {ChatHeader}")
		public String listFormat(final CommandSender sender)
		{
			return getLocale().getLanguageEntry("COMMAND.PLAYER.LIST.LISTFORMAT").getLanguageText(sender);
		}

		@Override
		@Localized("CRAZYPLUGIN.COMMAND.PLAYER.LIST.ENTRYFORMAT {Name} {...}")
		public String entryFormat(final CommandSender sender)
		{
			return getLocale().getLanguageEntry("COMMAND.PLAYER.LIST.ENTRYFORMAT").getLanguageText(sender);
		}
	};

	public final static Collection<CrazyPlayerDataPlugin<? extends PlayerDataInterface, ? extends PlayerDataInterface>> getCrazyPlayerDataPlugins()
	{
		return playerDataPlugins.values();
	}

	public final static CrazyPlugin getPlayerDataPlugin(final Class<? extends CrazyPlugin> plugin)
	{
		return playerDataPlugins.get(plugin);
	}

	public final static CrazyPlugin getPlayerDataPlugin(final String name)
	{
		for (final CrazyPlugin plugin : playerDataPlugins.values())
			if (plugin.getName().equalsIgnoreCase(name))
				return plugin;
		return null;
	}

	public CrazyPlayerDataPlugin()
	{
		super();
		registerSorters();
	}

	private void registerSorters()
	{
		playerDataSorters.put("name", new PlayerDataNameComparator<T>());
		playerDataSorters.put("default", getPlayerDataDefaultComparator());
	}

	@Override
	public PlayerDataDatabase<S> getCrazyDatabase()
	{
		return database;
	}

	@Override
	public final CrazyPlayerDataPluginCommandPlayerTree<T> getPlayerCommand()
	{
		return playerCommand;
	}

	@Override
	public T getAvailablePlayerData(final String name)
	{
		if (database == null)
			return null;
		else
			return database.getEntry(name);
	}

	@Override
	public final T getAvailablePlayerData(final OfflinePlayer player)
	{
		return getAvailablePlayerData(player.getName());
	}

	@Override
	@Localized("{CRAZYPLAYERDATAPLUGIN}.PLUGININFO.DATABASEENTRIES {EntryCount}")
	public void show(final CommandSender target, final String chatHeader, final boolean showDetailed)
	{
		super.show(target, chatHeader, showDetailed);
		if (database != null)
			ChatHelper.sendMessage(target, chatHeader, getLocale().getLanguageEntry("PLUGININFO.DATABASEENTRIES"), database.getAllEntries().size());
	}

	@Override
	protected void initialize()
	{
		playerDataPlugins.put(this.getClass(), this);
		PROVIDERS.add(this);
		super.initialize();
	}

	@Override
	protected void enable()
	{
		super.enable();
		mainCommand.addSubCommand(playerCommand, "p", "plr", "player", "players");
		final Reloadable reloadable = new CrazyPlayerDataPluginDatabaseReloadable(this);
		reloadables.put("d", reloadable);
		reloadables.put("db", reloadable);
		reloadables.put("database", reloadable);
	}

	@Override
	protected void disable()
	{
		if (saveDatabaseOnShutdown)
			saveDatabase();
	}

	@Override
	public void load()
	{
		super.load();
		loadDatabase();
	}

	@Override
	@Localized({ "{CRAZYPLAYERDATAPLUGIN}.DATABASE.ACCESSWARN {SaveType}", "{CRAZYPLAYERDATAPLUGIN}.DATABASE.LOADED {EntryCount}" })
	public final void loadDatabase()
	{
		loadDatabase(getConfig());
	}

	protected void loadDatabase(final ConfigurationSection config)
	{
	}

	@Override
	protected void loadConfiguration(final ConfigurationSection config)
	{
		super.loadConfiguration(config);
		saveDatabaseOnShutdown = config.getBoolean("database.saveOnShutdown", true);
	}

	@Override
	public void save()
	{
		saveDatabase();
		super.save();
	}

	@Override
	public final void saveDatabase()
	{
		saveDatabase(getConfig());
	}

	protected void saveDatabase(final ConfigurationSection config)
	{
		if (database != null)
			database.save(config, "database.");
	}

	@Override
	protected void saveConfiguration(final ConfigurationSection config)
	{
		super.saveConfiguration(config);
		if (database != null)
			database.save(config, "database.");
		config.set("database.saveOnShutdown", saveDatabaseOnShutdown);
	}

	@Override
	public final Collection<? extends PlayerDataFilter<T>> getPlayerDataFilters()
	{
		return playerDataFilters;
	}

	@Override
	public final Map<String, PlayerDataComparator<T>> getPlayerDataComparators()
	{
		return playerDataSorters;
	}

	@Override
	public PlayerDataComparator<T> getPlayerDataDefaultComparator()
	{
		return new PlayerDataNameComparator<T>();
	}

	@Override
	public ListFormat getPlayerDataListFormat()
	{
		return defaultPlayerListFormat;
	}

	@Override
	public ListOptionsModder<T> getPlayerDataListModder()
	{
		return null;
	}
}
