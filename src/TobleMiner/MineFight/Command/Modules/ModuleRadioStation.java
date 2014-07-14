package TobleMiner.MineFight.Command.Modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.CommandHelp;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush.RadioStation;
import TobleMiner.MineFight.Permissions.Permission;

public class ModuleRadioStation extends CommandModule
{
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player)
			p = (Player)sender;
		if(args.length >= 1)
		{
			if(p == null)
			{
				sender.sendMessage(playerOnly);
				return true;
			}
			if(args[0].equalsIgnoreCase("add"))
			{
				if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_RS_ADD))
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
					RadioStation.buildRadioStation(sign,RadioStation.getFacing(sign));
					Main.gameEngine.configuration.addRadioStation(new RadioStationContainer(sign, name, sky));
					Main.gameEngine.configuration.addNewProtectedRegion(sign.getLocation().clone().subtract(11d, 11d, 11d), sign.getLocation().clone().add(11d, 11d, 11d));
					sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("addRs"));
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
				}
				return true;						
			}
			
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_RS_DEL))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				Block tb = p.getTargetBlock(null,10);
				if(tb != null && (tb.getType().equals(Material.WALL_SIGN)))
				{
					Sign sign = (Sign)tb.getState();
					Main.gameEngine.configuration.removeRadioStation(sign);
					sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("rmRs"));
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
		return "rs";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandRadiostation.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandRadiostation.values();
	}
	
	private enum CommandRadiostation implements CommandHelp
	{
		MPVP_RADIOSTATION_ADD("rs","add",0,0,"cmdDescrRsAdd","/mpvp rs add",Permission.MPVP_RS_ADD.toString()),
		MPVP_RADIOSTATION_DEL("rs","remove",0,0,"cmdDescrRsDel","/mpvp rs remove",Permission.MPVP_RS_DEL.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandRadiostation(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
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
