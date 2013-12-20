package de.st_ddt.crazyutil.resources;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.plugin.Plugin;

public abstract class DownloadResourceSource extends AbstractResourceSource
{

	protected abstract URL resolveResourceLocation(Plugin plugin, String resourcePath) throws MalformedURLException;

	@Override
	public final InputStream openStream(final Plugin plugin, final String resourcePath)
	{
		try
		{
			final URL url = resolveResourceLocation(plugin, resourcePath);
			if (url == null)
				return null;
			else
			{
				System.out.println("[" + plugin.getName() + "] Accessing external resource from: " + url.toString());
				return url.openStream();
			}
		}
		catch (final Throwable t)
		{
			return null;
		}
	}
}
