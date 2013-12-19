package de.st_ddt.crazyutil.databases.dedicated;

class Messages
{

	final static String MESSAGE_END = "\0@End";
	final static String MESSAGE_PING = "\0@Ping";
	final static String ERROR_AUTH = "E_AUTHFAILED\nPassphrase rejected!";
	final static String ERROR_EXECUTE = "E_EXECUTIONFAILED\nAn error occured while executing that command.";
	final static String[] ERROR_PROTOCOL = new String[] { "E_PROTOCOLUNKNOW", "This protocol is unknown!", "Maybe you have to update the client/server." };

	private Messages()
	{
	}
}
