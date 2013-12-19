package de.st_ddt.crazyutil.databases.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

import de.st_ddt.crazyutil.databases.BasicDatabase;
import de.st_ddt.crazyutil.databases.datas.DatabaseDataInterface;
import de.st_ddt.crazyutil.databases.tasks.BasicDatabaseTask;
import de.st_ddt.crazyutil.databases.tasks.DatabaseTaskInterface;

public class SQLDatabase<A extends DatabaseDataInterface> extends BasicDatabase<A, SQLDatabaseField>
{

	protected final SQLConnectionPool connections;
	protected final String table;

	public SQLDatabase(final Class<? extends A> entryClass, final SQLDatabaseField[] fields, final SQLConnectionPool connections, final String table)
	{
		super(entryClass, fields);
		this.connections = connections;
		this.table = table;
		// EDIT check connection!=null && tableName valid!
	}

	@Override
	public Set<String> getAllRawKeys()
	{
		final Connection connection = connections.getConnection();
		final Set<String> res = new TreeSet<String>();
		if (connection == null)
			return null;
		try (final Statement query = connection.createStatement())
		{
			final String columnLabel = fields[1].getRealName();
			try (final ResultSet datas = query.executeQuery("SELECT '" + columnLabel + "' FROM " + table))
			{
				while (datas.next())
					res.add(datas.getString(columnLabel));
			}
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			connections.releaseConnection(connection);
		}
		return res;
	}

	@Override
	public DatabaseTaskInterface loadTask(final A data)
	{
		return new BasicDatabaseTask<A>(data, true)
		{

			@Override
			public boolean run()
			{
				final Connection connection = connections.getConnection();
				if (connection == null)
					return false;
				try (final PreparedStatement query = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + fields[0].getRealName() + "=? LIMIT 1"))
				{
					query.setString(1, data.getPrimaryKey());
					try (final ResultSet result = query.executeQuery())
					{
						// EDIT CaseFix siehe FlatDB
						if (result.next())
						{
							for (int i = 1; i < fields.length; i++)
								data.setField(i, fields[i].getSerializer().fromDatabase(result, fields[i].getRealName()));
							datas.put(data.getPrimaryKey(), data);
						}
					}
					return true;
				}
				catch (final SQLException e)
				{
					e.printStackTrace();
					return false;
				}
				finally
				{
					connections.releaseConnection(connection);
				}
			}
		};
	}

	@Override
	public DatabaseTaskInterface loadTask(final A data, final int index)
	{
		return new BasicDatabaseTask<A>(data, true)
		{

			@Override
			public boolean run()
			{
				if (index > 0 && index < fields.length)
				{
					final Connection connection = connections.getConnection();
					if (connection == null)
						return false;
					try (final PreparedStatement query = connection.prepareStatement("SELECT " + fields[index].getRealName() + " FROM " + table + " WHERE " + fields[0].getRealName() + "=? LIMIT 1");)
					{
						query.setString(1, data.getPrimaryKey());
						try (final ResultSet result = query.executeQuery())
						{
							// EDIT CaseFix siehe FlatDB
							if (result.next())
							{
								data.setField(index, fields[index].getSerializer().fromDatabase(result, fields[index].getRealName()));
								datas.put(data.getPrimaryKey(), data);
							}
						}
						return true;
					}
					catch (final SQLException e)
					{
						e.printStackTrace();
						return false;
					}
					finally
					{
						connections.releaseConnection(connection);
					}
				}
				return true;
			}
		};
	}

	@Override
	public DatabaseTaskInterface saveTask(final A data)
	{
		return new BasicDatabaseTask<A>(data)
		{

			@Override
			public boolean run()
			{
				final Connection connection = connections.getConnection();
				if (connection == null)
					return false;
				try
				{
					String sql = "UPDATE " + table + " SET ";
					if (fields.length > 1)
						sql += fields[1].getRealName() + "=?";
					for (int i = 2; i < fields.length; i++)
						sql += "," + fields[i].getRealName() + "=?";
					sql += " WHERE " + fields[0].getRealName() + "=? LIMIT 1";
					try (final PreparedStatement query = connection.prepareStatement(sql))
					{
						query.setString(fields.length, data.getPrimaryKey());
						for (int i = 1; i < fields.length; i++)
							fields[i].getSerializer().toDatabase(query, i, data.getField(i));
						if (query.executeUpdate() == 0)
							insertInto(connection, data);
					}
					return true;
				}
				catch (final SQLException e)
				{
					e.printStackTrace();
					return false;
				}
				finally
				{
					connections.releaseConnection(connection);
				}
			}
		};
	}

	@Override
	public DatabaseTaskInterface saveTask(final A data, final int index)
	{
		return new BasicDatabaseTask<A>(data)
		{

			@Override
			public boolean run()
			{
				if (index > 1 && index < fields.length)
				{
					final Connection connection = connections.getConnection();
					if (connection == null)
						return false;
					try
					{
						final String sql = "UPDATE " + table + " SET " + fields[index].getRealName() + "=? WHERE " + fields[0].getRealName() + "=? LIMIT 1";
						try (final PreparedStatement query = connection.prepareStatement(sql))
						{
							query.setString(fields.length, data.getPrimaryKey());
							fields[index].getSerializer().toDatabase(query, 1, data.getField(index));
							if (query.executeUpdate() == 0)
								insertInto(connection, data);
						}
						return true;
					}
					catch (final SQLException e)
					{
						e.printStackTrace();
						return false;
					}
					finally
					{
						connections.releaseConnection(connection);
					}
				}
				return true;
			}
		};
	}

	protected void insertInto(final Connection connection, final A data) throws SQLException
	{
		String sql = "INSERT INTO " + table + " (" + fields[0].getRealName();
		String values = "?";
		for (int i = 1; i < fields.length; i++)
		{
			sql += "," + fields[i].getRealName();
			values += ",?";
		}
		sql += ") VALUES (" + values + ")";
		try (final PreparedStatement query = connection.prepareStatement(sql))
		{
			for (int i = 0; i < fields.length; i++)
				fields[i].getSerializer().toDatabase(query, i + 1, data.getField(i));
			query.executeUpdate();
		}
	}
}
