package de.st_ddt.crazyutil.compatibility;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import de.st_ddt.crazyplugin.CrazyPluginInterface;
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
		if (checkUpdated(plugin))
			deleteCompatibilityProviders(plugin, fileSuffix);
		final String compatibilityPath = getCompatibilityPath(fileSuffix);
		final File target = new File(plugin.getDataFolder(), compatibilityPath);
		if (!target.exists())
			if (!ResourceHelper.saveResource(plugin, '/' + compatibilityPath, target))
			{
				System.err.println("[" + plugin.getName() + "] Could not find matching compatibility package!");
				System.err.println("[" + plugin.getName() + "] Please wait for it to become available!");
				final String websiteURL = plugin.getDescription().getWebsite();
				if (websiteURL != null)
					System.err.println("[" + plugin.getName() + "] Website: " + websiteURL);
				return false;
			}
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
			System.err.println("[" + plugin.getName() + "] WARNING: Serious Bug detected, please report this!");
			System.err.println("CompatibilityProvider: " + className);
			deleteCompatibilityProviders(plugin, fileSuffix);
			return false;
		}
		catch (final Throwable t)
		{
			System.err.println("[" + plugin.getName() + "] Error loading compatibility provider!");
			System.err.println("[" + plugin.getName() + "] WARNING: Serious Bug detected, please report this!");
			System.err.println("CompatibilityProvider: " + className);
			t.printStackTrace();
			deleteCompatibilityProviders(plugin, fileSuffix);
			return false;
		}
	}

	/**
	 * Removes all compatibility providers. Moves all files to the backup folder.<br>
	 * Should be called after plugin has been updated.
	 * 
	 * @param plugin
	 *            The plugin which compatibility folder should be cleared.
	 */
	public static void deleteCompatibilityProviders(final Plugin plugin, final String fileSuffix)
	{
		System.out.println("[" + plugin.getName() + "] Trying to remove the old/incompatible compatibility provider.");
		final File target = new File(plugin.getDataFolder(), getCompatibilityPath(fileSuffix));
		if (!target.exists() || target.delete())
			System.out.println("[" + plugin.getName() + "] Compatibility provider cleaned/removed.");
		else
			logDeleteRequest(plugin);
	}

	private static boolean checkUpdated(final Plugin plugin)
	{
		if (plugin instanceof CrazyPluginInterface)
			return ((CrazyPluginInterface) plugin).isUpdated();
		else
			return false;
	}

	private static String getCompatibilityPath(final String fileSuffix)
	{
		return "compatibility/" + implementation + "/" + version + fileSuffix + ".jar";
	}

	private static void logDeleteRequest(final Plugin plugin)
	{
		System.err.println("[" + plugin.getName() + "] Compatibility providers could not be cleaned/removed.");
		System.err.println("[" + plugin.getName() + "] Please delete your /plugins/" + plugin.getName() + "/compatiblity folder");
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
