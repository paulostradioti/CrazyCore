package de.st_ddt.crazyutil.comparators;

import de.st_ddt.crazyutil.comparators.PlayerDataComparator;
import de.st_ddt.crazyutil.datas.PlayerData;

public class PlayerDataNameComparator<S extends PlayerData> implements PlayerDataComparator<S>
{

	@Override
	public int compare(final S o1, final S o2)
	{
		return o1.getName().compareToIgnoreCase(o2.getName());
	}
}
