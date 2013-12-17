package de.st_ddt.crazycore.data;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.st_ddt.crazyutil.ChatHeaderProvider;
import de.st_ddt.crazyutil.ChatHelper;
import de.st_ddt.crazyutil.locales.CrazyLocale;
import de.st_ddt.crazyutil.source.Localized;

public class PlayerDataHelper
{

	@Localized({ "CRAZYPLUGIN.PLAYERINFO.HEAD $DateTime$", "CRAZYPLUGIN.PLAYERINFO.USERNAME $Username$", "CRAZYPLUGIN.PLAYERINFO.DISPLAYNAME $Displayname$", "CRAZYPLUGIN.PLAYERINFO.IPADDRESS $IPAdress$", "CRAZYPLUGIN.PLAYERINFO.CONNECTION $Connection$", "CRAZYPLUGIN.PLAYERINFO.URL $IPAddress$", "CRAZYPLUGIN.PLAYERINFO.FIRSTCONNECT $DateTime$", "CRAZYPLUGIN.PLAYERINFO.LASTCONNECT $DateTime$", "CRAZYPLUGIN.PLAYERINFO.OP $OP$", "CRAZYPLUGIN.PLAYERINFO.WHITELISTED $Whitelisted$", "CRAZYPLUGIN.PLAYERINFO.BANNED $Banned$", "CRAZYPLUGIN.PLAYERINFO.SEPARATOR" })
	public static void show(final CommandSender target, final OfflinePlayer offlinePlayer, final String chatHeader)
	{
		final CrazyLocale locale = CrazyLocale.getLocaleHead().getSecureLanguageEntry("CRAZYPLUGIN.PLAYERINFO");
		final Player player = offlinePlayer.getPlayer();
		ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("HEAD"), ChatHeaderProvider.DATETIMEFORMAT.format(new Date()));
		if (player == null)
			ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("USERNAME"), offlinePlayer.getName());
		else
		{
			ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("USERNAME"), player.getName());
			if (player.getDisplayName() == null)
				ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("DISPLAYNAME"), player.getName());
			else
				ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("DISPLAYNAME"), player.getDisplayName());
			ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("IPADDRESS"), player.getAddress().getAddress().getHostAddress());
			ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("CONNECTION"), player.getAddress().getHostName());
			ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("URL"), player.getAddress().getAddress().getHostAddress());
		}
		ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("FIRSTCONNECT"), ChatHeaderProvider.DATETIMEFORMAT.format(new Date(offlinePlayer.getFirstPlayed())));
		ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("LASTCONNECT"), ChatHeaderProvider.DATETIMEFORMAT.format(new Date(offlinePlayer.getLastPlayed())));
		ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("OP"), offlinePlayer.isOp() ? "True" : "False");
		if (Bukkit.hasWhitelist())
			ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("WHITELISTED"), offlinePlayer.isWhitelisted() ? "True" : "False");
		if (offlinePlayer.isBanned())
			ChatHelper.sendMessage(target, chatHeader, locale.getLanguageEntry("BANNED"), "True");
	}

	private PlayerDataHelper()
	{
	}
}
