package de.st_ddt.crazyutil.reloadable;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

public class AllReloadable implements Reloadable
{

	private final String reloadPermission;
	private final String savePermission;
	private final Set<Reloadable> reloadables = new LinkedHashSet<Reloadable>();

	public AllReloadable(final String reloadPermission, final String savePermission)
	{
		super();
		this.reloadPermission = reloadPermission;
		this.savePermission = savePermission;
	}

	@Override
	public void reload(final CommandSender sender)
	{
		for (final Reloadable reloadable : reloadables)
			reloadable.reload(sender);
	}

	@Override
	public boolean hasReloadPermission(final CommandSender sender)
	{
		return sender.hasPermission(reloadPermission);
	}

	@Override
	public void save(final CommandSender sender)
	{
		for (final Reloadable reloadable : reloadables)
			reloadable.reload(sender);
	}

	@Override
	public boolean hasSavePermission(final CommandSender sender)
	{
		return sender.hasPermission(savePermission);
	}

	public final boolean addReloadable(final Reloadable reloadable)
	{
		return reloadables.add(reloadable);
	}

	public final Set<Reloadable> getReloadables()
	{
		return reloadables;
	}
}
