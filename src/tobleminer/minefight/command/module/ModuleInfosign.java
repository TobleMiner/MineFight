package tobleminer.minefight.command.module;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tobleminer.minefight.Main;
import tobleminer.minefight.command.CommandHelp;
import tobleminer.minefight.engine.match.gamemode.Gamemode;
import tobleminer.minefight.permission.Permission;

public class ModuleInfosign extends CommandModule
{
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		Player p = null;
		if (sender instanceof Player)
			p = (Player) sender;
		if (args.length >= 1)
		{
			if (args[0].equalsIgnoreCase("list"))
			{
				if (args.length >= 3)
				{
					if (p != null)
					{
						if (!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_INFOSIGN_LIST))
						{
							p.sendMessage(this.noPermMsg);
							return true;
						}
					}
					World w = Bukkit.getServer().getWorld(args[1]);
					if (w != null)
					{
						Gamemode gm = null;
						for (Gamemode gmode : Gamemode.values())
						{
							if (gmode.toString().equalsIgnoreCase(args[2].trim()))
							{
								gm = gmode;
							}
						}
						if (gm != null)
						{
							sender.sendMessage(ChatColor.DARK_GREEN + Main.gameEngine.dict.get("Signs"));
							List<Sign> signs = Main.gameEngine.configuration.getInfoSigns(w, gm);
							int i = 0;
							for (Sign sign : signs)
							{
								Location loc = sign.getLocation();
								sender.sendMessage(ChatColor.DARK_GREEN + Main.gameEngine.dict.get("Sign") + i + "@"
										+ loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
								i++;
							}
							return true;
						}
						else
						{
							sender.sendMessage(
									String.format(ChatColor.DARK_RED + Main.gameEngine.dict.get("errorGm"), args[2]));
						}
					}
					else
					{
						sender.sendMessage(
								String.format(ChatColor.DARK_RED + Main.gameEngine.dict.get("errorWorld"), args[1]));
					}
					return true;
				}
				return false;
			}

			if (p == null)
			{
				sender.sendMessage(playerOnly);
				return true;
			}

			if (args[0].equalsIgnoreCase("add") && args.length >= 2)
			{
				if (!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_INFOSIGN_ADD))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				Gamemode gm = null;
				for (Gamemode gmode : Gamemode.values())
				{
					if (gmode.toString().equalsIgnoreCase(args[1].trim()))
					{
						gm = gmode;
					}
				}
				if (gm != null)
				{
					Block tb = p.getTargetBlock((HashSet<Byte>) null, 10);
					if (tb != null
							&& (tb.getType().equals(Material.WALL_SIGN) || tb.getType().equals(Material.SIGN_POST)))
					{
						Main.gameEngine.configuration.addInfoSign((Sign) tb.getState(), gm);
						sender.sendMessage(ChatColor.DARK_GREEN + Main.gameEngine.dict.get("signAdded"));
					}
					else
					{
						sender.sendMessage(ChatColor.DARK_RED + Main.gameEngine.dict.get("mustPointOnSign"));
					}
				}
				else
				{
					sender.sendMessage(
							String.format(ChatColor.DARK_RED + Main.gameEngine.dict.get("errorGm"), args[2]));
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("remove") && args.length >= 2)
			{
				if (!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_INFOSIGN_DEL))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				Gamemode gm = null;
				for (Gamemode gmode : Gamemode.values())
				{
					if (gmode.toString().equalsIgnoreCase(args[1].trim()))
					{
						gm = gmode;
					}
				}
				if (gm != null)
				{
					Block tb = p.getTargetBlock((HashSet<Byte>) null, 10);
					if (tb != null
							&& (tb.getType().equals(Material.WALL_SIGN) || tb.getType().equals(Material.SIGN_POST)))
					{
						Main.gameEngine.configuration.removeInfoSign((Sign) tb.getState(), gm);
						sender.sendMessage(ChatColor.DARK_GREEN + Main.gameEngine.dict.get("signRemoved"));
					}
					else
					{
						sender.sendMessage(ChatColor.DARK_RED + Main.gameEngine.dict.get("mustPointOnSign"));
					}
				}
				else
				{
					sender.sendMessage(
							String.format(ChatColor.DARK_RED + Main.gameEngine.dict.get("errorGm"), args[2]));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName()
	{
		return "is";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for (CommandHelp help : CommandInfosign.values())
			if (help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}

	@Override
	public CommandHelp[] getHelp()
	{
		return CommandInfosign.values();
	}

	private enum CommandInfosign implements CommandHelp
	{
		MPVP_FLAG_ADD("is", "add", 0, 0, "cmdDescrFlagAdd", "/mpvp flag add",
				Permission.MPVP_FLAG_ADD.toString()), MPVP_FLAG_DEL("is", "remove", 0, 0, "cmdDescrFlagDel",
						"/mpvp flag remove", Permission.MPVP_FLAG_DEL.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;

		CommandInfosign(String module, String cmd, int argnumMin, int argnumMax, String descr, String syntax,
				String perm)
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
