package de.st_ddt.crazyutil.databases.dedicated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import de.st_ddt.crazyutil.databases.DatabaseInterface;
import de.st_ddt.crazyutil.databases.datas.DatabaseDataInterface;
import de.st_ddt.crazyutil.databases.flat.FlatDatabaseField;
import de.st_ddt.crazyutil.databases.flat.serializer.StringFlatDatabaseSerializer;

public class DedicatedDatabaseServer<A extends DatabaseDataInterface> implements Runnable
{

	protected final static Pattern AUTH_SEPARATOR_PATTERN = Pattern.compile("\\|");
	protected final ExecutorService threadPool = Executors.newCachedThreadPool();
	protected final Map<String, SocketWorker> connectionsByName = Collections.synchronizedSortedMap(new TreeMap<String, SocketWorker>());
	protected final Map<String, SocketWorker> connectionsByIP = Collections.synchronizedSortedMap(new TreeMap<String, SocketWorker>());
	protected final DatabaseInterface<A> database;
	protected final FlatDatabaseField[] fields;
	protected final int port;
	protected final Set<String> validAuths;
	protected final ServerSocket socket;
	protected String[] motD = new String[] { "Connection established." };
	protected boolean shutingDown = false;

	public DedicatedDatabaseServer(final DatabaseInterface<A> database, final FlatDatabaseField[] fields, final int port, final Set<String> validAuths) throws IOException
	{
		super();
		this.database = database;
		this.fields = fields;
		this.port = port;
		this.validAuths = validAuths;
		this.socket = new ServerSocket(port);
	}

	@Override
	public void run()
	{
		while (!shutingDown)
			try
			{
				threadPool.execute(new SocketWorker(socket.accept()));
			}
			catch (final IOException e)
			{}
	}

	public Map<String, SocketWorker> getConnectionsByName()
	{
		return connectionsByName;
	}

	public Map<String, SocketWorker> getConnectionsByIP()
	{
		return connectionsByIP;
	}

	public DatabaseInterface<A> getDatabase()
	{
		return database;
	}

	public void shutdown()
	{
		shutingDown = true;
		threadPool.shutdown();
		try
		{
			socket.close();
		}
		catch (final IOException e)
		{}
		try
		{
			threadPool.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e)
		{}
	}

	private class SocketWorker implements Runnable
	{

		private final static int SOCKET_TIMEOUT = 5000;
		protected final Socket socket;
		protected final BufferedReader reader;
		protected final PrintWriter writer;

