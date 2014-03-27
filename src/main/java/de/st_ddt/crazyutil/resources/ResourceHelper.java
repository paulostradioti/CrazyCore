package de.st_ddt.crazyutil.resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bukkit.plugin.Plugin;

import de.st_ddt.crazyutil.ChatHelper;

public final class ResourceHelper
{

	private final static List<ResourceSource> SOURCES = ResourceSource.SOURCES;
	private final static ResourceSource UNPACK_RESOURCE_SOURCE = new UnpackResourceSource();
	private static boolean logResourceAccess = false;
	static
	{
		SOURCES.add(UNPACK_RESOURCE_SOURCE);
	}

	/**
	 * Helper method to add a new {@link ResourceSource} to the {@link ResourceSource#SOURCES} list.
	 * 
	 * @param source
	 *            The {@link ResourceSource} to be added.
	 */
	public static void addResourceSoruce(final ResourceSource source)
	{
		SOURCES.add(0, source);
	}

	/**
	 * Whether the access to resources will be announced to log.
	 * 
	 * @return True, if resource access will be announced to log.
	 */
	public static final boolean isLogResourceAccessEnabled()
	{
		return logResourceAccess;
	}

	/**
	 * Sets whether the access to resources will be announced to log.
	 * 
	 * @param logResourceAccess
	 *            Whether the access to resources will be announced to log
	 */
	public static final void setLogResourceAccessEnabled(final boolean logResourceAccess)
	{
		ResourceHelper.logResourceAccess = logResourceAccess;
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
		logResourceNotFound(plugin, resourcePath);
		return false;
	}

	/**
	 * @param plugin
	 *            The plugin this resource belongs to.
	 * @param resourcePath
	 *            The resourcePath starting with "/".
	 * @param target
	 *            The target the resource should be copied to.
	 * @return True, if the file was saved successfully, otherwise False.
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
	 * @return True, if the file was saved successfully, otherwise False.
	 */
	public static boolean saveResource(final Plugin plugin, final String resourcePath, final File target)
	{
		for (final ResourceSource source : ResourceSource.SOURCES)
			if (source.saveResource(plugin, resourcePath, target))
				return true;
		logResourceNotFound(plugin, resourcePath);
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
	static void streamCopy(final InputStream stream, final File target) throws IOException
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

	static void logResourceNotFound(final Plugin plugin, final String resourcePath)
	{
		if (logResourceAccess)
			ChatHelper.consoleLog(plugin, true, "Resource \"{0}\" not found!", resourcePath);
	}

	static void logLocalResourceAccess(final Plugin plugin, final String resolvedResourcePath)
	{
		if (logResourceAccess)
			ChatHelper.consoleLog(plugin, false, "Accessing local resource from \"{0}\"", resolvedResourcePath);
	}

	static void logLocalResourceSave(final Plugin plugin, final String targetPath)
	{
		if (logResourceAccess)
			ChatHelper.consoleLog(plugin, false, " Saved resource to: \"{0}\"", targetPath);
	}

	protected ResourceHelper()
	{
	}
}
