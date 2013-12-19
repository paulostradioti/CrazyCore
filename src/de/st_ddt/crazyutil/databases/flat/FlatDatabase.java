package de.st_ddt.crazyutil.databases.flat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import de.st_ddt.crazyutil.databases.BasicDatabase;
import de.st_ddt.crazyutil.databases.datas.DatabaseDataInterface;
import de.st_ddt.crazyutil.databases.tasks.BasicDatabaseTask;
import de.st_ddt.crazyutil.databases.tasks.DatabaseTaskInterface;

public class FlatDatabase<A extends DatabaseDataInterface> extends BasicDatabase<A, FlatDatabaseField>
{

	protected final static String DATA_SEPARATOR = "|";
	protected final static Pattern DATA_SEPARATOR_PATTERN = Pattern.compile("\\|");
	protected final static Charset CHARSET = Charset.forName("UTF-8");
	protected final Map<String, String> rawDatas = Collections.synchronizedMap(new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER));
	protected final File file;
	protected final File recoveryFile;
	protected boolean changed = false;

	public FlatDatabase(final Class<? extends A> entryClass, final FlatDatabaseField[] fields, final File file) throws Exception
	{
		super(entryClass, fields);
		this.file = file;
		this.recoveryFile = new File(file.getPath() + "_rec");
		file.mkdirs();
		try
		{
			loadFile(recoveryFile);
			loadFile(file);
		}
		catch (final Exception e)
		{
			throw new Exception("Could not read database file @" + databaseIdentifier + "! Please check if this application has read/write access for the database file.", e);
		}
		if (recoveryFile.exists() && !recoveryFile.canWrite())
			throw new Exception("Could not write recovery database file @" + databaseIdentifier + "! No write permission!");
		if (file.exists() && !file.canWrite())
			throw new Exception("Could not write database file @" + databaseIdentifier + "! No write permission!");
		new SaverThread().start();
	}

	protected void loadFile(final File file) throws IOException
	{
		if (!file.exists())
			return;
		synchronized (rawDatas)
		{
			try (InputStream stream = new FileInputStream(file);)
			{
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, CHARSET)))
				{
					String line = reader.readLine();
					if (line != null)
						while ((line = reader.readLine()) != null)
						{
							final String[] split = DATA_SEPARATOR_PATTERN.split(line, 2);
							rawDatas.put(split[0], line);
						}
				}
			}
		}
	}

	protected void saveFile()
	{
		synchronized (file)
		{
			if (!recoveryFile.exists())
				if (file.exists())
					file.renameTo(recoveryFile);
			synchronized (rawDatas)
			{
				try (FileOutputStream stream = new FileOutputStream(file))
				{
					try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, CHARSET)))
					{
						changed = false;
						// Header
						writer.write('#');
						for (final FlatDatabaseField field : fields)
						{
							writer.write(field.getName());
							writer.write(DATA_SEPARATOR);
						}
						writer.write(databaseIdentifier);
						// Values
						for (final String entry : rawDatas.values())
						{
							writer.write('\n');
							writer.write(entry);
						}
						writer.flush();
						if (file.exists())
							recoveryFile.delete();
						active = true;
					}
				}
				catch (final IOException e)
				{
					active = false;
					System.out.println("Could not create database file (" + file.getPath() + ")");
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Collection<String> getAllRawKeys()
	{
		return rawDatas.keySet();
	}

	@Override
	public A removeData(final String key)
	{
		final A data = super.removeData(key);
		rawDatas.remove(key);
		markChanged();
		return data;
	}

	@Override
	public void removeAllData(final String key)
	{
		super.removeAllData(key);
		rawDatas.clear();
		markChanged();
	}

	@Override
	public void flush()
	{
		super.flush();
		saveFile();
	}

	public void markChanged()
	{
		changed = true;
	}

	@Override
	public DatabaseTaskInterface loadTask(final A data)
	{
		return new BasicDatabaseTask<A>(data, true)
		{

			@Override
			public boolean run()
			{
				final String raw = rawDatas.get(data.getPrimaryKey());
				if (raw == null)
					return true;
				final String[] split = DATA_SEPARATOR_PATTERN.split(raw, -1);
				for (int index = 1; index < Math.min(split.length, fields.length); index++)
					data.setField(index, fields[index].getSerializer().fromDatabase(split[index]));
				datas.put(data.getPrimaryKey(), data);
				return true;
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
				final String raw = rawDatas.get(data.getPrimaryKey());
				if (raw == null)
					return true;
				final String[] split = DATA_SEPARATOR_PATTERN.split(raw, -1);
				if (index > 1 && index < Math.min(split.length, fields.length))
					data.setField(index, fields[index].getSerializer().fromDatabase(split[index]));
				datas.put(data.getPrimaryKey(), data);
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
				final StringBuilder builder = new StringBuilder(data.getPrimaryKey());
				for (int i = 1; i < fields.length; i++)
				{
					builder.append(DATA_SEPARATOR);
					builder.append(fields[i].getSerializer().toDatabase(data.getField(i)));
				}
				rawDatas.put(data.getPrimaryKey(), builder.toString());
				markChanged();
				return active;
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
				final String raw = rawDatas.get(data.getPrimaryKey());
				final StringBuilder builder = new StringBuilder(data.getPrimaryKey());
				if (raw == null)
				{
					final String[] split = DATA_SEPARATOR_PATTERN.split(raw, -1);
					if (index > 1 && index < Math.min(split.length, fields.length))
						split[index] = fields[index].getSerializer().toDatabase(data.getField(index));
					for (int i = 1; i < fields.length; i++)
					{
						builder.append(DATA_SEPARATOR);
						builder.append(split[i]);
					}
				}
				else
					for (int i = 1; i < fields.length; i++)
					{
						builder.append(DATA_SEPARATOR);
						builder.append(fields[i].getSerializer().toDatabase(data.getField(i)));
					}
				rawDatas.put(data.getPrimaryKey(), builder.toString());
				markChanged();
				return active;
			}
		};
	}

	private class SaverThread extends Thread
	{

		private final static long saveInterval = 1000;

		public SaverThread()
		{
			super(databaseIdentifier + "_" + databaseID + "_Flusher");
		}

		@Override
		public void run()
		{
			while (!shutingDown)
			{
				if (changed)
					saveFile();
				try
				{
					Thread.sleep(saveInterval);
				}
				catch (final InterruptedException e)
				{}
			}
		}
	}
}
