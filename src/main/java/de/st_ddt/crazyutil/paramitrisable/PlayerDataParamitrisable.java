package de.st_ddt.crazyutil.paramitrisable;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.OfflinePlayer;

import de.st_ddt.crazyplugin.CrazyPlayerDataPlugin;
import de.st_ddt.crazyplugin.CrazyPlayerDataPluginInterface;
import de.st_ddt.crazyplugin.data.PlayerDataInterface;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.databases.PlayerDataDatabase;

public class PlayerDataParamitrisable<S extends PlayerDataInterface> extends TypedParamitrisable<S>
{

	private static <S extends PlayerDataInterface> S getPlayerData(final CrazyPlayerDataPlugin<S, ? extends S> plugin, final String defaultName)
	{
		final PlayerDataDatabase<? extends S> database = plugin.getCrazyDatabase();
		if (database == null)
			return null;
		else
			return database.getEntry(defaultName);
	}

	private static <S extends PlayerDataInterface> S getPlayerData(final CrazyPlayerDataPlugin<S, ? extends S> plugin, final OfflinePlayer defaultName)
	{
		final PlayerDataDatabase<? extends S> database = plugin.getCrazyDatabase();
		if (database == null)
			return null;
		else
			return database.getEntry(defaultName);
	}

	private final CrazyPlayerDataPlugin<S, ? extends S> plugin;

	public PlayerDataParamitrisable(final CrazyPlayerDataPlugin<S, ? extends S> plugin)
	{
		super(null);
		this.plugin = plugin;
	}

	public PlayerDataParamitrisable(final CrazyPlayerDataPlugin<S, ? extends S> plugin, final String defaultName)
	{
		super(getPlayerData(plugin, defaultName));
		this.plugin = plugin;
	}

	public PlayerDataParamitrisable(final CrazyPlayerDataPlugin<S, ? extends S> plugin, final OfflinePlayer defaultPlayer)
	{
		super(getPlayerData(plugin, defaultPlayer));
		this.plugin = plugin;
	}

	@Override
	public void setParameter(final String parameter) throws CrazyException
	{
		value = getPlayerData(plugin, parameter);
		if (value == null)
			throw new CrazyCommandNoSuchException("PlayerData", parameter);
	}

	@Override
	public List<String> tab(final String parameter)
	{
		return tabHelp(plugin, parameter);
	}

	public static List<String> tabHelp(final CrazyPlayerDataPluginInterface<?, ?> plugin, String parameter)
	{
		parameter = parameter.toLowerCase();
		final List<String> res = new LinkedList<String>();
		int max = 20;
		final PlayerDataDatabase<?> database = plugin.getCrazyDatabase();
		if (database != null)
			synchronized (database.getDatabaseLock())
			{
				for (final PlayerDataInterface entry : database)
					if (entry.getName().toLowerCase().startsWith(parameter))
					{
						res.add(entry.getName());
						if (--max < 1)
							break;
					}
			}
		return res;
	}
}
