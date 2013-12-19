package de.st_ddt.crazyutil.databases.config.serializer;

import org.bukkit.configuration.ConfigurationSection;

public class StringConfigDatabaseSerializer implements ConfigDatabaseSerializer
{

	@Override
	public String fromDatabase(final ConfigurationSection config, final String path, final Object defaultValue)
	{
		return config.getString(path, (String) defaultValue);
	}

	@Override
	public void toDatabase(final ConfigurationSection config, final String path, final Object value)
	{
		config.set(path, value);
	}
}
