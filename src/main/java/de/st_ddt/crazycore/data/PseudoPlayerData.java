package de.st_ddt.crazycore.data;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.st_ddt.crazycore.CrazyCore;
import de.st_ddt.crazyplugin.data.PlayerData;
import de.st_ddt.crazyplugin.events.CrazyPlayerAssociatesEvent;
import de.st_ddt.crazyutil.ChatHelper;
import de.st_ddt.crazyutil.locales.CrazyLocale;
import de.st_ddt.crazyutil.modules.permissiongroups.PermissionModule;
import de.st_ddt.crazyutil.source.Localized;

public class PseudoPlayerData extends PlayerData<PseudoPlayerData>
{

	public PseudoPlayerData(final String name)
	{
		super(name);
	}

	@Override
	public String getParameter(final CommandSender sender, final int index)
	{
		switch (index)
		{
			case 0:
				return name;
			default:
				return "";
		}
	}

	@Override
	public int getParameterCount()
	{
		return 1;
	}

	public CrazyCore getPlugin()
	{
		return CrazyCore.getPlugin();
	}

	@Override
	protected String getChatHeader()
	{
		return getPlugin().getChatHeader();
	}

	@Override
	public void show(final CommandSender target)
	{
		show(target, getChatHeader(), false);
	}

	@Override
	@Localized({ "CRAZYCORE.PLAYERINFO.LANGUAGE {Language}", "CRAZYCORE.PLAYERINFO.ASSOCIATES {Associates}", "CRAZYCORE.PLAYERINFO.GROUPS {Groups}", "CRAZYCORE.PLAYERINFO.PROTECTEDPLAYER {Protected}" })
	public void showDetailed(final CommandSender target, final String chatHeader)
	{
		final CrazyCore plugin = CrazyCore.getPlugin();
		plugin.sendLocaleMessage("PLAYERINFO.LANGUAGE", target, CrazyLocale.getUserLanguageName(name, true));
		final CrazyPlayerAssociatesEvent associatesEvent = new CrazyPlayerAssociatesEvent(name);
		associatesEvent.callEvent();
		plugin.sendLocaleMessage("PLAYERINFO.ASSOCIATES", target, ChatHelper.listingString(associatesEvent.getAssociates()));
		final Player player = Bukkit.getPlayerExact(name);
		if (player != null)
		{
			final Set<String> groups = PermissionModule.getGroups(player);
			if (groups == null)
			{
				final String group = PermissionModule.getGroup(player);
				if (group != null)
					plugin.sendLocaleMessage("PLAYERINFO.GROUPS", target, group);
			}
			else
				plugin.sendLocaleMessage("PLAYERINFO.GROUPS", target, ChatHelper.listingString(groups));
		}
		plugin.sendLocaleMessage("PLAYERINFO.PROTECTEDPLAYER", target, plugin.isProtectedPlayer(name) ? "True" : "False");
	}

	@Override
	public boolean reload()
	{
		return true;
	}

	@Override
	public void flush()
	{
	}

	@Override
	public void delete()
	{
	}
}
