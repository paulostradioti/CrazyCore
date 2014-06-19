package de.st_ddt.crazyutil;

import java.util.List;
import java.util.Random;

public class RandomUtil
{

	protected final static Random RANDOM = new Random();

	protected RandomUtil()
	{
	}

	@SafeVarargs
	public static <E> E randomElement(final E... elements)
	{
		if (elements.length == 0)
			return null;
		else
			return elements[RANDOM.nextInt(elements.length - 1)];
	}

	public static <E> E randomElement(final List<E> elements)
	{
		if (elements.isEmpty())
			return null;
		else
			return elements.get(RANDOM.nextInt(elements.size() - 1));
	}
}
