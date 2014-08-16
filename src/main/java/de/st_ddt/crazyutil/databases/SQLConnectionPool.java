package de.st_ddt.crazyutil.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The SQLConnectionPool
 */
public abstract class SQLConnectionPool
{

	private final static Random RANDOM = new Random();
	private final static float REFRESHQUOTA = 0.05F;
	private final Queue<Connection> idleConenctions = new LinkedList<Connection>();
	private final Lock lock = new ReentrantLock();
	private final SQLConnection mainConnection;
	private final int maxConnections;

	public SQLConnectionPool(final SQLConnection sqlConnection)
	{
		this(sqlConnection, 10);
	}

	public SQLConnectionPool(final SQLConnection sqlConnection, final int maxConnections)
	{
		super();
		this.mainConnection = sqlConnection;
		this.maxConnections = maxConnections;
	}

	public SQLConnection getMainConnection()
	{
		return mainConnection;
	}

	public void reset()
	{
		lock.lock();
		final Iterator<Connection> it = idleConenctions.iterator();
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
		lock.unlock();
	}

	public Connection getConnection()
	{
		lock.lock();
		try
		{
			final Connection connection = idleConenctions.poll();
			if (connection == null)
				return mainConnection.openConnection();
			else
				try
			{
					if (connection.isValid(1))
						return connection;
					else
						connection.close();
			}
			catch (final SQLException e)
			{}
			return getConnection();
		}
		catch (final SQLException e)
		{
			throw new IllegalStateException("Could not open or get an active connetion to the database!", e);
		}
		finally
		{
			lock.unlock();
		}
	}

	public abstract boolean isValid(Connection connection) throws SQLException;

	public void releaseConnection(final Connection connection)
	{
		if (connection == null)
			return;
		lock.lock();
		try
		{
			if (idleConenctions.size() > maxConnections || RANDOM.nextFloat() <= REFRESHQUOTA)
				connection.close();
			else if (!connection.isClosed())
				idleConenctions.add(connection);
		}
		catch (final SQLException e)
		{
			try
			{
				connection.close();
			}
			catch (final SQLException e1)
			{}
		}
		finally
		{
			lock.unlock();
		}
	}
}
