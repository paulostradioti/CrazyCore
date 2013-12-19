package de.st_ddt.crazyutil.databases.sql.serializer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.st_ddt.crazyutil.databases.datas.serializer.DatabaseSerializer;

public interface SQLDatabaseSerializer extends DatabaseSerializer
{

	public Object fromDatabase(ResultSet result, String column) throws SQLException;

	public void toDatabase(PreparedStatement stmt, int index, Object value) throws SQLException;
}
