package de.st_ddt.crazyutil.databases.datas;

import de.st_ddt.crazyutil.databases.datas.serializer.DatabaseSerializer;

public class DatabaseField<S extends DatabaseSerializer>
{

	protected final String name;
	protected final S serializer;

	public DatabaseField(final String name, final S serializer)
	{
		super();
		this.name = name;
		this.serializer = serializer;
	}

	public final String getName()
	{
		return name;
	}

	public final S getSerializer()
	{
		return serializer;
	}
}
