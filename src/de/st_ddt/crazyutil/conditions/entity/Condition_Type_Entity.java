package de.st_ddt.crazyutil.conditions.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import de.st_ddt.crazyutil.conditions.checker.EntityConditionChecker;

public class Condition_Type_Entity extends BasicEntityCondition
{

	protected final Set<EntityType> allowedTypes = EnumSet.noneOf(EntityType.class);

	public Condition_Type_Entity()
	{
		super();
	}

	public Condition_Type_Entity(final ConfigurationSection config)
	{
		super(config);
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
	public String getType()
	{
		return "ENTITY_TYPE";
	}

	@Override
	public boolean check(final EntityConditionChecker checker)
	{
		return allowedTypes.contains(checker.getEntity().getType());
	}

	@Override
	public void save(final ConfigurationSection config, final String path)
	{
		super.save(config, path);
		final List<String> allowedTypeNames = new ArrayList<>(allowedTypes.size());
		for (final EntityType allowedType : allowedTypes)
			allowedTypeNames.add(allowedType.name());
		config.set(path + "allowedTypes", allowedTypeNames);
	}
}
