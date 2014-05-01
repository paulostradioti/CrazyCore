package de.st_ddt.crazyutil.paramitrisable;

import java.util.Comparator;
import java.util.Map;

public class SortParamitrisable<S> extends MapParamitrisable<Comparator<? super S>>
{

	public SortParamitrisable(final Map<String, ? extends Comparator<? super S>> values, final Comparator<? super S> defaultValue)
	{
		super("Sorter", values, defaultValue);
	}

	public SortParamitrisable(final Map<String, ? extends Comparator<? super S>> values, final Comparator<? super S> defaultValue, final boolean lowercase)
	{
		super("Sorter", values, defaultValue, lowercase);
	}
}
