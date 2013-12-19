package de.st_ddt.crazyutil.databases;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import de.st_ddt.crazyutil.databases.datas.DatabaseDataInterface;
import de.st_ddt.crazyutil.databases.datas.DatabaseField;
import de.st_ddt.crazyutil.databases.tasks.DatabaseTaskInterface;
import de.st_ddt.crazyutil.databases.tasks.DatabaseTaskQueue;

public abstract class BasicDatabase<A extends DatabaseDataInterface, F extends DatabaseField<?>> implements DatabaseInterface<A>
{

	protected static int newDatabaseID = 0;
	protected final int databaseID = newDatabaseID++;
	protected final Map<String, A> datas = Collections.synchronizedMap(new TreeMap<String, A>(String.CASE_INSENSITIVE_ORDER));
	protected final DatabaseTaskQueue taskQueue = new DatabaseTaskQueue();
	protected final String databaseIdentifier;
	protected final Constructor<? extends A> newConstructor;
	protected final F[] fields;
	protected boolean active = true;
	protected boolean shutingDown = false;

	public BasicDatabase(final Class<? extends A> entryClass, final F[] fields)
	{
		super();
		databaseIdentifier = entryClass.getSimpleName() + "Database";
		Constructor<? extends A> constructor = null;
		try
		{
			constructor = entryClass.getConstructor(DatabaseInterface.class, String.class);
		}
		catch (final Exception e)
		{
			System.err.println("WARNING: Serious Bug detected, please report this! (newConstructor)");
			e.printStackTrace();
		}
		this.newConstructor = constructor;
		this.fields = fields;
		new WorkerThread().start();
	}

	@Override
	public final void addData(final A data)
	{
		if (data == null)
			return;
		datas.put(data.getPrimaryKey(), data);
		taskQueue.add(saveTask(data));
		if (shutingDown)
			flush();
	}

	protected A newData(final String key)
	{
		try
		{
			return newConstructor.newInstance(this, key);
		}
		catch (final Exception e)
		{
			System.err.println("Could not create new instance for key: " + key);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public final A createData(final String key)
	{
		final A data = newData(key);
		addData(data);
		return data;
	}

	@Override
	public boolean hasRawKey(final String key)
	{
		return getAllRawKeys().contains(key);
	}

	@Override
	public abstract Collection<String> getAllRawKeys();

	@Override
	public final void loadData(final String key)
	{
		final A data = datas.get(key);
		if (data == null)
			taskQueue.add(loadTask(newData(key)));
		else
			taskQueue.add(loadTask(data));
	}

	@Override
	public final void loadAllData()
	{
		for (final String key : getAllRawKeys())
			loadData(key);
	}

	@Override
	public final A getData(final String key)
	{
		processImportantTasks(key);
		return datas.get(key);
	}

	@Override
	public A getOrLoadData(final String key)
	{
		final A data = getData(key);
		if (data == null)
		{
			loadData(key);
			return getData(key);
		}
		else
			return data;
	}

	@Override
	public A getOrCreateData(final String key)
	{
		final A data = getData(key);
		if (data == null)
			return createData(key);
		else
			return data;
	}

	@Override
	public A getOrLoadOrCreateData(final String key)
	{
		final A data = getOrLoadData(key);
		if (data == null)
			return createData(key);
		return data;
	}

	@Override
	public final Map<String, A> getAllData()
	{
		processAllTasks();
		return datas;
	}

	@Override
	public final Set<String> getAllKeys()
	{
		return datas.keySet();
	}

	@Override
	public final int size()
	{
		return datas.size();
	}

	@Override
	public final void unloadData(final String key)
	{
		processTasks(key);
		datas.remove(key);
	}

	@Override
	public final void unloadAllData()
	{
		processAllTasks();
		datas.clear();
	}

	@Override
	public A removeData(final String key)
	{
		taskQueue.dropTasks(key);
		return datas.remove(key);
	}

	@Override
	public void removeAllData(final String key)
	{
		taskQueue.dropAllTasks();
		datas.clear();
	}

	@Override
	public void flush()
	{
		processAllTasks();
	}

	@Override
	public final boolean isActive()
	{
		return active && !shutingDown;
	}

	@Override
	public boolean isShutingDown()
	{
		return shutingDown;
	}

	@Override
	public void shutdown()
	{
		processAllTasks();
		shutingDown = true;
		taskQueue.shutdown();
		flush();
	}

	@Override
	public final void queueTask(final DatabaseTaskInterface task)
	{
		if (shutingDown)
			task.run();
		else
			taskQueue.add(task);
	}

	private void processQueue(final Queue<DatabaseTaskInterface> queue)
	{
		while (!queue.isEmpty())
			queue.poll().run();
	}

	protected void processTasks(final String key)
	{
		synchronized (taskQueue)
		{
			final Queue<DatabaseTaskInterface> queue = taskQueue.getTaskQueue(key);
			if (queue != null)
				synchronized (queue)
				{
					processQueue(queue);
				}
		}
	}

	protected void processImportantTasks(final String key)
	{
		synchronized (taskQueue)
		{
			final Queue<DatabaseTaskInterface> queue = taskQueue.getTaskQueue(key);
			if (queue != null)
				synchronized (queue)
				{
					final List<DatabaseTaskInterface> tasks = new ArrayList<DatabaseTaskInterface>(queue);
					queue.clear();
					for (final DatabaseTaskInterface task : tasks)
					{
						queue.add(task);
						if (task.isImportantTask())
							processQueue(queue);
					}
				}
		}
	}

	@Override
	public void processAllTasks()
	{
		DatabaseTaskInterface task;
		while ((task = taskQueue.poll()) != null)
			task.run();
	}

	@Override
	public abstract DatabaseTaskInterface loadTask(A data);

	@Override
	public abstract DatabaseTaskInterface loadTask(A data, int index);

	@Override
	public abstract DatabaseTaskInterface saveTask(A data);

	@Override
	public abstract DatabaseTaskInterface saveTask(A data, int index);

	private class WorkerThread extends Thread
	{

		public WorkerThread()
		{
			super(databaseIdentifier + "_" + databaseID + "_Worker");
		}

		@Override
		public void run()
		{
			while (!shutingDown)
				if (active)
				{
					final DatabaseTaskInterface task = taskQueue.poll();
					if (task != null)
						if (!task.run())
							taskQueue.addPrimaryTask(task);
				}
		}
	}
}
