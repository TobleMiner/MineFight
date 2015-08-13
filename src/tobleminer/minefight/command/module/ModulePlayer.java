package tobleminer.minefight.command.module;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tobleminer.minefight.Main;
import tobleminer.minefight.command.CommandHelp;
import tobleminer.minefight.engine.match.statistics.beans.PlayerStatBean;
import tobleminer.minefight.permission.Permission;

public class ModulePlayer extends CommandModule
{
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		Player p = null;
		if (sender instanceof Player)
			p = (Player) sender;
		if (args.length >= 1)
		{
			if (args[0].equalsIgnoreCase("stats"))
			{
				String pname = null;
				if (p != null)
				{
					pname = p.getName();
					if (!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_RELOAD))
					{
						p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				boolean global = false;
				if (args.length >= 2)
				{
					pname = args[1];
					if (args.length >= 3)
					{
						if (args[2].toLowerCase().contains("global") || args[2].toLowerCase().contains("true"))
						{
							global = true;
						}
					}
				}
				else
				{
					if (p == null)
					{
						sender.sendMessage(ChatColor.RED + Main.gameEngine.dict.get("statsNameConsole"));
						return true;
					}
				}
				if (!global)
				{
					if (Main.gameEngine.stathandler.lStats)
					{
						PlayerStatBean bean = Main.gameEngine.stathandler.getBean(pname);
						if (bean != null)
						{
							sender.sendMessage("Points: " + bean.getPoints().longValue());
							return true;
						}
						sender.sendMessage(ChatColor.RED + String.format(
								Main.gameEngine.dict.get("statsPlayerNotFound"), pname,
								global ? Main.gameEngine.dict.get("globally") : Main.gameEngine.dict.get("locally")));
					}
					else
					{
						sender.sendMessage(ChatColor.RED + Main.gameEngine.dict.get("localStatsNotEnabled"));
					}
				}
				else
				{

					if (Main.gameEngine.stathandler.gStats)
					{

					}
					else
					{
						sender.sendMessage(ChatColor.RED + Main.gameEngine.dict.get("globalStatsNotEnabled"));
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName()
	{
		return "player";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for (CommandHelp help : CommandPlayer.values())
			if (help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}

	@Override
	public CommandHelp[] getHelp()
	{
		return CommandPlayer.values();
	}

	private enum CommandPlayer implements CommandHelp
	{
		MPVP_PLAYER_STATS("player", "stats", 0, 2, "cmdDescrPlStat", "/mpvp player [player] [global]",
				Permission.MPVP_STATS.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;

		CommandPlayer(String module, String cmd, int argnumMin, int argnumMax, String descr, String syntax, String perm)
		{
			this.module = module;
			this.cmd = cmd;
			this.argnumMin = argnumMin;
			this.argnumMax = argnumMax;
			this.syntax = syntax;
			this.descr = descr;
			this.perm = perm;
		}

		@Override
		public String getCmd()
		{
			return cmd;
		}

		@Override
		public String getModule()
		{
			return module;
		}

		@Override
		public int argMin()
		{
			return argnumMin;
		}

		@Override
		public int argMax()
		{
			return argnumMax;
		}

		@Override
		public String getDescr()
		{
			return Main.gameEngine.dict.get(descr);
		}

		@Override
		public String getPermission()
		{
			return perm;
		}

		@Override
		public String getSyntax()
		{
			return syntax;
		}
	}
}