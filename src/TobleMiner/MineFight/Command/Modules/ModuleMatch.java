package TobleMiner.MineFight.Command.Modules;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.CommandHelp;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.Permissions.Permission;

public class ModuleMatch extends CommandModule
{
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player)
			p = (Player)sender;
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("create") && args.length >= 4)
			{
				if(p != null)
				{
					if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_MATCH_START))
					{
						p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				String wName = args[1];
				String gmName = args[2];
				String mName = args[3];
				boolean hardcore = false;
				if(args.length > 4) try {hardcore = Boolean.parseBoolean(args[4]);} catch(Exception ex){ if(args[5].equalsIgnoreCase("hardcore")) hardcore = true;}
				World w = Bukkit.getServer().getWorld(wName);
				if(w == null)
				{
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("nosuchworld"),wName));
					return true;
				}
				Gamemode gm = null;
				for(Gamemode g : Gamemode.values())
				{
					if(g.toString().toLowerCase().equals(gmName.toLowerCase()))
					{
						gm = g;
						break;
					}
				}
				if(gm == null)
				{
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("nosuchgmode"),gmName));
					return true;
				}
				if(!Main.gameEngine.configuration.isMpvpEnabled(w))
				{
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("mpvpNotEnabled"),w.getName()));
					return true;
				}
				if(Main.gameEngine.getMatchNames().contains(mName))
				{								
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("duplicateName"),mName));
					return true;
				}
				if(Main.gameEngine.getMatch(w) != null)
				{
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("worldOccupied"),w.getName()));
					return true;								
				}
				Main.gameEngine.startNewMatch(w, gm, mName, hardcore);
				sender.sendMessage(String.format(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("newMatchSucess"),mName,wName,Boolean.toString(hardcore)));
				return true;
			}
			
			if(args[0].equalsIgnoreCase("list"))
			{
				if(p != null && (!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_MATCH_LIST)))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				List<String> matchNames = Main.gameEngine.getMatchNames();
				String matchNamesStr = "";
				int i = 0;
				for(String name : matchNames)
				{
					matchNamesStr += ((((i % 2) == 0) ? ChatColor.RED : ChatColor.GREEN) + name + ChatColor.RESET + "    ");
					i++;
				}
				if(matchNames.size() > 0)
				{
					sender.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("listMatches_pre"));				
					sender.sendMessage(matchNamesStr);				
					sender.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("listMatches_after"));				
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("noActiveMatches"));				
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("info") && args.length >= 2)
			{
				if(p != null && (!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_MATCH_INFO)))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				Match m = Main.gameEngine.getMatchByNameIgCase(args[1]);
				if(m != null)
				{
					sender.sendMessage(ChatColor.GOLD+"===========INFO===========");
					sender.sendMessage(ChatColor.GOLD+String.format("%s: %s",Main.gameEngine.dict.get("gamemode"),Main.gameEngine.dict.get(m.gmode.transname)));
					sender.sendMessage(ChatColor.GOLD+String.format("%s: %s",Main.gameEngine.dict.get("players"),ChatColor.BLUE+Integer.toString(m.getPlayerNumBlue())+ChatColor.RESET+" | "+ChatColor.RED+Integer.toString(m.getPlayerNumRed())));
					sender.sendMessage(ChatColor.GOLD+String.format("%s: %s",Main.gameEngine.dict.get("tickets"),ChatColor.BLUE+Integer.toString((int)Math.round(m.getTeamBlue().getPoints()))+ChatColor.RESET+" | "+ChatColor.RED+Integer.toString((int)Math.round(m.getTeamRed().getPoints()))));
					String mode = Main.gameEngine.dict.get("mode");
					if(m.isHardcore())
					{
						sender.sendMessage(String.format(ChatColor.GOLD+"%s: %s",mode,ChatColor.DARK_RED+"HARDCORE"));
					}
					else
					{
						sender.sendMessage(String.format(ChatColor.GOLD+"%s: %s",mode,ChatColor.DARK_GREEN+"NORMAL"));
					}
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_RED+String.format(Main.gameEngine.dict.get("noMatch"), args[1]));
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("join") && args.length >= 2)
			{
				if(p == null)
				{
					sender.sendMessage(playerOnly);
					return true;
				}
				if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_MATCH_JOIN))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				sender.sendMessage(Main.gameEngine.playerJoinMatch(p,args[1]));
				return true;
			}
			
			if(args[0].equalsIgnoreCase("leave") && args.length >= 1)
			{
				if(p == null)
				{
					sender.sendMessage(playerOnly);
					return true;
				}
				if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_MATCH_LEAVE))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				Main.gameEngine.playerLeave(p);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("end") && args.length >= 2)
			{
				if(p != null)
				{
					if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_MATCH_END))
					{
						p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				Match m = Main.gameEngine.getMatchByNameIgCase(args[1]);
				if(m != null)
				{
					m.endMatch();
					sender.sendMessage(String.format(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("youEndedTheMatch"),args[1]));
				}
				else
				{
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("noMatch"),args[1]));
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("changeteam") && args.length >= 1)
			{
				if(p == null)
				{
					sender.sendMessage(playerOnly);
					return true;
				}
				if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_MATCH_CHANGETEAM))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				p.sendMessage(Main.gameEngine.playerChangeTeam(p));
				return true;
			}
		}
		return false;
	}
	
	public String getName()
	{
		return "match";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandMatch.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandMatch.values();
	}
	
	private enum CommandMatch implements CommandHelp
	{
		MPVP_MATCH_CREATE("match","create",3,4,"cmdDescrMatchCreate","/mpvp match create <world> <gamemode> <name> [hardcore]",Permission.MPVP_MATCH_START.toString()),
		MPVP_MATCH_END("match","end",1,1,"cmdDescrMatchEnd","/mpvp match end <name>",Permission.MPVP_MATCH_END.toString()),
		MPVP_MATCH_JOIN("match","join",1,1,"cmdDescrMatchJoin","/mpvp match join <name>",Permission.MPVP_MATCH_JOIN.toString()),
		MPVP_MATCH_LEAVE("match","leave",0,0,"cmdDescrMatchLeave","/mpvp match leave",Permission.MPVP_MATCH_LEAVE.toString()),
		MPVP_MATCH_LIST("match","list",0,0,"cmdDescrMatchList","/mpvp match list",Permission.MPVP_MATCH_LIST.toString()),
		MPVP_MATCH_INFO("match","info",1,1,"cmdDescrMatchInfo","/mpvp match INFO <name>",Permission.MPVP_MATCH_INFO.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandMatch(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
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
