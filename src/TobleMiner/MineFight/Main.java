package TobleMiner.MineFight;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest.Flag;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush.RadioStation;
import TobleMiner.MineFight.LegalFu.LicenseHandler;
import TobleMiner.MineFight.PacketModification.ProtocolLibSafeLoader;
import TobleMiner.MineFight.Permissions.Permission;
import TobleMiner.MineFight.Permissions.PermissionManager;
import TobleMiner.MineFight.Util.Util;

public class Main extends JavaPlugin
{
	private final EventListener eventListener = new EventListener(this);
	public static Main main;
	public static PermissionManager pm;
	public static ProtocolLibSafeLoader plsl;
	public static Logger logger;
	public static Util util;
	
	public Main()
	{
		Main.main = this;
	}
	
	public static GameEngine gameEngine;
	
	@Override
	public void onEnable()
	{
		init();
		logger.log(Level.INFO,gameEngine.dict.get("onEnable"));
	}
	
	public void init()
	{
		Main.logger = new Logger(this);
		Main.util = new Util();
		Main.gameEngine = new GameEngine(this);
		logger.log(Level.INFO,gameEngine.dict.get("preEnable"));
		Bukkit.getPluginManager().registerEvents(eventListener, this);
		if(!(new LicenseHandler().init(this)))
		{
			Error err = new Error("License check failed!","The plugins license could not be copied into the plugin's folder!", "The plugin won't start until the license is copied.", this.getClass().getName(), ErrorSeverity.DOUBLERAINBOOM);
			ErrorReporter.reportError(err);
			return;
		}
		Main.pm = new PermissionManager();
		Main.plsl = new ProtocolLibSafeLoader(this);
		Timer timer = new Timer();
		GlobalTimer gtimer = new GlobalTimer();
		timer.schedule(gtimer,10,10);
	}
	
	@Override
	public void onDisable()
	{
		logger.log(Level.INFO,gameEngine.dict.get("onDisable"));
		Main.gameEngine.endAllMatches();
	}
	
