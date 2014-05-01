package de.st_ddt.crazyplugin.comparators;

import de.st_ddt.crazyplugin.CrazyPluginInterface;
import de.st_ddt.crazyplugin.comparators.CrazyPluginComparator;

public class CrazyPluginNameComparator implements CrazyPluginComparator
{

	@Override
	public int compare(final CrazyPluginInterface o1, final CrazyPluginInterface o2)
	{
		return o1.getName().compareTo(o2.getName());
	}
}
