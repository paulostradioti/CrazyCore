package de.st_ddt.crazyutil.databases.config.serializer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.Named;

public class KeyedListConfigDatabaseSerializer extends EnhencedConfigDatabaseSerializer
{

	public KeyedListConfigDatabaseSerializer(final ConfigDatabaseSerializer serializer)
	{
		super(serializer);
	}

	@Override
	public List<?> fromDatabase(final ConfigurationSection config, final String path, final Object defaultValue)
	{
		final List<Object> list = new ArrayList<>();
		final ConfigurationSection section = config.getConfigurationSection(path);
		if (section != null)
			for (final String key : section.getKeys(false))
			{
				final Object temp = serializer.fromDatabase(config, path + "." + key, null);
				if (temp == null)
					if (defaultValue != null)
						continue;
				list.add(temp);
			}
		return list;
	}

	@Override
	public void toDatabase(final ConfigurationSection config, final String path, final Object value)
	{
		config.set(path, null);
		@SuppressWarnings("unchecked")
		final List<Object> list = (List<Object>) value;
		int a = 0;
		for (final Object entry : list)
		{
			final String name;
			if (entry instanceof Named)
				name = ((Named) entry).getName();
			else
				name = entry.getClass().getSimpleName();
			serializer.toDatabase(config, path + "." + name + (a++), entry);
		}
	}
}