	public final List<Location> waypoints = new ArrayList<Location>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[])
	{
		try
		{
			String noPermMsg =  ChatColor.DARK_RED+Main.gameEngine.dict.get("nopermmsg");
			String cmdname = cmd.getName();
			if(cmdname.equalsIgnoreCase("mpvp") && (args.length == 1) && (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("v")))
			{
				PluginDescriptionFile pdf = this.getDescription();
				sender.sendMessage(String.format("%s v%s Copyright by %s 2014",pdf.getName(),pdf.getVersion(),StringUtils.join(pdf.getAuthors(),",")));
				return true;
			}
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.DARK_RED+gameEngine.dict.get("cmdNoCli"));
				return true;
			}
			Player p = (Player)sender;
			if(cmdname.equalsIgnoreCase("mpvp"))
			{
				if(args.length == 4 || args.length == 5)
				{
					if(args[0].equalsIgnoreCase("newMatch"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_MATCH_START))
						{
							String worldStr = args[1];
							String gamemodeStr = args[2];
							String name = args[3];
							World world  = Bukkit.getServer().getWorld(worldStr);
							Gamemode gmode = null;
							for(Gamemode g : Gamemode.values())
							{
								if(g.toString().toLowerCase().equals(gamemodeStr.toLowerCase()))
								{
									gmode = g;
									break;
								}
							}
							if(gmode != null && world != null)
							{
								if(!Main.gameEngine.configuration.isMpvpEnabled(world))
								{
									sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("mpvpNotEnabled"),world.getName()));
									return true;
								}
								if(Main.gameEngine.getMatchNames().contains(name))
								{								
									sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("duplicateName"),name));
									return true;
								}
								if(Main.gameEngine.getMatch(world) != null)
								{
									sender.sendMessage(String.format(ChatColor.DARK_RED+Main.gameEngine.dict.get("worldOccupied"),world.getName()));
									return true;								
								}
								boolean hardcore = false;
								if(args.length == 5)
								{
									hardcore = args[4].toLowerCase().equals("true");
								}
								gameEngine.startNewMatch(world, gmode, name,hardcore);
								sender.sendMessage(String.format(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("newMatchSucess"),name,worldStr,Boolean.toString(hardcore)));
								return true;
							}
							else
							{
								sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("newMatchInvalidInput"));							
							}
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
				}
				else if(args.length == 3)
				{
					if(args[0].equalsIgnoreCase("listInfoSigns"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_INFOSIGN_LIST))
						{
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
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
				}
				else if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("join"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_MATCH_JOIN))
						{
							sender.sendMessage(Main.gameEngine.playerJoinMatch(p,args[1]));
							return true;
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					if(args[0].equalsIgnoreCase("info"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_MATCH_INFO))
						{
							Match m = gameEngine.getMatchByNameIgCase(args[1]);
							if(m != null)
							{
								sender.sendMessage(ChatColor.GOLD+"===========INFO===========");
								sender.sendMessage(ChatColor.GOLD+"Gamemode: "+m.gmode.toString().toString());
								sender.sendMessage(ChatColor.GOLD+"Players: "+ChatColor.BLUE+Integer.toString(m.getPlayerNumBlue())+ChatColor.RESET+" | "+ChatColor.RED+Integer.toString(m.getPlayerNumRed()));
								sender.sendMessage(ChatColor.GOLD+"Tickets: "+ChatColor.BLUE+Integer.toString((int)Math.round(m.getTeamBlue().getPoints()))+ChatColor.RESET+" | "+ChatColor.RED+Integer.toString((int)Math.round(m.getTeamRed().getPoints())));
							}
							return true;
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("end"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_MATCH_END))
						{
							Match m = gameEngine.getMatchByNameIgCase(args[1]);
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
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("addInfoSign"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_INFOSIGN_ADD))
						{
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
								Block tb = p.getTargetBlock(null,10);
								if(tb != null && (tb.getType().equals(Material.WALL_SIGN) || tb.getType().equals(Material.SIGN_POST)))
								{
									Main.gameEngine.configuration.addInfoSign((Sign)tb.getState(), gm);
									sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("signAdded"));
									return true;
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
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("removeInfoSign"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_INFOSIGN_DEL))
						{
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
								Block tb = p.getTargetBlock(null,10);
								if(tb != null && (tb.getType().equals(Material.WALL_SIGN) || tb.getType().equals(Material.SIGN_POST)))
								{
									Main.gameEngine.configuration.removeInfoSign((Sign)tb.getState(), gm);
									sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("signRemoved"));
									return true;
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
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
				}
				if(args.length >= 1)
				{
					if(args[0].equalsIgnoreCase("leave"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_MATCH_LEAVE))
						{
							if(Main.gameEngine.playerLeave(p))
							{
								sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("leftGame"));
							}
							else
							{
								sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("notJoinedYet"));
							}
							return true;
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("list"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_MATCH_LIST))
						{
							List<String> matchNames = gameEngine.getMatchNames();
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
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_RELOAD))
						{
							Main.gameEngine.reload();
							sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("configrl"));				
							return true;
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("newFlag") || args[0].equalsIgnoreCase("addFlag"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_FLAG_ADD))
						{
							Block tb = p.getTargetBlock(null,10);
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
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("removeFlag"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_FLAG_DEL))
						{
							Block tb = p.getTargetBlock(null,10);
							if(tb != null && (tb.getType().equals(Material.WALL_SIGN)))
							{
								Sign sign = (Sign)tb.getState();
								Main.gameEngine.configuration.removeFlag(sign);
								sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("removeFlag"));
								return true;
							}
							else
							{
								sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
							}
							return true;
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("newRadioStation") || args[0].equalsIgnoreCase("newRS") || args[0].equalsIgnoreCase("addRadioStation") || args[0].equalsIgnoreCase("addRS"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_RS_ADD))
						{
							Block tb = p.getTargetBlock(null,10);
							if(tb != null && (tb.getType().equals(Material.WALL_SIGN)))
							{
								String name = "";
								if(args.length > 1)
								{
									name = args[1];
								}
								Sign sign = (Sign)tb.getState();
								RadioStation.buildRadioStation(sign,RadioStation.getFacing(sign));
								Main.gameEngine.configuration.addRadioStation(new RadioStationContainer(sign, name));
								Main.gameEngine.configuration.addNewProtectedRegion(sign.getLocation().clone().subtract(11d, 11d, 11d), sign.getLocation().clone().add(11d, 11d, 11d));
								sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("addRs"));
							}
							else
							{
								sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
							}
							return true;						
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("removeRadioStation") || args[0].equalsIgnoreCase("removeRS"))
					{
						if(pm.hasPlayerPermission(p, Permission.MPVP_RS_DEL))
						{
							Block tb = p.getTargetBlock(null,10);
							if(tb != null && (tb.getType().equals(Material.WALL_SIGN)))
							{
								Sign sign = (Sign)tb.getState();
								Main.gameEngine.configuration.removeRadioStation(sign);
								sender.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("rmRs"));
								return true;
							}
							else
							{
								sender.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("mustPointOnSign"));
							}
							return true;
						}
						else
						{
							p.sendMessage(noPermMsg);
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("changeTeam"))
					{
						sender.sendMessage(Main.gameEngine.playerChangeTeam(p));
						return true;
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}	
	
	public File getPluginDir()
	{
		return this.getDataFolder();
	}
}
