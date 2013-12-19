package de.st_ddt.crazyutil.databases.config.serializer;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class StringListConfigDatabaseSerializer implements ConfigDatabaseSerializer
{

	@Override
	public List<String> fromDatabase(final ConfigurationSection config, final String path, final Object defaultValue)
	{
		return config.getStringList(path);
	}

	@Override
	public void toDatabase(final ConfigurationSection config, final String path, final Object value)
	{
		config.set(path, value);
	}
}
