package de.st_ddt.crazyutil.compatibility;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import de.st_ddt.crazyutil.resources.ResourceHelper;

public class CompatibilityLoader
{

	private final static Method addUrl;
	private final static String implementation;
	private final static String version;
	static
	{
		final Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
		Method _addUrl = null;
		try
		{
			_addUrl = classLoaderClass.getDeclaredMethod("addURL", new Class[] { URL.class });
			_addUrl.setAccessible(true);
		}
		catch (final Throwable t)
		{}
		addUrl = _addUrl;
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
		if (!target.exists())
			ResourceHelper.saveResource(plugin, "/compatibility/" + implementation + "/" + version + fileSuffix + ".jar", target);
		final String className = packagePrefix + implementation + "." + version + packageSuffix + ".CompatibilityProvider";
		try
		{
			attachClassContainer(plugin, target);
			loadClass(plugin, className);
			System.out.println("[" + plugin.getName() + "] Loaded compatibility provider.");
			return true;
		}
		catch (final ClassNotFoundException e)
		{
			System.err.println("[" + plugin.getName() + "] Could not find matching compatibility provider!");
			return false;
		}
		catch (final Throwable t)
		{
			System.err.println("[" + plugin.getName() + "] Error loading compatibility provider!");
			System.err.println("[" + plugin.getName() + "] WARNING: Serious Bug detected, please report this!");
			System.err.println("CompatibilityProvider: " + className);
			t.printStackTrace();
			return false;
		}
	}

	protected static void attachClassContainer(final Plugin plugin, final File file) throws Exception
	{
		if (!file.exists())
			return;
		final ClassLoader classLoader = plugin.getClass().getClassLoader();
		final URL url = file.toURI().toURL();
		if (addUrl == null)
			throw new IllegalStateException("URLClassLoader.addURL method not found!");
		else
			addUrl.invoke(classLoader, url);
	}

	protected static void loadClass(final Plugin plugin, final String className) throws ClassNotFoundException
	{
		Class.forName(className, true, plugin.getClass().getClassLoader());
	}

	private CompatibilityLoader()
	{
	}
}
