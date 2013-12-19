package de.st_ddt.crazyutil.databases.flat;

import de.st_ddt.crazyutil.databases.datas.DatabaseField;
import de.st_ddt.crazyutil.databases.flat.serializer.FlatDatabaseSerializer;

public class FlatDatabaseField extends DatabaseField<FlatDatabaseSerializer>
{

	public FlatDatabaseField(final String name, final FlatDatabaseSerializer serializer)
	{
		super(name, serializer);
	}
}
