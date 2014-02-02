package TobleMiner.MineFight.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.GameEngine.Match.Statistics.Beans.PlayerStatBean;
import TobleMiner.MineFight.Permissions.Permission;

public class CommandPlayer extends CommandHandler
{
	public CommandPlayer(CommandSender sender)
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("stats"))
			{
				String pname = null;
				if(this.p != null)
				{
					pname = this.p.getName();
					if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_RELOAD))
					{
						this.p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				boolean global = false;
				if(args.length >= 2)
				{
					pname = args[1];
					if(args.length >= 3)
					{
						if(args[2].toLowerCase().contains("global") || args[2].toLowerCase().contains("true"))
						{
							global = true;
						}
					}
				}
				else
				{
					if(this.p == null)
					{
						sender.sendMessage(ChatColor.RED+Main.gameEngine.dict.get("statsNameConsole"));
						return true;
					}
				}
				if(!global)
				{
					if(Main.gameEngine.stathandler.lStats)
					{
						PlayerStatBean bean = Main.gameEngine.stathandler.getBean(pname);
						if(bean != null)
						{
							sender.sendMessage("Points: "+bean.getPoints().longValue());
							return true;
						}
						sender.sendMessage(ChatColor.RED+String.format(Main.gameEngine.dict.get("statsPlayerNotFound"),pname,global ? Main.gameEngine.dict.get("globally") : Main.gameEngine.dict.get("locally")));				
					}
					else
					{
						sender.sendMessage(ChatColor.RED+Main.gameEngine.dict.get("localStatsNotEnabled"));				
					}
				}
				else
				{
					
					if(Main.gameEngine.stathandler.gStats)
					{
						
					}
					else
					{
						sender.sendMessage(ChatColor.RED+Main.gameEngine.dict.get("globalStatsNotEnabled"));				
					}
				}
				return true;
			}
		}
		return false;
	}	
}
