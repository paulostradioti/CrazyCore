package de.st_ddt.crazyutil.compatibility;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;

import de.st_ddt.crazyutil.resources.ResourceHelper;

public class CompatibilityLoader
{

	private final static String implementation;
	private final static String version;
	static
	{
		final Server server = Bukkit.getServer();
		final Class<? extends Server> serverClass = server.getClass();
		final String serverClassName = serverClass.getName();
		final String[] serverClassNameParts = serverClassName.split("\\.");
		if (serverClassNameParts.length >= 5 && serverClassNameParts[2].equals("craftbukkit"))
		{
			implementation = "craftbukkit";
			version = serverClassNameParts[3];
			System.out.println("[CrazyCompatibility] Implementation: " + implementation + "@" + version + " found.");
		}
		else
		{
			implementation = null;
			version = null;
			System.err.println("[CrazyCompatibility] No compatible implementation detected for class: " + serverClassName);
		}
	}

	public static boolean loadCompatibilityProvider(final Plugin plugin)
	{
		return loadCompatibilityProvider(plugin, plugin.getClass().getPackage().getName() + ".", "");
	}

	public static boolean loadCompatibilityProvider(final Plugin plugin, final String packagePrefix, final String packageSuffix)
	{
		return loadCompatibilityProvider(plugin, packagePrefix, packageSuffix, "");
	}

	public static boolean loadCompatibilityProvider(final Plugin plugin, final String packagePrefix, final String packageSuffix, final String fileSuffix)
	{
		if (implementation == null || version == null)
			return false;
		final File target = new File(plugin.getDataFolder(), "/compatibility/" + implementation + "/" + version + fileSuffix + ".jar");
		if (target.exists())
			ResourceHelper.saveResource(plugin, "/compatibility/" + implementation + "/" + version + fileSuffix + ".jar", target);
		try
		{
			loadClassContainer(plugin, target);
			Class.forName(packagePrefix + implementation + "." + version + packageSuffix + ".CompatibilityProvider", true, plugin.getClass().getClassLoader());
			System.err.println("[" + plugin.getName() + "] Loaded compatibility provider.");
			return true;
		}
		catch (final Throwable t)
		{
			System.err.println("[" + plugin.getName() + "] Error loading compatibility provider!");
			System.err.println(t.getMessage());
			return false;
		}
	}

	protected static void loadClassContainer(final Plugin plugin, final File file) throws MalformedURLException
	{
		if (!file.exists())
			return;
		final ClassLoader loader = plugin.getClass().getClassLoader();
		final URL url = file.toURI().toURL();
		((PluginClassLoader) loader).addURL(url);
	}

	private CompatibilityLoader()
	{
	}
}
