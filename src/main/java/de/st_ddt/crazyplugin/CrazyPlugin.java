package de.st_ddt.crazyplugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.st_ddt.crazycore.CrazyCore;
import de.st_ddt.crazyplugin.commands.CrazyCommandTreeExecutor;
import de.st_ddt.crazyplugin.commands.CrazyPluginCommandMainMode;
import de.st_ddt.crazyplugin.commands.CrazyPluginCommandMainTree;
import de.st_ddt.crazyplugin.tasks.LanguageLoadTask;
import de.st_ddt.crazyutil.ChatHelper;
import de.st_ddt.crazyutil.ChatHelperExtended;
import de.st_ddt.crazyutil.CrazyLogger;
import de.st_ddt.crazyutil.ListFormat;
import de.st_ddt.crazyutil.UpdateChecker;
import de.st_ddt.crazyutil.locales.CrazyLocale;
import de.st_ddt.crazyutil.reloadable.Reloadable;
import de.st_ddt.crazyutil.resources.ResourceHelper;
import de.st_ddt.crazyutil.source.Localized;
import de.st_ddt.crazyutil.source.LocalizedVariable;
import de.st_ddt.crazyutil.source.Permission;

@LocalizedVariable(variables = "CRAZYPLUGIN", values = "CRAZYPLUGIN")
public abstract class CrazyPlugin extends CrazyLightPlugin implements CrazyPluginInterface
{

	private static final LinkedHashMap<Class<? extends CrazyPlugin>, CrazyPlugin> plugins = new LinkedHashMap<Class<? extends CrazyPlugin>, CrazyPlugin>();
	protected final CrazyLogger logger = new CrazyLogger(this);
	protected final CrazyPluginCommandMainTree mainCommand = new CrazyPluginCommandMainTree(this);
	protected final CrazyPluginCommandMainMode modeCommand = new CrazyPluginCommandMainMode(this);
	protected final Map<String, Reloadable> reloadables = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private UpdateChecker updateChecker = null;
	protected CrazyLocale locale = null;
	protected String previousVersion = "0";
	protected boolean isUpdated = false;
	protected boolean isInstalled = false;

	public final static Collection<CrazyPlugin> getCrazyPlugins()
	{
		return plugins.values();
	}

	public final static CrazyPlugin getPlugin(final String name)
	{
		for (final CrazyPlugin plugin : plugins.values())
			if (plugin.getName().equalsIgnoreCase(name))
				return plugin;
		return null;
	}

	@Override
	public final boolean isInstalled()
	{
		return isInstalled;
	}

	@Override
	public final boolean isUpdated()
	{
		return isUpdated;
	}

	@Override
	public final CrazyCommandTreeExecutor<CrazyPluginInterface> getMainCommand()
	{
		return mainCommand;
	}

	@Override
	protected void initialize()
	{
		plugins.put(this.getClass(), this);
		getDataFolder().mkdir();
		new File(getDataFolder().getPath() + "/lang").mkdirs();
		checkLocale();
		final ConfigurationSection config = getConfig();
		previousVersion = config.getString("version", "0");
		isInstalled = previousVersion.equals("0");
		isUpdated = !previousVersion.equals(getDescription().getVersion());
		config.set("version", getDescription().getVersion());
		final Integer bukkitProjectId = getBukkitProjectId();
		if (bukkitProjectId != null)
			this.updateChecker = new UpdateChecker(getName(), getVersion(), bukkitProjectId);
	}

	@Override
	@Localized("CRAZYPLUGIN.UPDATED {Name} {Version}")
	protected void enable()
	{
		if (isUpdated)
			broadcastLocaleMessage("UPDATED", getName(), getDescription().getVersion());
		load();
		saveConfiguration();
		super.enable();
		registerHooks();
		registerCommands();
	}

	protected void registerHooks()
	{
	}

	protected void registerCommands()
	{
		registerCommand(getName(), mainCommand);
		mainCommand.addSubCommand(modeCommand, "mode");
		modeCommand.addMode(getChatHeaderMode());
	}

	public final void registerCommand(final String commandName, final CommandExecutor commandExecutor)
	{
		final PluginCommand command = getCommand(commandName);
		if (command != null)
			command.setExecutor(commandExecutor);
	}

