package TobleMiner.MineFight.Command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.Permissions.Permission;

public class CommandMatch extends CommandHandler
{
	public CommandMatch(CommandSender sender)
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("create") && args.length >= 4)
			{
				if(this.p != null)
				{
					if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_START))
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
					this.sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("nosuchworld"),wName));
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
					this.sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("nosuchgmode"),gmName));
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
				if(this.p != null && (!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_LIST)))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				List<String> matchNames = Main.gameEngine.getMatchNames();
				String matchNamesStr = "";
				for(String name : matchNames)
				{
					matchNamesStr += name+"     ";
				}
				if(matchNames.size() > 0)
				{
					sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("listMatches_pre")+"\n"+matchNamesStr);				
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("noActiveMatches"));				
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("info") && args.length >= 2)
			{
				if(this.p != null && (!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_INFO)))
				{
					sender.sendMessage(this.noPermMsg);
					return true;
				}
				Match m = Main.gameEngine.getMatchByNameIgCase(args[1]);
				if(m != null)
				{
					sender.sendMessage(ChatColor.GOLD+"===========INFO===========");
					sender.sendMessage(ChatColor.GOLD+"Gamemode: "+m.gmode.toString().toString());
					sender.sendMessage(ChatColor.GOLD+"Players: "+ChatColor.BLUE+Integer.toString(m.getPlayerNumBlue())+ChatColor.RESET+" | "+ChatColor.RED+Integer.toString(m.getPlayerNumRed()));
					sender.sendMessage(ChatColor.GOLD+"Tickets: "+ChatColor.BLUE+Integer.toString((int)Math.round(m.getTeamBlue().getPoints()))+ChatColor.RESET+" | "+ChatColor.RED+Integer.toString((int)Math.round(m.getTeamRed().getPoints())));
					if(m.isHardcore())
					{
						sender.sendMessage(ChatColor.DARK_RED+"HARDCORE");
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
				if(this.p == null)
				{
					this.sender.sendMessage(this.playerOnly);
					return true;
				}
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_JOIN))
				{
					this.sender.sendMessage(this.noPermMsg);
					return true;
				}
				sender.sendMessage(Main.gameEngine.playerJoinMatch(this.p,args[1]));
				return true;
			}
			
			if(args[0].equalsIgnoreCase("leave") && args.length >= 1)
			{
				if(this.p == null)
				{
					this.sender.sendMessage(this.playerOnly);
					return true;
				}
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_LEAVE))
				{
					this.sender.sendMessage(this.noPermMsg);
					return true;
				}
				Main.gameEngine.playerLeave(this.p);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("end") && args.length >= 2)
			{
				if(this.p != null)
				{
					if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_END))
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
				if(this.p == null)
				{
					this.sender.sendMessage(this.playerOnly);
					return true;
				}
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_CHANGETEAM))
				{
					this.sender.sendMessage(this.noPermMsg);
					return true;
				}
				this.p.sendMessage(Main.gameEngine.playerChangeTeam(this.p));
				return true;
			}
		}
		return false;
	}
}
