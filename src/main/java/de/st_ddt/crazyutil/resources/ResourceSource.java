package de.st_ddt.crazyutil.resources;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

public interface ResourceSource
{

	public final static List<ResourceSource> SOURCES = new ArrayList<>();

	public InputStream openStream(Plugin plugin, String resourcePath);

	public boolean saveResource(Plugin plugin, String resourcePath, File target);
}
