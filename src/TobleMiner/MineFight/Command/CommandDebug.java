package TobleMiner.MineFight.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Team.Team;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Permissions.Permission;

public class CommandDebug extends CommandHandler
{
	public CommandDebug(CommandSender sender)
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(!Main.gameEngine.configuration.isDebuging())
		{
			return false;
		}
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("player"))
			{
				String pname = null;
				if(this.p != null)
				{
					pname = this.p.getName();
					if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_DEBUG))
					{
						this.p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				if(args.length >= 2)
				{
					pname = args[1];
				}
				if(pname == null)
				{
					sender.sendMessage(ChatColor.RED+Main.gameEngine.dict.get("statsNameConsole"));
				}
				Player p = Bukkit.getPlayer(pname);
				if(p != null)
				{
					Match m = Main.gameEngine.getMatch(p.getWorld());
					if(m != null)
					{
						PVPPlayer player = m.getPlayerExact(p);
						if(player != null)
						{
							sender.sendMessage("=====DEBUG=====");
							sender.sendMessage("Name: "+player.getName());
							Team t = player.getTeam();
							String teamname = "null";
							if(t != null) teamname = t.getName();
							String teamObjAddr = "null";
							if(t != null) teamObjAddr = t.toString();
							sender.sendMessage("Team: "+teamname);
							sender.sendMessage("Team (precise): "+teamObjAddr);
							sender.sendMessage("Spawned: "+player.isSpawned());
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
