package de.st_ddt.crazyplugin.commands;

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

public class CrazyCommandSave<S extends ChatHeaderProvider> extends CrazyCommandExecutor<S>
{

	protected final ReloadableProvider reloadableProvider;

	public CrazyCommandSave(final S owner, final ReloadableProvider reloadableProvider)
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
				if (!reloadable.hasSavePermission(sender))
					throw new CrazyCommandPermissionException();
			for (final Reloadable reloadable : reloads.getValue())
				reloadable.save(sender);
		}
		else
		{
			final Reloadable reloadable = reloadableProvider.getReloadables().get("default");
			if (reloadable == null)
				throw new CrazyCommandNoSuchException("Reloadable", "(none)", tab(sender, args));
			else if (!reloadable.hasSavePermission(sender))
				throw new CrazyCommandPermissionException();
			else
				reloadable.save(sender);
		}
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		return MapParamitrisable.tabHelp(reloadableProvider.getReloadables(), args[args.length - 1]);
	}
}
