package de.st_ddt.crazyutil.databases.tasks;

import de.st_ddt.crazyutil.databases.datas.DatabaseDataInterface;

public abstract class BasicDatabaseTask<A extends DatabaseDataInterface> implements DatabaseTaskInterface
{

	protected final A data;
	protected final boolean important;

	public BasicDatabaseTask(final A data)
	{
		super();
		this.data = data;
		this.important = false;
	}

	public BasicDatabaseTask(final A data, final boolean important)
	{
		super();
		this.data = data;
		this.important = important;
	}

	@Override
	public abstract boolean run();

	@Override
	public final String getPrimaryKey()
	{
		return data.getPrimaryKey();
	}

	@Override
	public final boolean isImportantTask()
	{
		return important;
	}
}
