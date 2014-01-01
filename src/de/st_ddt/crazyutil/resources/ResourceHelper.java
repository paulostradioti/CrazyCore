package de.st_ddt.crazyutil.resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.plugin.Plugin;

public class ResourceHelper
{

	protected final static ResourceSoruce UNPACK_RESOURCE_SOURCE = new UnpackResourceSource();
	protected static boolean silentResourceAccess = false;
	static
	{
		ResourceSoruce.SOURCES.add(UNPACK_RESOURCE_SOURCE);
	}

	/**
	 * Whether the access to resources will be announced to log.
	 * 
	 * @return False, if resource access will be announced to log.
	 */
	public static final boolean isSilentResourceAccess()
	{
		return silentResourceAccess;
	}

	/**
	 * Sets whether the access to resources will be announced to log.
	 * 
	 * @param silentResourceAccess
	 *            Whether the access to resources will be announced to log
	 */
	public static final void setSilentResourceAccess(final boolean silentResourceAccess)
	{
		ResourceHelper.silentResourceAccess = silentResourceAccess;
	}

	/**
	 * @param plugin
	 *            The plugin this resource belongs to.
	 * @param resourcePath
	 *            The resourcePath starting with "/".
	 * @param target
	 *            The target the resource should be copied to.
	 * @return True, if the file was unpacked successfully, otherwise False.
	 */
	public static boolean unpackResource(final Plugin plugin, final String resourcePath, final String target)
	{
		final File file = new File(plugin.getDataFolder(), target);
		return unpackResource(plugin, resourcePath, file);
	}

	/**
	 * @param plugin
	 *            The plugin this resource belongs to.
	 * @param resourcePath
	 *            The resourcePath starting with "/".
	 * @param target
	 *            The target the resource should be copied to.
	 * @return True, if the file was unpacked successfully, otherwise False.
	 */
	public static boolean unpackResource(final Plugin plugin, final String resourcePath, final File target)
	{
		if (UNPACK_RESOURCE_SOURCE.saveResource(plugin, resourcePath, target))
			return true;
		printResourceNotFound(plugin, resourcePath);
		return false;
	}

	/**
	 * @param plugin
	 *            The plugin this resource belongs to.
	 * @param resourcePath
	 *            The resourcePath starting with "/".
	 * @param target
	 *            The target the resource should be copied to.
	 * @return True, if the file was save successfully, otherwise False.
	 */
	public static boolean saveResource(final Plugin plugin, final String resourcePath, final String target)
	{
		final File file = new File(plugin.getDataFolder(), target);
		return saveResource(plugin, resourcePath, file);
	}

	/**
	 * @param plugin
	 *            The plugin this resource belongs to.
	 * @param resourcePath
	 *            The resourcePath starting with "/".
	 * @param target
	 *            The target the resource should be copied to.
	 * @return True, if the file was save successfully, otherwise False.
	 */
	public static boolean saveResource(final Plugin plugin, final String resourcePath, final File target)
	{
		for (final ResourceSoruce source : ResourceSoruce.SOURCES)
			if (source.saveResource(plugin, resourcePath, target))
				return true;
		printResourceNotFound(plugin, resourcePath);
		return false;
	}

	/**
	 * Copies the entire input stream to the given target.<br>
	 * Closes the input stream.
	 * 
	 * @param stream
	 *            The stream that should be copied.
	 * @param target
	 *            The target where the stream should be copied too.
	 * @throws IOException
	 *             If something went wrong.
	 */
	protected static void streamCopy(final InputStream stream, final File target) throws IOException
	{
		target.getParentFile().mkdirs();
		try (InputStream in = new BufferedInputStream(stream);
				OutputStream out = new BufferedOutputStream(new FileOutputStream(target)))
		{
			final byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1)
				out.write(data, 0, count);
			out.flush();
		}
	}

	protected static void printResourceNotFound(final Plugin plugin, final String resourcePath)
	{
		System.err.println("[" + plugin.getName() + "] Resource \"" + resourcePath + "\" not found!");
	}

	protected ResourceHelper()
	{
	}
}