		public SocketWorker(final Socket socket) throws IOException
		{
			super();
			if (socket == null)
				throw new IllegalArgumentException("Socket cannot be null!");
			System.out.println("Connect");
			this.socket = socket;
			socket.setSoTimeout(SOCKET_TIMEOUT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
		}

		@Override
		public void run()
		{
			try
			{
				System.out.println("Connected");
				final String line = reader.readLine();
				if (validAuths.contains(line))
				{
					for (final String msg : motD)
						writer.println(msg);
					writer.println(Messages.MESSAGE_END);
				}
				else
				{
					writer.println(Messages.ERROR_AUTH);
					writer.println(Messages.MESSAGE_END);
					disconnect();
					return;
				}
				connectionsByIP.put(socket.getInetAddress().getHostAddress(), this);
				connectionsByName.put(AUTH_SEPARATOR_PATTERN.split(line)[0], this);
				while (!shutingDown && !socket.isClosed())
					try
					{
						final Queue<String> messages = readMessages();
						if (messages == null)
							break;
						synchronized (this)
						{
							for (final String msg : runMessage(messages))
								writer.println(msg);
							writer.println(Messages.MESSAGE_END);
						}
					}
					catch (final IOException e)
					{
						throw e;
					}
					catch (final Exception e)
					{
						writer.println(Messages.ERROR_EXECUTE);
						writer.println(Messages.MESSAGE_END);
					}
			}
			catch (final IOException e)
			{}
			finally
			{
				disconnect();
			}
		}

		public synchronized void disconnect()
		{
			if (!socket.isOutputShutdown())
				try
				{
					socket.shutdownOutput();
				}
				catch (final IOException e)
				{}
			if (!socket.isInputShutdown())
				try
				{
					socket.shutdownInput();
				}
				catch (final IOException e)
				{}
			try
			{
				socket.close();
			}
			catch (final IOException e)
			{}
			writer.close();
			try
			{
				reader.close();
			}
			catch (final IOException e)
			{}
		}

		protected synchronized Queue<String> readMessages() throws IOException
		{
			final Queue<String> res = new LinkedList<String>();
			while (true)
			{
				final String message = reader.readLine();
				if (message == null)
					break;
				else if (!message.startsWith("\0"))
					res.add(message);
				else if (message.equals(Messages.MESSAGE_END))
					break;
				else if (message.equals(Messages.MESSAGE_PING))
					continue;
				else
					System.err.println("Unknown message: " + message);
			}
			return res;
		}

		private String[] runMessage(final Queue<String> messages) throws IOException
		{
			final String line = messages.poll();
			if (line.startsWith("Q_"))
				return runQuery(line.substring(2), messages);
			else if (line.startsWith("C_"))
				return runCommand(line.substring(2), messages);
			else
				return Messages.ERROR_PROTOCOL;
		}

		private String[] runQuery(final String header, final Queue<String> messages)
		{
			if (header.startsWith("Save"))
			{
				if (header.equals("SaveFull"))
				{
					final String key = messages.poll();
					final A data = database.getOrLoadOrCreateData(key);
					int index = 1;
					for (final String entry : messages)
						if (index < fields.length)
						{
							data.setField(index, fields[index].getSerializer().fromDatabase(entry));
							index++;
						}
					database.addData(data);
					return new String[] { "A_SaveFull", data.getPrimaryKey() };
				}
				else if (header.equals("SaveIndex"))
				{
					final String key = messages.poll();
					final A data = database.getOrLoadOrCreateData(key);
					final int index = Integer.parseInt(messages.poll());
					if (index > 0 && index < fields.length)
						data.setField(index, fields[index].getSerializer().fromDatabase(messages.poll()));
					return new String[] { "A_SaveIndex", data.getPrimaryKey() };
				}
				else
					return Messages.ERROR_PROTOCOL;
			}
			else if (header.startsWith("Load"))
			{
				if (header.equals("LoadFull"))
				{
					final String key = messages.poll();
					final A data = database.getOrLoadOrCreateData(key);
					final String[] res = new String[fields.length + 1];
					res[0] = "A_LoadFull";
					res[1] = data.getPrimaryKey();
					for (int index = 1; index < fields.length; index++)
						res[index + 1] = fields[index].getSerializer().toDatabase(data.getField(index));
					return res;
				}
				else if (header.equals("LoadIndex"))
				{
					final String key = messages.poll();
					final A data = database.getOrLoadOrCreateData(key);
					final int index = Integer.parseInt(messages.poll());
					if (index > 1 && index < fields.length)
						return new String[] { "A_LoadIndex", data.getPrimaryKey(), fields[index].getSerializer().toDatabase(data.getField(index)) };
					else
						return new String[] { "A_LoadIndex", data.getPrimaryKey(), "" };
				}
				else if (header.equals("RawKeys"))
				{
					final Collection<String> keys = database.getAllRawKeys();
					final String[] res = new String[keys.size() + 1];
					res[0] = "A_RawKeys";
					int i = 1;
					for (final String key : keys)
						res[i++] = key;
					return res;
				}
				else
					return Messages.ERROR_PROTOCOL;
			}
			else
				return Messages.ERROR_PROTOCOL;
		}

		private String[] runCommand(final String header, final Queue<String> messages)
		{
			return Messages.ERROR_PROTOCOL;
		}
	}

	public static void main(final String[] args)
	{
		final Set<String> auths = new HashSet<String>();
		auths.add("USERNAME:PASSWORD");
		final FlatDatabaseField[] fields = new FlatDatabaseField[3];
		fields[0] = new FlatDatabaseField("Key", new StringFlatDatabaseSerializer());
		fields[1] = new FlatDatabaseField("Value1", new StringFlatDatabaseSerializer());
		fields[2] = new FlatDatabaseField("Value2", new StringFlatDatabaseSerializer());
		// final DedicatedDatabaseServer<DatabaseDataInterface> server = null;// new DedicatedDatabaseServer<DatabaseDataInterface>(database, fields, port, auths);
		System.out.println("Server online");
		// server.run();
		System.out.println("Server offline");
	}
}
