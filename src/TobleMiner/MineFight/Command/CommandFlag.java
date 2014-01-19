package TobleMiner.MineFight.Command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest.Flag;
import TobleMiner.MineFight.Permissions.Permission;

public class CommandFlag extends CommandHandler
{
	public CommandFlag(CommandSender sender) 
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(args.length >= 1)
		{
			if(this.p == null)
			{
				this.sender.sendMessage(this.playerOnly);
				return true;
			}
			if(args[0].equalsIgnoreCase("add"))
			{
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_FLAG_ADD))
				{
					this.sender.sendMessage(this.noPermMsg);
					return true;
				}
				Block tb = this.p.getTargetBlock(null,10);
				if(tb != null && (tb.getType().equals(Material.WALL_SIGN)))
				{
					String name = "";
					if(args.length > 1)
					{
						name = args[1];
					}
					Sign sign = (Sign)tb.getState();
					Flag.buildFlag(sign);
					Main.gameEngine.configuration.addFlag(new FlagContainer(sign, name));
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
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_FLAG_DEL))
				{
					this.sender.sendMessage(this.noPermMsg);
					return true;
				}

				Block tb = this.p.getTargetBlock(null,10);
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
}
