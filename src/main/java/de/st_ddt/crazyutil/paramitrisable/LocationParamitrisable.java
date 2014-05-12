package de.st_ddt.crazyutil.paramitrisable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandParameterException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatConverter;

public class LocationParamitrisable extends TypedParamitrisable<Location>
{

	protected final static Pattern PATTERN_SPACE = Pattern.compile(" ");
	protected TabbedParamitrisable[] subParam;

	public LocationParamitrisable(final CommandSender sender)
	{
		this(sender instanceof Player ? simplyfyLocation(((Player) sender).getLocation()) : null);
	}

	public LocationParamitrisable(final Location defaultValue)
	{
		super(defaultValue == null ? new Location(null, 0, 0, 0) : defaultValue);
	}

	@Override
	public void setParameter(final String parameter) throws CrazyException
	{
		value = ChatConverter.stringToLocation(value, PATTERN_SPACE.split(parameter));
	}

	public void setParameter(final String[] parameters) throws CrazyException
	{
		value = ChatConverter.stringToLocation(value, parameters);
	}

	public void addFullParams(final Map<String, ? super TabbedParamitrisable> params, final String... prefixes)
	{
		for (final String prefix : prefixes)
			params.put(prefix, this);
		addAdvancedParams(params, prefixes);
	}

	public void addAdvancedParams(final Map<String, ? super TabbedParamitrisable> params, final String... prefixes)
	{
		if (subParam == null)
			subParam = createSubParams();
		for (final String prefix : prefixes)
		{
			params.put(prefix + "w", subParam[0]);
			params.put(prefix + "world", subParam[0]);
			params.put(prefix + "x", subParam[1]);
			params.put(prefix + "y", subParam[2]);
			params.put(prefix + "z", subParam[3]);
		}
	}

	public TabbedParamitrisable[] getSubParams()
	{
		if (subParam == null)
			subParam = createSubParams();
		return subParam;
	}

	public TabbedParamitrisable[] createSubParams()
	{
		final TabbedParamitrisable[] res = new TabbedParamitrisable[4];
		res[0] = new TabbedParamitrisable()
		{

			@Override
			public void setParameter(final String parameter) throws CrazyException
			{
				value.setWorld(Bukkit.getWorld(parameter));
				if (value == null)
					throw new CrazyCommandNoSuchException("World", parameter, getWorldNames());
			}

			@Override
			public List<String> tab(final String parameter)
			{
				final List<String> res = new LinkedList<String>();
				for (final World world : Bukkit.getWorlds())
					if (world.getName().startsWith(parameter))
						res.add(world.getName());
				return res;
			}
		};
		res[1] = new TabbedParamitrisable()
		{

			@Override
			public void setParameter(final String parameter) throws CrazyException
			{
				try
				{
					value.setX(Double.parseDouble(parameter));
				}
				catch (final NumberFormatException e)
				{
					throw new CrazyCommandParameterException(0, "Number (Double)");
				}
			}

			@Override
			public List<String> tab(final String parameter)
			{
				return new LinkedList<String>();
			}
		};
		res[2] = new TabbedParamitrisable()
		{

			@Override
			public void setParameter(final String parameter) throws CrazyException
			{
				try
				{
					value.setY(Double.parseDouble(parameter));
				}
				catch (final NumberFormatException e)
				{
					throw new CrazyCommandParameterException(0, "Number (Double)");
				}
			}

			@Override
			public List<String> tab(final String parameter)
			{
				return new LinkedList<String>();
			}
		};
		res[3] = new TabbedParamitrisable()
		{

			@Override
			public void setParameter(final String parameter) throws CrazyException
			{
				try
				{
					value.setZ(Double.parseDouble(parameter));
				}
				catch (final NumberFormatException e)
				{
					throw new CrazyCommandParameterException(0, "Number (Double)");
				}
			}

			@Override
			public List<String> tab(final String parameter)
			{
				return new LinkedList<String>();
			}
		};
		return res;
	}

	private String[] getWorldNames()
	{
		final List<World> worlds = Bukkit.getWorlds();
		final int length = worlds.size();
		final String[] res = new String[length];
		for (int i = 0; i < length; i++)
			res[i] = worlds.get(i).getName();
		return res;
	}

	public static Location simplyfyLocation(final Location location)
	{
		return simplyfyLocation(location, 10, 1);
	}

