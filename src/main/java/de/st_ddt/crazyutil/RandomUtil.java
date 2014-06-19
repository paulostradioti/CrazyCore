package de.st_ddt.crazyutil;

import java.util.Random;

public class RandomUtil
{

	protected final static Random RANDOM = new Random();

	protected RandomUtil()
	{
	}

	@SafeVarargs
	public static <E> E randomElement(E... elements)
	{
		if (elements.length == 0)
			return null;
		else
			return elements[RANDOM.nextInt(elements.length - 1)];
	}
}
