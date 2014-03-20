package de.st_ddt.crazyplugin.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazycore.CrazyCore;
import de.st_ddt.crazyplugin.CrazyPlayerDataPluginInterface;
import de.st_ddt.crazyplugin.data.PlayerDataInterface;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatHelper;
import de.st_ddt.crazyutil.paramitrisable.PlayerDataParamitrisable;
import de.st_ddt.crazyutil.source.Localized;
import de.st_ddt.crazyutil.source.Permission;

public class CrazyPlayerDataPluginCommandPlayerDelete<T extends PlayerDataInterface> extends CrazyPlayerDataPluginCommandExecutor<T, CrazyPlayerDataPluginInterface<T, ? extends T>>
{

	public CrazyPlayerDataPluginCommandPlayerDelete(final CrazyPlayerDataPluginInterface<T, ? extends T> plugin)
	{
		super(plugin);
	}

	@Override
	@Permission("{CRAZYPLAYERDATAPLUGIN}.player.delete.protected")
	@Localized("{CRAZYPLAYERDATAPLUGIN}.COMMAND.PLAYER.DELETE.SUCCESS {Name}")
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		final String name = ChatHelper.listingString(" ", args);
		CrazyCore.getPlugin().checkProtectedPlayer(name, sender, owner.getName() + ".player.delete.protected", owner.getName(), "deleting plugin's player data");
		if (!owner.getCrazyDatabase().deleteEntry(name))
			throw new CrazyCommandNoSuchException("PlayerData", name);
		owner.sendLocaleMessage("COMMAND.PLAYER.DELETE.SUCCESS", sender, name);
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		if (args.length != 1)
			return null;
		else
			return PlayerDataParamitrisable.tabHelp(owner, args[0]);
	}

	@Override
	@Permission("{CRAZYPLAYERDATAPLUGIN}.player.delete")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission(owner.getName() + ".player.delete");
	}
}
