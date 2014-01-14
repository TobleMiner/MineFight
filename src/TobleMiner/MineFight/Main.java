package TobleMiner.MineFight;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Air.Missiles.Missile;
import TobleMiner.MineFight.Air.Missiles.StandardMissile3.SM_3;
import TobleMiner.MineFight.Air.Missiles.Tomahawk.Tomahawk;
import TobleMiner.MineFight.Air.Missiles.Tomahawk.TomahawkMode;
import TobleMiner.MineFight.Air.Targets.Flight;
import TobleMiner.MineFight.Air.Targets.Debugging.Swarm;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest.Flag;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush.RadioStation;
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
		Main.util = new Util();
		Main.logger = new Logger(this);
		Main.gameEngine = new GameEngine(this);
		Main.pm = new PermissionManager();
		Main.plsl = new ProtocolLibSafeLoader(this);
		Bukkit.getPluginManager().registerEvents(eventListener, this);
		Timer timer = new Timer();
		GlobalTimer gtimer = new GlobalTimer();
		timer.schedule(gtimer,10,10);
	}
	
	@Override
	public void onDisable()
	{
		if(this.flight != null)
		{
			flight.doCancel();
		}
		if(swarm != null) swarm.kill();
		logger.log(Level.INFO,gameEngine.dict.get("onDisable"));
		Main.gameEngine.endAllMatches();
	}
	
	public Flight flight = null;
	public final List<Location> waypoints = new ArrayList<Location>();
	public Swarm swarm;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[])
	{
		try
		{
			if(!(sender instanceof Player))
			{
				return true;
			}
			String noPermMsg =  ChatColor.DARK_RED+Main.gameEngine.dict.get("nopermmsg");
			String cmdname = cmd.getName();
			Player p = (Player)sender;
			if(cmdname.equalsIgnoreCase("newFlight"))
			{
				if(p.isOp())
				{
					if(args.length != 6)
					{
						return false;
					}
					//String flightname = args[0];
					
					String[] startLocale = args[1].split(",");
					int xStart = Integer.parseInt(startLocale[0]);
					int yStart = Integer.parseInt(startLocale[1]);
					int zStart = Integer.parseInt(startLocale[2]);
					Location start = new Location(p.getWorld(),xStart, yStart, zStart);
					
					String[] targetLocale = args[2].split(",");
					int xTarget = Integer.parseInt(targetLocale[0]);
					int yTarget = Integer.parseInt(targetLocale[1]);
					int zTarget = Integer.parseInt(targetLocale[2]);
					Location target = new Location(p.getWorld(),xTarget, yTarget, zTarget);
					
					int maxHeight = Integer.parseInt(args[3]);
					double maxSpeed = Double.parseDouble(args[4]);
					double acceleration = Double.parseDouble(args[5]);
					Vector v = new Vector(0.1d,0.1d,0.1d);
					Arrow arrow = p.getWorld().spawnArrow(start,v,1.0f,1.0f);
					if(this.flight != null)
					{
						flight.doCancel();
					}
					flight = new Flight(arrow, start, target, maxHeight, maxSpeed, acceleration);
				}
				else
				{
					p.sendMessage(noPermMsg);
				}
				return true;
			}
			else if(cmdname.equalsIgnoreCase("newMissile"))
			{
				if(p.isOp())
				{
					if(flight == null)
					{
						return true;
					}
					if(args.length < 4)
					{
						return false;
					}
					boolean SM3 = false;
					boolean Tomahawk = false;
					TomahawkMode mode = TomahawkMode.GroundProfile;
					if(args.length >= 5)
					{
						if(args[4].toLowerCase().contains("sm") && args[4].toLowerCase().contains("3"))
						{
							SM3 = true;
						}
						else if(args[4].toLowerCase().contains("tomahawk"))
						{
							Tomahawk = true;
						}
						if(args.length >= 6)
						{
							for(TomahawkMode m : TomahawkMode.values())
							{
								if(m.toString().toLowerCase().equalsIgnoreCase(args[5].toLowerCase()))
								{
									mode = m;
									break;
								}
							}
						}
					}
					//String flightname = args[0];
					
					String[] startLocale = args[1].split(",");
					int xStart = Integer.parseInt(startLocale[0]);
					int yStart = Integer.parseInt(startLocale[1]);
					int zStart = Integer.parseInt(startLocale[2]);
					Location start = new Location(p.getWorld(),xStart, yStart, zStart);
									
					int maxHeight = (int)Math.round((double)flight.maxHeight*Math.sqrt(2d));
					double maxSpeed = Double.parseDouble(args[2]);
					double acceleration = Double.parseDouble(args[3]);
					Vector v = new Vector(0.1d,0.1d,0.1d);
					Arrow arrow = p.getWorld().spawnArrow(start,v,1.0f,1.0f);
					if(SM3)
					{
						new SM_3(arrow, start, maxHeight, maxSpeed, acceleration, flight,this);
						return true;
					}
					else
					{
						if(Tomahawk)
						{
							new Tomahawk(arrow, start, maxSpeed, acceleration, flight, mode);
							return true;
						}
						else
						{
							new Missile(arrow, start, maxHeight, maxSpeed, acceleration,flight);
							return true;
						}
					}
				}
				else
				{
					p.sendMessage(noPermMsg);
					return true;
				}
			}
			else if(cmdname.equalsIgnoreCase("spawnSwarm"))
			{
				if(p.isOp())
				{
					if(args.length > 2)
					{
						int size = 10;
						if(args.length > 3)
						{
							size = Integer.parseInt(args[3]);
						}
						double spreadX = Double.parseDouble(args[0]);
						double spreadY = Double.parseDouble(args[1]);
						double spreadZ = Double.parseDouble(args[2]);
						if(swarm != null) swarm.kill();
						Swarm swarm = new Swarm(this, size,"Welcome to the herd",this.waypoints);
						this.swarm = swarm;
						System.out.println("Swarm created!");
						swarm.spawn(p.getWorld(),spreadX, spreadY, spreadZ);
						System.out.println("Swarm spawned!");
						return true;
					}
				}
				else
				{
					p.sendMessage(noPermMsg);
					return true;
				}
			}
			else if(cmdname.equalsIgnoreCase("mpvp"))
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
				else if(args.length == 1)
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
								Sign sign = (Sign)tb.getState();
								Flag.buildFlag(sign);
								Main.gameEngine.configuration.addFlag(sign);
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
								Sign sign = (Sign)tb.getState();
								RadioStation.buildRadioStation(sign,RadioStation.getFacing(sign));
								Main.gameEngine.configuration.addRadioStation(sign);
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
