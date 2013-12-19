package de.st_ddt.crazyutil.databases.sql;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.paramitrisable.NumberParamitrisable;
import de.st_ddt.crazyutil.paramitrisable.Paramitrisable;
import de.st_ddt.crazyutil.paramitrisable.StringParamitrisable;

public abstract class AbstractSQLConnector implements SQLConnectorInterface
{

	protected final String host;
	protected final String port;
	protected final String dbName;
	protected final String user;
	protected final String password;

	public AbstractSQLConnector(final String host, final String port, final String database, final String user, final String password)
	{
		super();
		this.host = host;
		this.port = port;
		this.dbName = database;
		this.user = user;
		this.password = password;
	}

	@SuppressWarnings("unchecked")
	public AbstractSQLConnector(final Map<String, ? extends Paramitrisable> params)
	{
		super();
		final StringParamitrisable hostParam = (StringParamitrisable) params.get("host");
		this.host = hostParam.getValue();
		final Paramitrisable portParam = params.get("port");
		if (portParam instanceof NumberParamitrisable<?>)
			this.port = Integer.toString(((NumberParamitrisable<? extends Number>) portParam).getValue().intValue());
		else
			this.port = ((StringParamitrisable) portParam).getValue();
		final StringParamitrisable databaseParam = (StringParamitrisable) params.get("dbname");
		this.dbName = databaseParam.getValue();
		final StringParamitrisable userParam = (StringParamitrisable) params.get("user");
		this.user = userParam.getValue();
		final StringParamitrisable passwordParam = (StringParamitrisable) params.get("user");
		this.password = passwordParam.getValue();
	}

	public AbstractSQLConnector(final ConfigurationSection config, final String defaultHost, final String defaultPort, final String defaultDatabase, final String defaultUser, final String defaultPassword)
	{
		super();
		if (config == null)
		{
			this.host = defaultHost;
			this.port = defaultPort;
			this.dbName = defaultDatabase;
			this.user = defaultUser;
			this.password = defaultPassword;
		}
		else
		{
			this.host = config.getString("host", defaultHost);
			this.port = config.getString("port", defaultPort);
			this.dbName = config.getString("dbname", defaultDatabase);
			this.user = config.getString("user", defaultUser);
			this.password = config.getString("password", defaultPassword);
		}
	}

	public final String getHost()
	{
		return host;
	}

	public final String getPort()
	{
		return port;
	}

	public String getDatabaseName()
	{
		return dbName;
	}

	public final String getUser()
	{
		return user;
	}

	public final String getPassword()
	{
		return password;
	}

	@Override
	public void save(final ConfigurationSection config, final String path)
	{
		config.set(path + "host", host);
		config.set(path + "port", port);
		config.set(path + "dbname", dbName);
		config.set(path + "user", user);
		config.set(path + "password", password);
	}
}
