package de.st_ddt.crazyutil.resources;

import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.plugin.Plugin;

public class CrazyGithubDownloadResourceSorce extends DownloadResourceSource
{

	@Override
	protected URL resolveResourceLocation(final Plugin plugin, final String resourcePath) throws MalformedURLException
	{
		return new URL("https://github.com/ST-DDT/" + plugin.getName() + "/tree/master/src/resource");
	}
}
