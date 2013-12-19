package de.st_ddt.crazyutil.databases.flat.serializer;

import de.st_ddt.crazyutil.databases.datas.serializer.DatabaseSerializer;

public interface FlatDatabaseSerializer extends DatabaseSerializer
{

	public Object fromDatabase(String source);

	public String toDatabase(Object value);
}
