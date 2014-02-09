package TobleMiner.MineFight.Command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush.RadioStation;
import TobleMiner.MineFight.Permissions.Permission;

public class CommandRadioStation extends CommandHandler
{
	public CommandRadioStation(CommandSender sender)
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
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_RS_ADD))
				{
					this.sender.sendMessage(this.noPermMsg);
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
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_RS_DEL))
				{
					this.sender.sendMessage(this.noPermMsg);
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
}
