package de.st_ddt.crazyutil.databases.flat.serializer;

public class StringFlatDatabaseSerializer implements FlatDatabaseSerializer
{

	@Override
	public Object fromDatabase(final String source)
	{
		return source;
	}

	@Override
	public String toDatabase(final Object value)
	{
		return value.toString();
	}
}
