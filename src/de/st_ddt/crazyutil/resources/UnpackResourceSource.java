package de.st_ddt.crazyutil.resources;

import java.io.InputStream;

import org.bukkit.plugin.Plugin;

public class UnpackResourceSource extends AbstractResourceSource
{

	@Override
	public InputStream openStream(final Plugin plugin, final String resourcePath)
	{
		ResourceHelper.logLocalResourceAccess(plugin, "/plugins/" + plugin.getName() + ".jar/resource" + resourcePath);
		return plugin.getClass().getResourceAsStream("/resource" + resourcePath);
	}
}
