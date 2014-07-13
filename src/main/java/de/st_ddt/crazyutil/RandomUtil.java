package de.st_ddt.crazyutil;

import java.util.Collection;
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
		if (elements == null || elements.length == 0)
			return null;
		else
			return elements[RANDOM.nextInt(elements.length)];
	}

	@SuppressWarnings("unchecked")
	public static <E> E randomElement(final Collection<E> elements)
	{
		if (elements == null || elements.isEmpty())
			return null;
		else
			return (E) randomElement(elements.toArray());
	}

	public static <E> E randomElement(final List<E> elements)
	{
		if (elements == null || elements.isEmpty())
			return null;
		else
			return elements.get(RANDOM.nextInt(elements.size()));
	}

	public static int random(final int min, final int max)
	{
		if (min < 0)
			return (int) Math.abs(RANDOM.nextLong()) % (max - min + 1) + min;
		else
			return RANDOM.nextInt(max - min + 1) + min;
	}

	public static long random(final long min, final long max)
	{
		return Math.abs(RANDOM.nextLong()) % (max - min + 1) + min;
	}

	public static float random(final float min, final float max)
	{
		return RANDOM.nextFloat() * (max - min) + min;
	}

	public static double random(final double min, final double max)
	{
		return RANDOM.nextDouble() * (max - min) + min;
	}
}
