package de.st_ddt.crazyutil.databases;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigurationDatabaseEntry extends DatabaseEntry
{

	// public ConfigurationDatabaseEntry(ConfigurationSection rawData);
	public abstract void save(ConfigurationSection config, String table);
}
