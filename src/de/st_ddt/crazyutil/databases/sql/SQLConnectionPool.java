package de.st_ddt.crazyutil.databases.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLConnectionPool
{

	protected final SQLConnectorInterface connector;
	protected final Lock lock = new ReentrantLock();
	protected final Queue<Connection> idleConnections = new LinkedList<Connection>();
	protected final int maxConnections;
	protected int busyConnections = 0;

	public SQLConnectionPool(final SQLConnectorInterface connector)
	{
		this(connector, 10);
	}

	public SQLConnectionPool(final SQLConnectorInterface connector, final int maxConnections)
	{
		super();
		this.connector = connector;
		this.maxConnections = maxConnections;
	}

	public final SQLConnectorInterface getConnector()
	{
		return connector;
	}

	public final int getMaxConnections()
	{
		return maxConnections;
	}

	public final int getBusyConnections()
	{
		return busyConnections;
	}

	public void reset()
	{
		lock.lock();
		final Iterator<Connection> it = idleConnections.iterator();
		while (it.hasNext())
			try
			{
				it.next().close();
			}
			catch (final SQLException e)
			{}
			finally
			{
				it.remove();
			}
		busyConnections = 0;
		lock.unlock();
	}

	public Connection getConnection()
	{
		lock.lock();
		try
		{
			final Connection connection = idleConnections.poll();
			if (connection == null)
				if (busyConnections >= maxConnections)
					return null;
				else
				{
					busyConnections++;
					return connector.openConnection();
				}
			else if (connector.isValid(connection))
				return connection;
			else
			{
				connection.close();
				return getConnection();
			}
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			lock.unlock();
		}
	}

	public void releaseConnection(final Connection connection)
	{
		if (connection == null)
			return;
		lock.lock();
		try
		{
			if (busyConnections == 0)
				connection.close();
			else
			{
				busyConnections--;
				if (!connection.isClosed())
					idleConnections.add(connection);
			}
		}
		catch (final SQLException e)
		{}
		finally
		{
			lock.unlock();
		}
	}
}
