package de.st_ddt.crazyutil.databases.config.serializer;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.databases.datas.serializer.DatabaseSerializer;

public interface ConfigDatabaseSerializer extends DatabaseSerializer
{

	public Object fromDatabase(ConfigurationSection config, String path, Object defaultValue);

	public void toDatabase(ConfigurationSection config, String path, Object value);
}
