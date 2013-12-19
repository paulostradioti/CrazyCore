package de.st_ddt.crazyutil.databases.tasks;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class DatabaseTaskQueue
{

	protected final Queue<DatabaseTaskInterface> primaryTasks = new LinkedList<DatabaseTaskInterface>();
	protected final Map<String, Queue<DatabaseTaskInterface>> tasks = Collections.synchronizedMap(new LinkedHashMap<String, Queue<DatabaseTaskInterface>>());
	protected boolean shutingDown;

	public void add(final DatabaseTaskInterface task)
	{
		if (task == null)
			return;
		synchronized (this)
		{
			Queue<DatabaseTaskInterface> queue = getTaskQueue(task.getPrimaryKey());
			if (queue == null)
			{
				queue = new LinkedList<DatabaseTaskInterface>();
				queue.add(task);
				tasks.put(task.getPrimaryKey().toLowerCase(), queue);
			}
			else
				queue.add(task);
			notify();
		}
	}

	public void add(final String key, final Collection<DatabaseTaskInterface> newTasks)
	{
		if (newTasks == null || newTasks.isEmpty())
			return;
		synchronized (this)
		{
			Queue<DatabaseTaskInterface> queue = getTaskQueue(key);
			if (queue == null)
			{
				queue = new LinkedList<DatabaseTaskInterface>(newTasks);
				tasks.put(key.toLowerCase(), queue);
			}
			else
				queue.addAll(newTasks);
			notify();
		}
	}

	public void addPrimaryTask(final DatabaseTaskInterface task)
	{
		if (task == null)
			return;
		synchronized (this)
		{
			primaryTasks.add(task);
			notify();
		}
	}

	public void addPrimaryTask(final String key, final Collection<DatabaseTaskInterface> newTasks)
	{
		if (newTasks == null || newTasks.isEmpty())
			return;
		synchronized (this)
		{
			primaryTasks.addAll(newTasks);
			notify();
		}
	}

	public synchronized DatabaseTaskInterface poll()
	{
		if (!primaryTasks.isEmpty())
			return primaryTasks.poll();
		final Iterator<Queue<DatabaseTaskInterface>> it = tasks.values().iterator();
		while (it.hasNext())
		{
			final Queue<DatabaseTaskInterface> queue = it.next();
			try
			{
				if (queue.isEmpty())
					continue;
				else
					return queue.poll();
			}
			finally
			{
				if (queue.isEmpty())
					it.remove();
			}
		}
		return null;
	}

	public synchronized DatabaseTaskInterface pollOrWait()
	{
		while (hasTasks())
			try
			{
				if (shutingDown)
					return null;
				else
					wait();
			}
			catch (final InterruptedException e)
			{
				return null;
			}
		return poll();
	}

	public boolean hasTasks()
	{
		return !primaryTasks.isEmpty() || !tasks.isEmpty();
	}

	public synchronized void shutdown()
	{
		shutingDown = true;
		notify();
	}

	public Queue<DatabaseTaskInterface> getTaskQueue(final String key)
	{
		return tasks.get(key.toLowerCase());
	}

	public Queue<DatabaseTaskInterface> dropTasks(final String key)
	{
		return tasks.remove(key.toLowerCase());
	}

	public void dropAllTasks()
	{
		tasks.clear();
	}
}
