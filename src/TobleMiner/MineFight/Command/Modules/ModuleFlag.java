package TobleMiner.MineFight.Command.Modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.CommandHelp;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest.Flag;
import TobleMiner.MineFight.Permissions.Permission;

public class ModuleFlag extends CommandModule
{
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		if(args.length >= 1)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(playerOnly);
				return true;
			}
			Player p = (Player)sender;
			if(args[0].equalsIgnoreCase("add"))
			{
				if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_FLAG_ADD))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				Block tb = p.getTargetBlock(null,10);
				if(tb != null && (tb.getType().equals(Material.WALL_SIGN)))
				{
					String name = "";
					boolean sky = true;
					if(args.length >= 2)
					{
						name = args[1];
						if(args.length >= 3)
						{
							try
							{
								sky = Boolean.parseBoolean(args[2]);
							}
							catch(Exception ex) { };
						}
					}
					Sign sign = (Sign)tb.getState();
					Flag.buildFlag(sign);
					Main.gameEngine.configuration.addFlag(new FlagContainer(sign, name, sky));
					Main.gameEngine.configuration.addNewProtectedRegion(sign.getLocation().clone().subtract(11d, 11d, 11d), sign.getLocation().clone().add(11d, 11d, 11d));
					sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("addFlag"));
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(!!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_FLAG_DEL))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}

				Block tb = p.getTargetBlock(null,10);
				if(tb != null && (tb.getType().equals(Material.WALL_SIGN)))
				{
					Sign sign = (Sign)tb.getState();
					Main.gameEngine.configuration.removeFlag(sign);
					sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("removeFlag"));
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getName()
	{
		return "flag";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandFlag.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandFlag.values();
	}
	
	private enum CommandFlag implements CommandHelp
	{
		MPVP_FLAG_ADD("flag","add",0,0,"cmdDescrFlagAdd","/mpvp flag add",Permission.MPVP_FLAG_ADD.toString()),
		MPVP_FLAG_DEL("flag","remove",0,0,"cmdDescrFlagDel","/mpvp flag remove",Permission.MPVP_FLAG_DEL.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandFlag(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
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