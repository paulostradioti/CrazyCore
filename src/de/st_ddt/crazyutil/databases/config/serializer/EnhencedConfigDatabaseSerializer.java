package de.st_ddt.crazyutil.databases.config.serializer;

public abstract class EnhencedConfigDatabaseSerializer implements ConfigDatabaseSerializer
{

	protected final ConfigDatabaseSerializer serializer;

	public EnhencedConfigDatabaseSerializer(final ConfigDatabaseSerializer serializer)
	{
		super();
		this.serializer = serializer;
	}
}