	public static Location simplyfyLocation(final Location location, final int precCoords, final int trimAngles)
	{
		if (precCoords > 0)
		{
			location.setX(((double) Math.round(location.getX() * precCoords)) / precCoords);
			location.setY(((double) Math.round(location.getY() * precCoords)) / precCoords);
			location.setZ(((double) Math.round(location.getZ() * precCoords)) / precCoords);
		}
		if (trimAngles > 0)
		{
			location.setYaw(Math.round(location.getYaw() / trimAngles) * trimAngles);
			location.setPitch(Math.round(location.getPitch() / trimAngles) * trimAngles);
		}
		return location;
	}

	/**
	 * Returns all chunks that are partially or totally covered by a circle centered at the given location with the given range.
	 * 
	 * @param location
	 * @param range
	 * @return
	 */
	public static Set<Chunk> getChunkWithinSoftRange(final Location location, final double range)
	{
		final LinkedList<Chunk> newChunks = new LinkedList<Chunk>();
		newChunks.add(location.getChunk());
		final World world = location.getWorld();
		final Set<Chunk> visited = new HashSet<Chunk>();
		final Set<Chunk> res = new HashSet<>();
		Chunk chunk;
		while ((chunk = newChunks.poll()) != null)
			if (getNearestChunkLocation(chunk, location).distance(location) <= range)
			{
				res.add(chunk);
				final int cX = chunk.getX();
				final int cZ = chunk.getZ();
				final Chunk cXP = world.getChunkAt(cX + 1, cZ);
				if (visited.add(cXP))
					newChunks.add(cXP);
				final Chunk cXM = world.getChunkAt(cX - 1, cZ);
				if (visited.add(cXM))
					newChunks.add(cXM);
				final Chunk cZP = world.getChunkAt(cX, cZ + 1);
				if (visited.add(cZP))
					newChunks.add(cZP);
				final Chunk cZM = world.getChunkAt(cX, cZ - 1);
				if (visited.add(cZM))
					newChunks.add(cZM);
			}
		return res;
	}

	/**
	 * Returns all chunks that are totally covered by a circle centered at the given location with the given range.
	 * 
	 * @param location
	 * @param range
	 * @return
	 */
	public static Set<Chunk> getChunkWithinStrictRange(final Location location, final double range)
	{
		final LinkedList<Chunk> newChunks = new LinkedList<Chunk>();
		newChunks.add(location.getChunk());
		final World world = location.getWorld();
		final Set<Chunk> visited = new HashSet<Chunk>();
		final Set<Chunk> res = new HashSet<>();
		Chunk chunk;
		while ((chunk = newChunks.poll()) != null)
			if (getFarestXZChunkLocation(chunk, location).distance(location) <= range)
			{
				res.add(chunk);
				final int cX = chunk.getX();
				final int cZ = chunk.getZ();
				final Chunk cXP = world.getChunkAt(cX + 1, cZ);
				if (visited.add(cXP))
					newChunks.add(cXP);
				final Chunk cXM = world.getChunkAt(cX - 1, cZ);
				if (visited.add(cXM))
					newChunks.add(cXM);
				final Chunk cZP = world.getChunkAt(cX, cZ + 1);
				if (visited.add(cZP))
					newChunks.add(cZP);
				final Chunk cZM = world.getChunkAt(cX, cZ - 1);
				if (visited.add(cZM))
					newChunks.add(cZM);
			}
		return res;
	}

	public static Location getNearestChunkLocation(final Chunk chunk, final Location location)
	{
		final long cXMin = chunk.getX() * 16L;
		final long cZMin = chunk.getZ() * 16L;
		final double nearX = getNearest(location.getX(), cXMin, cXMin + 16);
		final double nearY = getNearest(location.getZ(), cZMin, cZMin + 16);
		return new Location(location.getWorld(), nearX, location.getY(), nearY);
	}

	private static double getNearest(final double origin, final double lowerLimit, final double upperLimit)
	{
		if (lowerLimit > origin)
			return lowerLimit;
		else if (origin > upperLimit)
			return upperLimit;
		else
			return origin;
	}

	public static Location getFarestXZChunkLocation(final Chunk chunk, final Location location)
	{
		final long cXMin = chunk.getX() * 16L;
		final long cZMin = chunk.getZ() * 16L;
		final double farX = getFarest(location.getX(), cXMin, cXMin + 16);
		final double farZ = getFarest(location.getZ(), cZMin, cZMin + 16);
		return new Location(location.getWorld(), farX, location.getY(), farZ);
	}

	private static double getFarest(final double origin, final double lowerLimit, final double upperLimit)
	{
		final double dLow = Math.abs(origin - lowerLimit);
		final double dUp = Math.abs(origin - upperLimit);
		if (dLow > dUp)
			return lowerLimit;
		else
			return upperLimit;
	}
}
