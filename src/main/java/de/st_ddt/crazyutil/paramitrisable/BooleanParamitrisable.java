package de.st_ddt.crazyutil.paramitrisable;

import java.util.LinkedList;
import java.util.List;

import de.st_ddt.crazyplugin.exceptions.CrazyCommandParameterException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;

public class BooleanParamitrisable extends TypedParamitrisable<Boolean>
{

	public BooleanParamitrisable(final boolean defaultValue)
	{
		super(defaultValue);
	}

	public BooleanParamitrisable(final Boolean defaultValue)
	{
		super(defaultValue);
	}

	@Override
	public void setParameter(final String parameter) throws CrazyException
	{
		try
		{
			value = getFromString(parameter);
		}
		catch (final IllegalArgumentException e)
		{
			throw new CrazyCommandParameterException(0, "Boolean (false/true)");
		}
	}

	@Override
	public List<String> tab(final String parameter)
	{
		return tabHelp(parameter);
	}

	public static boolean getFromString(String parameter) throws IllegalArgumentException
	{
		parameter = parameter.toLowerCase();
		if (parameter.equals("true"))
			return true;
		else if (parameter.equals("1"))
			return true;
		else if (parameter.equals("y"))
			return true;
		else if (parameter.equals("yes"))
			return true;
		else if (parameter.equals("false"))
			return false;
		else if (parameter.equals("0"))
			return false;
		else if (parameter.equals("n"))
			return false;
		else if (parameter.equals("no"))
			return false;
		else
			throw new IllegalArgumentException("The input " + parameter + " could not be converted into a boolean!");
	}

	public static List<String> tabHelp(String parameter)
	{
		parameter = parameter.toLowerCase();
		final List<String> res = new LinkedList<String>();
		if ("true".startsWith(parameter))
			res.add("true");
		if ("false".startsWith(parameter))
			res.add("false");
		return res;
	}
}
