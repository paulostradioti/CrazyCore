package de.st_ddt.crazycore.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazycore.CrazyCore;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandUsageException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.locales.CrazyLocale;
import de.st_ddt.crazyutil.source.Localized;
import de.st_ddt.crazyutil.source.Permission;

public class CommandLanguageRemovePreloaded extends CommandExecutor
{

	public CommandLanguageRemovePreloaded(final CrazyCore plugin)
	{
		super(plugin);
	}

	@Override
	@Localized("CRAZYCORE.COMMAND.LANGUAGE.PRELOADED.REMOVED {LanguageName} {Language}")
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (args.length != 1)
			throw new CrazyCommandUsageException("<Language>");
		final String language = CrazyLocale.fixLanguage(args[0]);
		if (language == null)
			throw new CrazyCommandNoSuchException("Language", args[0], CrazyLocale.getActiveLanguagesNames(true));
		owner.getPreloadedLanguages().remove(language);
		owner.sendLocaleMessage("COMMAND.LANGUAGE.PRELOADED.REMOVED", sender, CrazyLocale.getSaveLanguageName(language), language);
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		if (args.length != 1)
			return null;
		final List<String> res = new ArrayList<String>();
		final String arg = args[0];
		final Pattern pattern = Pattern.compile(arg, Pattern.CASE_INSENSITIVE);
		for (final String language : owner.getPreloadedLanguages())
			if (pattern.matcher(language).find() || pattern.matcher(CrazyLocale.getSaveLanguageName(language)).find())
				res.add(language);
		return res;
	}

	@Override
	@Permission("crazylanguage.advanced")
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return sender.hasPermission("crazylanguage.advanced");
	}
}
