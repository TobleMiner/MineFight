package TobleMiner.MineFight.Command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.Permissions.Permission;

public class CommandInfosign extends CommandHandler
{
	public CommandInfosign(CommandSender sender) 
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("list"))
			{
				if(args.length >= 3)
				{
					if(this.p != null)
					{
						if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_MATCH_END))
						{
							p.sendMessage(this.noPermMsg);
							return true;
						}
					}
					World w = Bukkit.getServer().getWorld(args[1]);
					if(w != null)
					{
						Gamemode gm = null;
						for(Gamemode gmode : Gamemode.values())
						{
							if(gmode.toString().equalsIgnoreCase(args[2].trim()))
							{
								gm = gmode;
							}
						}
						if(gm != null)
						{
							sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("Signs"));
							List<Sign> signs = Main.gameEngine.configuration.getInfoSigns(w, gm);
							int i=0;
							for(Sign sign : signs)
							{
								Location loc = sign.getLocation();
								sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("Sign")+i+"@"+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ());
								i++;
							}
							return true;
						}
						else
						{
							sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("errorGm"),args[2]));
						}
					}
					else
					{
						sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("errorWorld"),args[1]));
					}
					return true;
				}
				return false;
			}
			
			if(this.p == null)
			{
				this.sender.sendMessage(this.playerOnly);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("add") && args.length >= 2)
			{
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_INFOSIGN_ADD))
				{
					this.sender.sendMessage(this.noPermMsg);
					return true;
				}
				Gamemode gm = null;
				for(Gamemode gmode : Gamemode.values())
				{
					if(gmode.toString().equalsIgnoreCase(args[1].trim()))
					{
						gm = gmode;
					}
				}
				if(gm != null)
				{
					Block tb = this.p.getTargetBlock(null,10);
					if(tb != null && (tb.getType().equals(Material.WALL_SIGN) || tb.getType().equals(Material.SIGN_POST)))
					{
						Main.gameEngine.configuration.addInfoSign((Sign)tb.getState(), gm);
						sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("signAdded"));
					}
					else
					{
						sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
					}
				}
				else
				{
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("errorGm"),args[2]));
				}
				return true;
			}
	
			if(args[0].equalsIgnoreCase("remove") && args.length >= 2)
			{
				if(!this.pm.hasPlayerPermission(this.p, Permission.MPVP_INFOSIGN_DEL))
				{
					this.sender.sendMessage(this.noPermMsg);
					return true;
				}
				Gamemode gm = null;
				for(Gamemode gmode : Gamemode.values())
				{
					if(gmode.toString().equalsIgnoreCase(args[1].trim()))
					{
						gm = gmode;
					}
				}
				if(gm != null)
				{
					Block tb = this.p.getTargetBlock(null,10);
					if(tb != null && (tb.getType().equals(Material.WALL_SIGN) || tb.getType().equals(Material.SIGN_POST)))
					{
						Main.gameEngine.configuration.removeInfoSign((Sign)tb.getState(), gm);
						sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("signRemoved"));
					}
					else
					{
						sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
					}
				}
				else
				{
					sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("errorGm"),args[2]));
				}
				return true;
			}
		}
		return false;
	}
}
