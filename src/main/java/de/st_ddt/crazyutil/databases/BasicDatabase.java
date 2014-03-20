package de.st_ddt.crazyutil.databases;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.ChatHelper;

public abstract class BasicDatabase<S extends DatabaseEntry> implements Database<S>
{

	protected final Map<String, S> datas = Collections.synchronizedMap(new HashMap<String, S>());
	protected final Class<S> clazz;
	protected final Constructor<S> constructor;

	public BasicDatabase(final Class<S> clazz)
	{
		super();
		this.clazz = clazz;
		this.constructor = getConstructor(clazz);
	}

	@Override
	public Class<S> getEntryClazz()
	{
		return clazz;
	}

	protected abstract Constructor<S> getConstructor(final Class<S> clazz);

	@Override
	public abstract S createEntry(String entry);

	@Override
	public abstract void initialize() throws Exception;

	@Override
	public boolean hasEntry(final String key)
	{
		return datas.containsKey(key.toLowerCase());
	}

	@Override
	public final S getEntry(final String key)
	{
		return datas.get(key.toLowerCase());
	}

	@Override
	public Object getDatabaseLock()
	{
		return datas;
	}

	@Override
	public final Collection<S> getAllEntries()
	{
		return datas.values();
	}

	@Override
	public final int size()
	{
		return datas.size();
	}

	@Override
	public abstract S loadEntry(String key);

	@Override
	public abstract void loadAllEntries();

	@Override
	public boolean unloadEntry(final String key)
	{
		final S data = datas.remove(key.toLowerCase());
		if (data == null)
			return false;
		else
		{
			data.flush();
			return true;
		}
	}

	@Override
	public void unloadAllEntries()
	{
		synchronized (datas)
		{
			for (final S data : datas.values())
				data.flush();
			datas.clear();
		}
	}

	@Override
	public boolean deleteEntry(final String key)
	{
		return datas.remove(key.toLowerCase()) != null;
	}

	@Override
	public void purgeDatabase()
	{
		datas.clear();
	}

	@Override
	public void save(final ConfigurationSection config, final String path)
	{
		config.set(path + "saveType", getDatabaseType());
	}

	protected final void shortPrintStackTrace(final Throwable main, final Throwable throwable)
	{
		ChatHelper.shortPrintStackTrace(main, throwable, this);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " (Contains " + size() + " entries of type " + clazz.getSimpleName() + ")";
	}
}
