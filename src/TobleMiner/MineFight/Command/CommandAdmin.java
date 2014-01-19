package TobleMiner.MineFight.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Permissions.Permission;

public class CommandAdmin extends CommandHandler
{
	public CommandAdmin(CommandSender sender)
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))
			{
				if(this.p != null)
				{
					if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_RELOAD))
					{
						p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				Main.gameEngine.reload();
				sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("configrl"));				
				return true;
			}
		}
		return false;
	}	
}