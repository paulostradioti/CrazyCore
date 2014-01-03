package de.st_ddt.crazyutil.resources;

import java.io.File;
import java.io.InputStream;

import org.bukkit.plugin.Plugin;

public abstract class AbstractResourceSource implements ResourceSource
{

	@Override
	public final boolean saveResource(final Plugin plugin, final String resourcePath, final File target)
	{
		try (InputStream stream = openStream(plugin, resourcePath))
		{
			if (stream == null)
				return false;
			ResourceHelper.streamCopy(stream, target);
			System.out.println("[" + plugin.getName() + "] Saved resource to: " + target.getPath());
			return true;
		}
		catch (final Throwable t)
		{
			return false;
		}
	}
}
