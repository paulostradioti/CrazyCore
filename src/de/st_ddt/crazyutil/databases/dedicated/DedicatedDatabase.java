package de.st_ddt.crazyutil.databases.dedicated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import de.st_ddt.crazyutil.databases.BasicDatabase;
import de.st_ddt.crazyutil.databases.datas.DatabaseDataInterface;
import de.st_ddt.crazyutil.databases.flat.FlatDatabaseField;
import de.st_ddt.crazyutil.databases.tasks.BasicDatabaseTask;
import de.st_ddt.crazyutil.databases.tasks.DatabaseTaskInterface;

public class DedicatedDatabase<A extends DatabaseDataInterface> extends BasicDatabase<A, FlatDatabaseField>
{

	protected final String host;
	protected final int port;
	protected final String auth;
	protected final Object socketLock = new Object();
	protected Socket socket;
	protected BufferedReader reader;
	protected PrintWriter writer;

	public DedicatedDatabase(final Class<A> entryClass, final FlatDatabaseField[] fields)
	{
		super(entryClass, fields);
		host = "localhost";
		port = 14578;
		auth = "USERNAME:PASSWORD";
		connect();
		new PingThread().start();
	}

	protected boolean connect()
	{
		synchronized (socketLock)
		{
			if (socket == null || !socket.isConnected() || socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown())
				try
				{
					disconnect();
					socket = new Socket(host, port);
					socket.setReuseAddress(true);
					socket.setSoTimeout(5000);
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer = new PrintWriter(socket.getOutputStream(), true);
					writer.println(auth);
					final Queue<String> messages = readMessages();
					final String line = messages.poll();
					if (line.equals(Messages.ERROR_AUTH))
					{
						System.err.println("Could not connect to host: " + line);
						disconnect();
						return false;
					}
				}
				catch (final UnknownHostException e)
				{
					System.err.println("Could not resolve hostname!");
					return false;
				}
				catch (final IOException e)
				{
					disconnect();
					return false;
				}
		}
		active = socket.isConnected();
		return active;
	}

	protected void disconnect()
	{
		active = false;
		if (socket == null)
			return;
		synchronized (socketLock)
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
			writer = null;
			try
			{
				reader.close();
			}
			catch (final IOException e)
			{}
			reader = null;
		}
	}

	@Override
	public boolean hasRawKey(final String key)
	{
		try
		{
			final Queue<String> messages = readResponse("Q_HasRawKey", key);
			if (messages == null)
				return false;
			String line = messages.poll();
			if (!line.equals("A_HasRawKey"))
				return false;
			line = messages.poll();
			if (!line.equalsIgnoreCase(key))
				return false;
			line = messages.poll();
			return line.equals("true");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Collection<String> getAllRawKeys()
	{
		final Set<String> res = new HashSet<String>();
		try
		{
			final Queue<String> messages = readResponse("Q_RawKeys");
			if (messages == null)
				return res;
			final String line = messages.poll();
			if (!line.equals("A_RawKeys"))
				return res;
			res.addAll(messages);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
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
				try
				{
					final Queue<String> messages = readResponse("Q_LoadFull", data.getPrimaryKey());
					if (messages == null)
						return false;
					String line = messages.poll();
					if (!line.equals("A_LoadFull"))
					{
						if (line.startsWith("E_"))
						{
							System.out.println("Could not load " + data.getPrimaryKey());
							System.err.println(line);
							for (final String message : messages)
								System.err.println(message);
						}
						return false;
					}
					line = messages.poll();
					A data;
					if (getPrimaryKey().equals(line))
						data = this.data;
					else if (getPrimaryKey().equalsIgnoreCase(line))
						data = newData(line);
					else
						return true;
					int index = 1;
					for (final String message : messages)
					{
						data.setField(index, fields[index].getSerializer().fromDatabase(message));
						index++;
					}
					datas.put(data.getPrimaryKey(), data);
					return true;
				}
				catch (final SocketException e)
				{
					disconnect();
					return false;
				}
				catch (final IOException e)
				{
					e.printStackTrace();
					return false;
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
				if (index > 1 && index < fields.length)
					try
					{
						final Queue<String> messages = readResponse("Q_LoadIndex", data.getPrimaryKey(), Integer.toString(index));
						if (messages == null)
							return false;
						String line = messages.poll();
						if (!line.equals("A_LoadIndex"))
						{
							if (line.startsWith("E_"))
							{
								System.out.println("Could not load " + data.getPrimaryKey());
								System.err.println(line);
								for (final String message : messages)
									System.err.println(message);
							}
							return false;
						}
						line = messages.poll();
						A data;
						if (getPrimaryKey().equals(line))
							data = this.data;
						else if (getPrimaryKey().equalsIgnoreCase(line))
						{
							data = newData(line);
							for (int i = 0; i < fields.length; i++)
								data.setField(i, data.getField(i));
						}
						else
							return true;
						line = messages.poll();
						data.setField(index, fields[index].getSerializer().fromDatabase(line));
						datas.put(data.getPrimaryKey(), data);
						return true;
					}
					catch (final SocketException e)
					{
						disconnect();
						return false;
					}
					catch (final IOException e)
					{
						e.printStackTrace();
						return false;
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
				try
				{
					final String[] args = new String[fields.length];
					for (int i = 0; i < fields.length; i++)
						args[i] = fields[i].getSerializer().toDatabase(data.getField(i));
					Queue<String> messages;
					synchronized (socketLock)
					{
						writer.println("Q_SaveFull");
						messages = readResponse(args);
					}
					if (messages == null)
						return false;
					String line = messages.poll();
					if (!line.equals("A_SaveFull"))
					{
						if (line.startsWith("E_"))
						{
							System.out.println("Could not save " + data.getPrimaryKey());
							System.err.println(line);
							for (final String message : messages)
								System.err.println(message);
						}
						return false;
					}
					line = messages.poll();
					return data.getPrimaryKey().equalsIgnoreCase(line);
				}
				catch (final IOException e)
				{
					e.printStackTrace();
					return false;
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
					try
					{
						final Queue<String> messages = readResponse("Q_SaveIndex", data.getPrimaryKey(), Integer.toString(index), fields[index].getSerializer().toDatabase(data.getField(index)));
						if (messages == null)
							return false;
						String line = messages.poll();
						if (!line.equals("A_SaveIndex"))
						{
							if (line.startsWith("E_"))
							{
								System.out.println("Could not save " + data.getPrimaryKey());
								System.err.println(line);
								for (final String message : messages)
									System.err.println(message);
							}
							return false;
						}
						line = messages.poll();
						return data.getPrimaryKey().equalsIgnoreCase(line);
					}
					catch (final IOException e)
					{
						e.printStackTrace();
						return false;
					}
				return true;
			}
		};
	}

	protected Queue<String> readMessages() throws IOException
	{
		synchronized (socketLock)
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
	}

	protected Queue<String> readResponse(final String... query) throws IOException
	{
		synchronized (socketLock)
		{
			if (!connect())
				return null;
			for (final String msg : query)
				writer.println(msg);
			writer.println(Messages.MESSAGE_END);
			return readMessages();
		}
	}

	private class PingThread extends Thread
	{

		private final static int PINGINTERVAL = 5000;

		public PingThread()
		{
			super(databaseIdentifier + "_" + databaseID + "_Ping");
		}

		@Override
		public void run()
		{
			while (!shutingDown)
			{
				synchronized (socketLock)
				{
					if (connect())
						writer.println(Messages.MESSAGE_PING);
				}
				try
				{
					Thread.sleep(PINGINTERVAL);
				}
				catch (final InterruptedException e)
				{}
			}
		}
	}
}
