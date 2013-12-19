package de.st_ddt.crazyutil.databases.sql;

import de.st_ddt.crazyutil.databases.datas.DatabaseField;
import de.st_ddt.crazyutil.databases.sql.serializer.SQLDatabaseSerializer;

public class SQLDatabaseField extends DatabaseField<SQLDatabaseSerializer>
{

	protected String realName;

	public SQLDatabaseField(final String name, final SQLDatabaseSerializer serializer)
	{
		super(name, serializer);
		this.realName = name;
	}

	public final String getRealName()
	{
		return realName;
	}

	public final void setRealName(final String realName)
	{
		// EDIT verify
		this.realName = realName;
	}
}
