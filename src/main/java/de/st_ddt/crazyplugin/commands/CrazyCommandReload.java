package de.st_ddt.crazyplugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandPermissionException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatHeaderProvider;
import de.st_ddt.crazyutil.paramitrisable.MapParamitrisable;
import de.st_ddt.crazyutil.paramitrisable.MultiParamitrisable;
import de.st_ddt.crazyutil.reloadable.Reloadable;
import de.st_ddt.crazyutil.reloadable.ReloadableProvider;

public class CrazyCommandReload<S extends ChatHeaderProvider> extends CrazyCommandExecutor<S>
{

	protected final ReloadableProvider reloadableProvider;

	public CrazyCommandReload(final S owner, final ReloadableProvider reloadableProvider)
	{
		super(owner);
		this.reloadableProvider = reloadableProvider;
	}

	@Override
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (args.length > 0)
		{
			final MapParamitrisable<Reloadable> reload = new MapParamitrisable<Reloadable>("Reloadable", reloadableProvider.getReloadables(), null, true);
			final MultiParamitrisable<Reloadable> reloads = new MultiParamitrisable<Reloadable>(reload);
			for (final String arg : args)
				reloads.setParameter(arg);
			for (final Reloadable reloadable : reloads.getValue())
				if (!reloadable.hasReloadPermission(sender))
					throw new CrazyCommandPermissionException();
			for (final Reloadable reloadable : reloads.getValue())
				reloadable.reload(sender);
		}
		else
		{
			final Reloadable reloadable = reloadableProvider.getReloadables().get("default");
			if (reloadable == null)
				throw new CrazyCommandNoSuchException("Reloadable", "(none)", tab(sender, args));
			else if (!reloadable.hasReloadPermission(sender))
				throw new CrazyCommandPermissionException();
			else
				reloadable.reload(sender);
		}
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		if (args.length == 0)
			return new ArrayList<String>(reloadableProvider.getReloadables().keySet());
		else
			return MapParamitrisable.tabHelp(reloadableProvider.getReloadables(), args[args.length - 1]);
	}
}
