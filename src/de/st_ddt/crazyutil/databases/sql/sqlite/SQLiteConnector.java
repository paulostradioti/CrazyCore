package de.st_ddt.crazyutil.databases.sql.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.databases.sql.AbstractSQLConnector;

public class SQLiteConnector extends AbstractSQLConnector
{

	public SQLiteConnector(final ConfigurationSection config, final String defaultHost, final String defaultPort, final String defaultDatabase, final String defaultUser, final String defaultPassword) throws Exception
	{
		super(config, defaultHost, defaultPort, defaultDatabase, defaultUser, defaultPassword);
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch (final ClassNotFoundException e)
		{
			throw new Exception("MySQL-Driver not found!", e);
		}
	}

	public SQLiteConnector(final String host, final String port, final String database, final String user, final String password) throws Exception
	{
		super(host, port, database, user, password);
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch (final ClassNotFoundException e)
		{
			throw new Exception("MySQL-Driver not found!", e);
		}
	}

	@Override
	public Connection openConnection() throws SQLException
	{
		try
		{
			// TODO Setup SQLite Properly!
			return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbName, user, password);
		}
		catch (final SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Connection failed");
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Please check:");
			Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + " 1) Is your database running/online?");
			Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + " 2) Did you made any mistakes with your server access data?");
			Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + " 3) Can you connect to your database from this server?");
			throw e;
		}
	}

	@Override
	public boolean isValid(final Connection connection) throws SQLException
	{
		return connection.isValid(1);
	}
}
