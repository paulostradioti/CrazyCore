package de.st_ddt.crazyutil.conditions.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class Condition_Entity_Type extends SimpleEntityCondition
{

	protected final Set<EntityType> allowedTypes = EnumSet.noneOf(EntityType.class);

	public Condition_Entity_Type(final int index)
	{
		super(index);
	}

	public Condition_Entity_Type(final int index, final EntityType... types)
	{
		super(index);
		for (final EntityType type : types)
			allowedTypes.add(type);
	}

	public Condition_Entity_Type(final int index, final Collection<EntityType> types)
	{
		super(index);
		allowedTypes.addAll(types);
	}

	public Condition_Entity_Type(final ConfigurationSection config, final Map<String, Integer> parameterIndexes)
	{
		super(config, parameterIndexes);
		final List<String> allowedTypeNames = config.getStringList("allowedTypes");
		if (allowedTypeNames != null)
			for (final String allowedType : allowedTypeNames)
				try
				{
					this.allowedTypes.add(EntityType.valueOf(allowedType));
				}
				catch (final Exception e)
				{
					System.err.println("EntityType " + allowedType + " was not found/invalid and has been removed! (" + config.getCurrentPath() + ".allowedTypes)");
				}
	}

	@Override
	protected boolean check(final Entity object)
	{
		return allowedTypes.contains(object.getType());
	}

	@Override
	public void save(final ConfigurationSection config, final String path, final Map<Integer, String> parameterNames)
	{
		super.save(config, path, parameterNames);
		final List<String> allowedTypeNames = new ArrayList<>(allowedTypes.size());
		for (final EntityType allowedType : allowedTypes)
			allowedTypeNames.add(allowedType.name());
		config.set(path + "allowedTypes", allowedTypeNames);
	}
}
