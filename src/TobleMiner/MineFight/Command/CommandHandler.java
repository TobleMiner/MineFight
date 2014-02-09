package TobleMiner.MineFight.Command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Permissions.PermissionManager;

public abstract class CommandHandler
{
	protected final String noPermMsg;
	protected final String playerOnly;
	protected Player p;
	protected final CommandSender sender;
	protected final PermissionManager pm;
	
	public CommandHandler(CommandSender sender)
	{
		this.noPermMsg = ChatColor.DARK_RED+Main.gameEngine.dict.get("nopermmsg");
		this.playerOnly = ChatColor.DARK_RED+Main.gameEngine.dict.get("playeronlycmd");
		this.sender = sender;
		if(this.sender instanceof Player)
		{
			p = (Player)sender;
		}
		this.pm = Main.pm;
	}
	
	public static boolean handleCommand(String[] args, CommandSender sender)
	{
		if(args.length >= 1)
		{
			List<String> argsComp = new ArrayList<String>();
			boolean escaped = false;
			String current = "";
			for(String argRaw : args)
			{
				String arg = argRaw.trim();
				if(escaped)
				{
					if(arg.endsWith("\"") && arg.length() > 1)
					{
						escaped = false;
						argsComp.add(current+" "+arg.subSequence(0, arg.length()-1));
					}
					else
					{
						current += " "+arg;
					}
				}
				else
				{
					if(arg.startsWith("\"") && arg.length() > 1)
					{
						escaped = true;
						current = arg.substring(1);
					}
					else
					{
						argsComp.add(arg);
					}
				}
			}
			if(argsComp.size() < 2) return false;
			String[] argsShort = new String[argsComp.size()-1];
			System.arraycopy(argsComp.toArray(new String[0]), 1, argsShort, 0, argsShort.length);
			if(args[0].equalsIgnoreCase("help"))
			{
				return new CommandHelp(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("rs"))
			{
				return new CommandRadioStation(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("flag"))
			{
				return new CommandFlag(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("is"))
			{
				return new CommandInfosign(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("match"))
			{
				return new CommandMatch(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("admin"))
			{
				return new CommandAdmin(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("player"))
			{
				return new CommandPlayer(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("debug"))
			{
				return new CommandDebug(sender).handle(argsShort);
			}
			else if(args[0].equalsIgnoreCase("version"))
			{
				PluginDescriptionFile pdf = Main.main.getDescription();
				sender.sendMessage(String.format("%s v%s Copyright by %s 2014",pdf.getName(),pdf.getVersion(),StringUtils.join(pdf.getAuthors(),",")));
				return true;
			}
		}
		return false;
	}
	
	public abstract boolean handle(String[] args);
}