	@Override
	public void load()
	{
		loadConfiguration();
	}

	@Override
	public final void loadConfiguration()
	{
		loadConfiguration(getConfig());
	}

	protected void loadConfiguration(final ConfigurationSection config)
	{
		logger.createLogChannels(config.getConfigurationSection("logs"), getLogChannels());
	}

	protected String[] getLogChannels()
	{
		return new String[0];
	}

	@Override
	public void save()
	{
		saveConfiguration();
	}

	@Override
	public final void saveConfiguration()
	{
		saveConfiguration(getConfig());
		saveConfig();
	}

	protected void saveConfiguration(final ConfigurationSection config)
	{
		logger.save(getConfig(), "logs.");
	}

	@Override
	public void show(final CommandSender target, final String chatHeader, final boolean showDetailed)
	{
		super.show(target, chatHeader, showDetailed);
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable()
		{

			@Override
			public void run()
			{
				checkForUpdateWithMessage(false, target);
			}
		});
	}

	public void checkLocale()
	{
		locale = CrazyLocale.getPluginHead(this);
		locale.setAlternative(CrazyLocale.getLocaleHead().getLanguageEntry("CRAZYPLUGIN"));
	}

	@Override
	public final void sendLocaleMessage(final String localepath, final CommandSender target, final Object... args)
	{
		sendLocaleMessage(getLocale().getLanguageEntry(localepath), target, args);
	}

	@Override
	public final void sendLocaleMessage(final CrazyLocale locale, final CommandSender target, final Object... args)
	{
		ChatHelper.sendMessage(target, getChatHeader(), locale, args);
	}

	@Override
	public final void sendLocaleMessage(final String localepath, final CommandSender[] targets, final Object... args)
	{
		sendLocaleMessage(getLocale().getLanguageEntry(localepath), targets, args);
	}

	@Override
	public final void sendLocaleMessage(final CrazyLocale locale, final CommandSender[] targets, final Object... args)
	{
		ChatHelper.sendMessage(targets, getChatHeader(), locale, args);
	}

	@Override
	public final void sendLocaleMessage(final String localepath, final Collection<? extends CommandSender> targets, final Object... args)
	{
		sendLocaleMessage(getLocale().getLanguageEntry(localepath), targets, args);
	}

	@Override
	public final void sendLocaleMessage(final CrazyLocale locale, final Collection<? extends CommandSender> targets, final Object... args)
	{
		ChatHelper.sendMessage(targets, getChatHeader(), locale, args);
	}

	@Override
	public void sendLocaleList(final CommandSender target, final ListFormat format, final int amount, final int page, final List<?> datas)
	{
		ChatHelperExtended.sendList(target, getChatHeader(), format, amount, page, datas);
	}

	@Override
	public final void sendLocaleList(final CommandSender target, final String formatPath, final int amount, final int page, final List<?> datas)
	{
		sendLocaleList(target, formatPath + ".HEADER", formatPath + ".LISTFORMAT", formatPath + ".ENTRYFORMAT", amount, page, datas);
	}

	@Override
	public final void sendLocaleList(final CommandSender target, final String headFormatPath, final String listFormatPath, final String entryFormatPath, final int amount, final int page, final List<?> datas)
	{
		CrazyLocale headFormat = null;
		if (headFormatPath != null)
			headFormat = getLocale().getLanguageEntry(headFormatPath);
		CrazyLocale listFormat = null;
		if (listFormatPath != null)
			listFormat = getLocale().getLanguageEntry(listFormatPath);
		CrazyLocale entryFormat = null;
		if (entryFormatPath != null)
			entryFormat = getLocale().getLanguageEntry(entryFormatPath);
		sendLocaleList(target, headFormat, listFormat, entryFormat, amount, page, datas);
	}

	@Override
	@Localized({ "CRAZYPLUGIN.LIST.HEADER {CurrentPage} {MaxPage} {ChatHeader} {DateTime}", "CRAZYPLUGIN.LIST.LISTFORMAT {Index} {Entry} {ChatHeader}", "CRAZYPLUGIN.LIST.ENTRYFORMAT {Name} ..." })
	public final void sendLocaleList(final CommandSender target, CrazyLocale headFormat, CrazyLocale listFormat, CrazyLocale entryFormat, final int amount, final int page, final List<?> datas)
	{
		if (headFormat == null)
			headFormat = getLocale().getLanguageEntry("LIST.HEADER");
		if (listFormat == null)
			listFormat = getLocale().getLanguageEntry("LIST.LISTFORMAT");
		if (entryFormat == null)
			entryFormat = getLocale().getLanguageEntry("LIST.ENTRYFORMAT");
		ChatHelperExtended.sendList(target, getChatHeader(), headFormat.getLanguageText(target), listFormat.getLanguageText(target), entryFormat.getLanguageText(target), amount, page, datas);
	}

	@Override
	public final void broadcastLocaleMessage(final String localepath, final Object... args)
	{
		broadcastLocaleMessage(getLocale().getLanguageEntry(localepath), args);
	}

	@Override
	public final void broadcastLocaleMessage(final CrazyLocale locale, final Object... args)
	{
		sendLocaleMessage(locale, Bukkit.getConsoleSender(), args);
		sendLocaleMessage(locale, Bukkit.getOnlinePlayers(), args);
	}

	@Override
	public final void broadcastLocaleMessage(final boolean console, final String permission, final String localepath, final Object... args)
	{
		broadcastLocaleMessage(console, permission, getLocale().getLanguageEntry(localepath), args);
	}

	@Override
	public final void broadcastLocaleMessage(final boolean console, final String permission, final CrazyLocale locale, final Object... args)
	{
		if (permission == null)
			broadcastLocaleMessage(console, new String[] {}, locale, args);
		else
			broadcastLocaleMessage(console, new String[] { permission }, locale, args);
	}

	@Override
	public final void broadcastLocaleMessage(final boolean console, final String[] permissions, final String localepath, final Object... args)
	{
		broadcastLocaleMessage(console, permissions, getLocale().getLanguageEntry(localepath), args);
	}

	@Override
	public final void broadcastLocaleMessage(final boolean console, final String[] permissions, final CrazyLocale locale, final Object... args)
	{
		if (console)
			sendLocaleMessage(locale, Bukkit.getConsoleSender(), args);
		Player: for (final Player player : Bukkit.getOnlinePlayers())
		{
			for (final String permission : permissions)
				if (!player.hasPermission(permission))
					continue Player;
			sendLocaleMessage(locale, player, args);
		}
	}

	@Override
	public final CrazyLogger getCrazyLogger()
	{
		return logger;
	}

	@Override
	public final Map<String, Reloadable> getReloadables()
	{
		return reloadables;
	}

	@Override
	public final CrazyLocale getLocale()
	{
		return locale;
	}

	protected boolean isSupportingLanguages()
	{
		return true;
	}

	public final void loadLanguage(final String language)
	{
		loadLanguage(language, Bukkit.getConsoleSender());
	}

	public void loadLanguageDelayed(final String language, final CommandSender sender)
	{
		Bukkit.getScheduler().runTaskAsynchronously(this, new LanguageLoadTask(this, language, sender));
	}

	@Localized({ "CRAZYPLUGIN.LANGUAGE.ERROR.AVAILABLE {Language} CRAZYPLUGIN.LANGUAGE", "CRAZYPLUGIN.LANGUAGE.ERROR.READ {Language} {Plugin}" })
	public void loadLanguage(String language, final CommandSender sender)
	{
		if (!isSupportingLanguages())
			return;
		language = CrazyLocale.fixLanguage(language);
		if (language == null)
			return;
		// default files
		File file = new File(getDataFolder(), "lang/" + language + ".lang");
		if (!file.exists())
			if (!ResourceHelper.saveResource(this, "/lang/" + language + ".lang", file))
			{
				sendLocaleMessage("LANGUAGE.ERROR.AVAILABLE", sender, language, getName());
				return;
			}
		try
		{
			loadLanguageFile(language, file);
		}
		catch (final IOException e)
		{
			sendLocaleMessage("LANGUAGE.ERROR.READ", sender, language, getName());
		}
		// Custom files:
		file = new File(getDataFolder(), "lang/custom_" + language + ".lang");
		if (file.exists())
			try
			{
				loadLanguageFile(language, file);
			}
			catch (final IOException e)
			{
				sendLocaleMessage("LANGUAGE.ERROR.READ", sender, language + " (Custom)", getName());
			}
	}

	// public String getMainDownloadLocation()
	// {
	// return "https://raw.github.com/ST-DDT/Crazy/master/" + getName() + "/src/resource";
	// }
	public final void updateLanguage(final String language, final boolean reload)
	{
		updateLanguage(language, Bukkit.getConsoleSender(), reload);
	}

	@Localized({ "CRAZYPLUGIN.LANGUAGE.ERROR.AVAILABLE {Language} {Plugin}", "CRAZYPLUGIN.LANGUAGE.ERROR.READ {Language} {Plugin}" })
	public void updateLanguage(final String language, final CommandSender sender, final boolean reload)
	{
		if (!isSupportingLanguages())
			return;
		final File file = new File(getDataFolder(), "lang/" + language + ".lang");
		if (!ResourceHelper.saveResource(this, "/lang/" + language + ".lang", file))
		{
			sendLocaleMessage("LANGUAGE.ERROR.AVAILABLE", sender, language, getName());
			return;
		}
		if (reload)
		{
			try
			{
				loadLanguageFile(language, file);
			}
			catch (final IOException e)
			{
				sendLocaleMessage("LANGUAGE.ERROR.READ", sender, language, getName());
			}
			final File customFile = new File(getDataFolder(), "lang/custom_" + language + ".lang");
			if (customFile.exists())
				try
				{
					loadLanguageFile(language, customFile);
				}
				catch (final IOException e)
				{
					sendLocaleMessage("LANGUAGE.ERROR.READ", sender, language + " (Custom)", getName());
				}
		}
	}

	public final void unpackLanguage(final String language)
	{
		unpackLanguage(language, Bukkit.getConsoleSender());
	}

	@Localized("CRAZYPLUGIN.LANGUAGE.ERROR.EXTRACT {Language} {Plugin}")
	public void unpackLanguage(final String language, final CommandSender sender)
	{
		if (!ResourceHelper.unpackResource(this, "/lang/" + language + ".lang", "lang/" + language + ".lang"))
			sendLocaleMessage("LANGUAGE.ERROR.EXTRACT", sender, language, getName());
	}

	public final void loadLanguageFile(final String language, final File file) throws IOException
	{
		CrazyLocale.readFile(language, file);
	}

	public Integer getBukkitProjectId()
	{
		return null;
	}

	@Override
	public final String getUpdateVersion()
	{
		if (updateChecker == null)
			return null;
		else if (updateChecker.hasUpdate())
			return updateChecker.getLatestVersion();
		else
			return null;
	}

	public final String getUpdateURL()
	{
		if (updateChecker == null)
			return null;
		else if (updateChecker.hasUpdate())
			return updateChecker.getLatestLink();
		else
			return null;
	}

	@Override
	public boolean checkForUpdate(final boolean force)
	{
		if (updateChecker == null)
			return false;
		else if (!CrazyCore.getPlugin().isCheckingForUpdatesEnabled())
			return updateChecker.hasUpdate();
		else if (force || !updateChecker.wasQueried())
			return updateChecker.query();
		else
			return updateChecker.hasUpdate();
	}

	@Permission("crazycore.updatecheck")
	@Localized("CRAZYPLUGIN.PLUGININFO.UPDATE {UpdateVersion} {UpdateType} {UpdateGameVersion} {UpdateDownloadLink}")
	public final boolean checkForUpdateWithMessage(final boolean force, final CommandSender sender)
	{
		final boolean res = checkForUpdate(force);
		if (res)
			if (sender == null)
				broadcastLocaleMessage(true, "crazycore.updatecheck", "PLUGININFO.UPDATE", updateChecker.getLatestVersion(), updateChecker.getLatestType(), updateChecker.getLatestGameVersion(), updateChecker.getLatestLink());
			else
				sendLocaleMessage("PLUGININFO.UPDATE", sender, updateChecker.getLatestVersion(), updateChecker.getLatestType(), updateChecker.getLatestGameVersion(), updateChecker.getLatestLink());
		return res;
	}
}
