package de.st_ddt.crazyutil.databases.sql;

import java.sql.Connection;
import java.sql.SQLException;

import de.st_ddt.crazyutil.ConfigurationSaveable;

public interface SQLConnectorInterface extends ConfigurationSaveable
{

	public Connection openConnection() throws SQLException;

	public boolean isValid(Connection connection) throws SQLException;
}
