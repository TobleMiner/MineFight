package TobleMiner.MineFight.GameEngine.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest.Flag;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush.RadioStation;
import TobleMiner.MineFight.GameEngine.Match.Team.Team;
import TobleMiner.MineFight.GameEngine.Match.Team.TeamBlue;
import TobleMiner.MineFight.GameEngine.Match.Team.TeamRed;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.GameEngine.Player.CombatClass.CombatClass;
import TobleMiner.MineFight.GameEngine.Player.Info.InformationSign;
import TobleMiner.MineFight.GameEngine.Player.Resupply.ResupplyStation;
import TobleMiner.MineFight.Protection.ProtectedArea;
import TobleMiner.MineFight.Util.Location.TeleportUtil;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.InventorySyncCalls;
import TobleMiner.MineFight.Weapon.WeaponType;
import TobleMiner.MineFight.Weapon.Projectile.ProjectileType;
import TobleMiner.MineFight.Weapon.Projectile.SimpleProjectile;
import TobleMiner.MineFight.Weapon.RC.C4;
import TobleMiner.MineFight.Weapon.TickControlled.Claymore;
import TobleMiner.MineFight.Weapon.TickControlled.HandGrenade;
import TobleMiner.MineFight.Weapon.TickControlled.IMS;
import TobleMiner.MineFight.Weapon.TickControlled.RPG;
import TobleMiner.MineFight.Weapon.TickControlled.SentryMissile;
import TobleMiner.MineFight.Weapon.TickControlled.TickControlledWeapon;
import TobleMiner.MineFight.WeaponStationary.SentryGun;

public class Match 
{
	public final Gamemode gmode;
	public final World world;
	public final String name;
	private TeamRed teamRed = new TeamRed();
	private TeamBlue teamBlue = new TeamBlue();
	private List<PVPPlayer> playersBlue = new ArrayList<PVPPlayer>();
	private List<PVPPlayer> playersRed = new ArrayList<PVPPlayer>();
	private final boolean hardcore;
	private final Location classSelectLoc;
	private final Location spawnLoc;
	private final Location matchLeaveLoc;
	private final List<TickControlledWeapon> ltcw = new ArrayList<TickControlledWeapon>();
	private List<InformationSign> infSs = new ArrayList<InformationSign>();
	private List<Flag> flags = new ArrayList<Flag>();
	private int flagsRed = 0;
	private int flagsBlue = 0;
	private List<RadioStation> radioStations = new ArrayList<RadioStation>();
	private Iterator<RadioStation> radioStationIterator;
	private RadioStation activeRadioStation;
	private int timer = 1;
	
	private final HashMap<Item,Claymore> claymores = new HashMap<Item,Claymore>();
	private final HashMap<PVPPlayer,List<C4>> c4explosives = new HashMap<PVPPlayer,List<C4>>();
	private final HashMap<PVPPlayer,SentryGun> sentries = new HashMap<PVPPlayer,SentryGun>();
	private final HashMap<Item,HandGrenade> handGrenades = new HashMap<Item,HandGrenade>();
	private final HashMap<Item,IMS> imss = new HashMap<Item,IMS>();
	private final HashMap<Arrow,SentryGun> sentryArrows = new HashMap<Arrow,SentryGun>();
	private final HashMap<Arrow, SentryMissile> sentryMissiles = new HashMap<Arrow,SentryMissile>();
	private final HashMap<Arrow,RPG> rpgs = new HashMap<Arrow,RPG>();
	private final List<Item> c4registry = new ArrayList<Item>();
	private final List<ResupplyStation> resupplyStations = new ArrayList<ResupplyStation>();
	private final HashMap<Arrow, SimpleProjectile> projectiles = new HashMap<Arrow,SimpleProjectile>();
	private final boolean damageEnviron;
	private final boolean exploDamageEnviron;
	
	public Match(World world,Gamemode gmode,String name,boolean hardcore,List<Sign> infoSigns,List<Sign> flagSigns,List<Sign> radioStationSigns)
	{
		this.world = world;
		this.gmode = gmode;
		this.name = name;
		this.hardcore = hardcore;
		this.matchLeaveLoc = Main.gameEngine.configuration.getRoundEndSpawnForWorld(world);
		this.spawnLoc = Main.gameEngine.configuration.getSpawnForWorld(world);
		this.classSelectLoc = Main.gameEngine.configuration.getRespawnForWorld(world);
		if(gmode.equals(Gamemode.Conquest))
		{
			this.teamRed.setPoints(Main.gameEngine.configuration.getPointsForGamemodeInWorld(world,gmode));
			this.teamBlue.setPoints(Main.gameEngine.configuration.getPointsForGamemodeInWorld(world,gmode));
		}
		else if(gmode.equals(Gamemode.Rush))
		{
			this.teamRed.setPoints(Main.gameEngine.configuration.getPointsForGamemodeInWorld(world,gmode));
			this.teamBlue.setPoints(radioStationSigns.size());
		}
		else
		{
			this.teamRed.setPoints(0);
			this.teamBlue.setPoints(0);			
		}
		for(Sign s : infoSigns)
		{
			InformationSign infS = new InformationSign(this,s);
			infSs.add(infS);
		}
		if(gmode.equals(Gamemode.Conquest))
		{
			double dist = Main.gameEngine.configuration.getFlagCaptureDistance(world);
			double speed = Main.gameEngine.configuration.getFlagCaptureSpeed(world);
			double accel = Main.gameEngine.configuration.getFlagCaptureAcceleration(world);
			for(Sign s : flagSigns)
			{
				Flag flag = new Flag(s,this,dist,speed,accel);
				flags.add(flag);
			}			
		}
		else if(gmode.equals(Gamemode.Rush))
		{
			double radioStationExploTime = Main.gameEngine.configuration.getRadioStationDestructTime(world);
			for(Sign s : radioStationSigns)
			{
				RadioStation rs = new RadioStation(s, radioStationExploTime, this);
				radioStations.add(rs);
			}
			radioStationIterator = radioStations.iterator();
			if(radioStationIterator.hasNext())
			{
				activeRadioStation = radioStationIterator.next();
			}
		}
		this.damageEnviron = Main.gameEngine.configuration.canEvironmentBeDamaged(gmode, world);
		this.exploDamageEnviron = Main.gameEngine.configuration.canExlosionsDamageEnvironment(gmode, world);
	}
	
	public boolean canEnvironmentBeDamaged()
	{
		return this.damageEnviron;
	}
	
	public boolean canExplosionsDamageEnvironment()
	{
		return this.exploDamageEnviron;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public String join(Player p)
	{
		if(Main.gameEngine.getPlayerExact(p) != null)
		{
			return ChatColor.DARK_RED+Main.gameEngine.dict.get("alreadyJoined");
		}
		this.sendTeamMessage(null, ChatColor.GOLD+String.format(Main.gameEngine.dict.get("joinMsg"),p.getName()));
		int membersRed = playersRed.size();
		int membersBlue = playersBlue.size();
		Random rand = new Random();
		Team team = ((rand.nextInt() % 2 == 0) ? teamRed : teamBlue);
		if(membersRed != membersBlue)
		{
			if(membersRed > membersBlue)
			{
				team = teamBlue;
			}
			else
			{
				team = teamRed;
			}
		}
		PVPPlayer player = new PVPPlayer(p,team,this,Main.gameEngine.configuration.getFlamethrowerIgnitionDist(),Main.gameEngine.configuration.getFlamethrowerDirectDamage(),Main.gameEngine.configuration.getMedigunHealingDist(),Main.gameEngine.configuration.getMedigunHealingRate(),Bukkit.getServer().createMap(world));
		p.setDisplayName(player.getName());
		p.setCustomName(player.getName());
		p.setCustomNameVisible(true);
		for(PVPPlayer watcher : this.getPlayers())
		{
			Main.plsl.sendNamechange(player, watcher, false);
			Main.plsl.sendNamechange(watcher, player, false);
		}
		if(team == teamRed)
		{
			playersRed.add(player);
		}
		else
		{
			playersBlue.add(player);
		}
		p.teleport(classSelectLoc);
		Main.plsl.registerPlayer(p, player);
		player.storeInventory();
		return ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("persJoinMsg"),this.name);
	}
	
	public PVPPlayer getPlayerByName(String name)
	{
		for(int i=0;i<playersRed.size();i++)
		{
			PVPPlayer p = playersRed.get(i);
			if(p.getName().equals(name))
			{
				return p;
			}
		}
		for(int i=0;i<playersBlue.size();i++)
		{
			PVPPlayer p = playersBlue.get(i);
			if(p.getName().equals(name))
			{
				return p;
			}
		}
		return null;
	}

	public PVPPlayer getPlayerExact(Player player)
	{
		for(int i=0;i<playersRed.size();i++)
		{
			PVPPlayer p = playersRed.get(i);
			if(p.thePlayer.equals(player))
			{
				return p;
			}
		}
		for(int i=0;i<playersBlue.size();i++)
		{
			PVPPlayer p = playersBlue.get(i);
			if(p.thePlayer.equals(player))
			{
				return p;
			}
		}
		return null;
	}
	
	public boolean anounceTeamchange(PVPPlayer player,Team from,Team to)
	{
		if(from == to)
		{
			return true;
		}
		for(PVPPlayer watcher : this.getPlayers())
		{
			Main.plsl.sendNamechange(player, watcher, false);
		}
		if(from == teamRed)
		{
			playersRed.remove(player);
			playersBlue.add(player);
			return true;
		}
		if(from == teamBlue)
		{
			playersBlue.remove(player);
			playersRed.add(player);
			return true;			
		}
		return false;
	}
	
	public void anouncePlayerLeave(PVPPlayer player)
	{
		Main.plsl.unregisterPlayer(player.thePlayer);
		if(player.getTeam() == teamRed)
		{
			playersRed.remove(player);
		}
		else
		{
			playersBlue.remove(player);		
		}
		player.thePlayer.setDisplayName(player.thePlayer.getName());
		player.thePlayer.setCustomName(player.thePlayer.getName());
		player.thePlayer.setCustomNameVisible(false);
		player.thePlayer.getInventory().clear();
		player.thePlayer.getInventory().setHelmet(null);
		player.thePlayer.getInventory().setChestplate(null);
		player.thePlayer.getInventory().setLeggings(null);
		player.thePlayer.getInventory().setBoots(null);
		player.loadInventory();
		this.sendTeamMessage(null,ChatColor.GOLD+String.format(Main.gameEngine.dict.get("matchLeaveBroad"),player.getName()));
		for(PVPPlayer watcher : this.getPlayers())
		{
			Main.plsl.sendNamechange(player, watcher, true);
			Main.plsl.sendNamechange(watcher, player, true);
		}
	}
	
	public boolean isHardcore()
	{
		return hardcore;
	}
	
	public void setTeam(PVPPlayer p,Team t)
	{
		p.setTeam(t);
	}
	
	public boolean canKill(PVPPlayer killer,PVPPlayer victim)
	{
		if(killer == null)
		{
			return true;
		}
		return (killer.getTeam() != victim.getTeam() || this.hardcore);
	}
	
	public void kill(PVPPlayer killer,PVPPlayer victim,String weapon,boolean doKill)
	{
		if(doKill)
		{
			victim.normalDeathBlocked = true;
		}
		if(killer != null)
		{
			if(killer.isSpawned() && victim.isSpawned())
			{
				this.c4explosives.remove(victim);
				if(doKill)
				{
					victim.thePlayer.setHealth(0);
				}
				victim.onKill(killer);
				victim.setSpawned(false);
				if(killer == victim)
				{
					this.broadcastMessage(killer.getName()+" ["+weapon+"] "+Main.gameEngine.dict.get("suicide"));
				}
				else
				{
					if(gmode.equals(Gamemode.Teamdeathmatch))
					{
						killer.getTeam().addPoints(1);
					}
					killer.killed();
					this.checkKillstreak(killer);
					this.broadcastMessage(killer.getName()+" ["+weapon+"] "+victim.getName());
				}
			}
		}
		else
		{
			this.c4explosives.remove(victim);
			if(doKill)
			{
				victim.thePlayer.setHealth(0);
			}
			victim.onKill(killer);
			victim.setSpawned(false);
			this.broadcastMessage(victim.getName()+" "+Main.gameEngine.dict.get("died"));
		}
		if(gmode.equals(Gamemode.Conquest))
		{
			victim.getTeam().subPoints(1);
		}
		else if(gmode.equals(Gamemode.Rush))
		{
			if(victim.getTeam().equals(teamRed))
			{
				victim.getTeam().subPoints(1);
			}
		}
		victim.normalDeathBlocked = false;
	}
	
	public void doUpdate()
	{
		try
		{
			for(int i=0;i<playersBlue.size();i++)
			{
				PVPPlayer p = playersBlue.get(i);
				p.doUpdate();
			}
			for(int i=0;i<playersRed.size();i++)
			{
				PVPPlayer p = playersRed.get(i);
				p.doUpdate();
			}
			for(int i=0;i<ltcw.size();i++)
			{
				TickControlledWeapon tcw = ltcw.get(i);
				tcw.doUpdate();
			}
			for(int i=0;i<infSs.size();i++)
			{
				InformationSign infS = infSs.get(i);
				infS.doUpdate();
			}
			try
			{
				for(int i=0;i<resupplyStations.size();i++)
				{
					ResupplyStation rs = resupplyStations.get(i);
					rs.doUpdate();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			if(gmode.equals(Gamemode.Conquest))
			{
				int tmpflagsRed  = 0;
				int tmpflagsBlue = 0;
				double pointLossPerFlagPerSecond = Main.gameEngine.configuration.getPointlossPerFlagPerSecond(world);
				boolean needntHaveHalfFlags = Main.gameEngine.configuration.getLosePointsWhenEnemyHasLessThanHalfFlags(world);
				for(int i=0;i<flags.size();i++)
				{
					Flag flag = flags.get(i);
					flag.doUpdate();
					Team flagOwner = flag.getOwner();
					if(flagOwner != null)
					{
						if(flagOwner.equals(teamRed))
						{
							tmpflagsRed++;
						}
						else if(flagOwner.equals(teamBlue))
						{
							tmpflagsBlue++;
						}
					}
				}
				flagsRed = tmpflagsRed;
				flagsBlue = tmpflagsBlue;
				if(flagsRed != flagsBlue)
				{
					int flagDiff = Math.abs(flagsRed-flagsBlue);
					if(needntHaveHalfFlags)
					{
						if(flagsRed > flagsBlue)
						{
							teamBlue.subPoints(pointLossPerFlagPerSecond/100d*flagDiff);
						}
						else
						{		
							teamRed.subPoints(pointLossPerFlagPerSecond/100d*flagDiff);
						}
					}
					else
					{
						if(((double)flagsRed) > ((double)this.getFlagNum())/2d)
						{
							teamBlue.subPoints(pointLossPerFlagPerSecond/100d*(((double)flagsRed)-((double)this.getFlagNum())/2d));
						}
						else if(((double)flagsBlue) > ((double)this.getFlagNum())/2d)
						{
							teamRed.subPoints(pointLossPerFlagPerSecond/100d*(((double)flagsBlue)-((double)this.getFlagNum())/2d));
						}
					}
				}
			}
			else if(gmode.equals(Gamemode.Rush))
			{
				for(int i=0;i<radioStations.size();i++)
				{
					RadioStation rs = radioStations.get(i);
					rs.doUpdate();
				}
			}
			if(gmode.equals(Gamemode.Teamdeathmatch))
			{
				double pointLimit = Main.gameEngine.configuration.getPointsForGamemodeInWorld(world, gmode);
				if(teamRed.getPoints() >= pointLimit)
				{
					this.win(teamRed);
				}
				else if(teamBlue.getPoints() >= pointLimit)
				{
					this.win(teamBlue);
				}
			}
			else if(gmode.equals(Gamemode.Conquest) || gmode.equals(Gamemode.Rush))
			{
				if(teamRed.getPoints() <= 0d)
				{
					this.win(teamBlue);
				}
				else if(teamBlue.getPoints() <= 0d)
				{
					this.win(teamRed);
				}
			}
			if(timer > 6000)
			{
				timer = 0;
				this.sendTeamMessage(null,ChatColor.GOLD+"Tickets: "+teamRed.color+Integer.toString((int)Math.round(teamRed.getPoints()))+ChatColor.RESET+" | "+teamBlue.color+Integer.toString((int)Math.round(teamBlue.getPoints())));
				if(gmode.equals(Gamemode.Conquest))
				{
					this.sendTeamMessage(null,ChatColor.GOLD+"Flags: "+teamRed.color+Integer.toString(this.getFlagsRed())+ChatColor.RESET+" | "+teamBlue.color+Integer.toString(this.getFlagsBlue()));
					if(this.getFlagNum() - this.getFlagsRed() - this.getFlagsBlue() > 0)
					{
						this.sendTeamMessage(null,ChatColor.GOLD+String.format(Main.gameEngine.dict.get("uncapped"),Integer.toString(this.getFlagNum() - this.getFlagsRed() - this.getFlagsBlue())));
					}
				}
			}
			timer++;
		}
		catch(Exception ex)
		{
			Error error = new Error("Internal error!", "Error while performing tick-update: "+ex.getMessage(), "This is most certainly the developers fault. Please report this error to me.", this.getClass().getName(), ErrorSeverity.WARNING);
			ex.printStackTrace();
			ErrorReporter.reportError(error);
		}
	}
	
	public void broadcastMessage(String message)
	{
		for(int i=0;i<playersRed.size();i++)
		{
			PVPPlayer player = playersRed.get(i);
			player.thePlayer.sendMessage(message);
		}
		for(int i=0;i<playersBlue.size();i++)
		{
			PVPPlayer player = playersBlue.get(i);
			player.thePlayer.sendMessage(message);
		}		
	}

	public boolean playerDroppedItem(Item is, Player p)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			if(is.getItemStack().getType().equals(Material.CLAY_BALL))
			{
				float mod = Main.gameEngine.configuration.getM18KillRangeMod();
				Claymore clay = new Claymore(is, player,Main.gameEngine.configuration.getM18ExploStr(),this,mod);
				claymores.put(is, clay);
				return false;
			}
			else if(is.getItemStack().getType().equals(Material.IRON_INGOT))
			{
				float mod = Main.gameEngine.configuration.getHandGrenadeKillRangeMod();
				float throwSpeed = Main.gameEngine.configuration.getHandGrenadeThrowSpeed();
				if(p.isSprinting())
				{
					throwSpeed *= 2.0f;
				}
				HandGrenade grenade = new HandGrenade(is, player, this, Main.gameEngine.configuration.getHandGrenadeExploStr(), Main.gameEngine.configuration.getHandGrenadeFuse(), throwSpeed, mod);
				this.handGrenades.put(is, grenade);
				return false;
			}
			else if(is.getItemStack().getType().equals(Material.REDSTONE))
			{
				double triggerDist = Main.gameEngine.configuration.getIMSTriggerDist();
				int grenades = Main.gameEngine.configuration.getIMSShots();
				IMS ims = new IMS(this, is, triggerDist, grenades, player);
				this.imss.put(is, ims);
				return false;
			}
			else if(is.getItemStack().getType().equals(Material.INK_SACK) && (is.getItemStack().getDurability() == (short)4))
			{
				double throwSpeed = Main.gameEngine.configuration.getC4ThrowSpeed();
				if(player.thePlayer.isSprinting())
				{
					throwSpeed *= 2d;
				}
				double actualSpeed = is.getVelocity().length();
				double fact = throwSpeed/actualSpeed;
				is.setVelocity(is.getVelocity().multiply(fact));
				List<C4> c4s = c4explosives.get(player);
				if(c4s == null)
				{
					c4s = new ArrayList<C4>();
				}
				float mod = Main.gameEngine.configuration.getC4KillRangeMod();
				C4 explosive = new C4(null,is,Main.gameEngine.configuration.getC4ExploStr(),player,this,mod);
				c4s.add(explosive);
				c4explosives.put(player, c4s);			
				c4registry.add(is);
				return false;
			}
		}
		return Main.gameEngine.configuration.getPreventItemDrop(world, gmode);
	}

	public boolean playerPickUpItem(Item is, Player p)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			if(is.getItemStack().getType().equals(Material.CLAY_BALL))
			{
				Claymore clay = claymores.get(is);
				if(clay != null)
				{
					if(this.canKill(clay.owner,player))
					{
						claymores.remove(is);
						clay.explode();
						this.kill(clay.owner,player,"M18 CLAYMORE",player.thePlayer.getHealth() > 0);
					}
					return true;
				}
				return false;
			}
			else if(is.getItemStack().getType().equals(Material.IRON_INGOT))
			{
				HandGrenade hg = handGrenades.get(is);
				if(hg != null)
				{
					return true;
				}
			}
			else if(is.getItemStack().getType().equals(Material.REDSTONE))
			{
				IMS ims = imss.get(is);
				if(ims != null)
				{
					return true;
				}
			}
			else if(is.getItemStack().getType().equals(Material.INK_SACK))
			{
				if(c4registry.contains(is))
				{
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public boolean itemDespawn(Item is)
	{
		if(is.getItemStack().getType().equals(Material.CLAY_BALL))
		{
			if(claymores.get(is) != null)
			{
				return true;
			}
		}
		else if(is.getItemStack().getType().equals(Material.REDSTONE))
		{
			if(imss.get(is) != null)
			{
				return true;
			}
		}
		else if(is.getItemStack().getType().equals(Material.INK_SACK))
		{
			if(c4registry.contains(is))
			{
				return true;
			}
		}
		return false;
	}
	
	private void checkKillstreak(PVPPlayer player)
	{
		//TODO Whatever
	}
	
	public void endMatch()
	{
		for(int i=0;i<playersRed.size();i++)
		{
			PVPPlayer p = playersRed.get(i);
			p.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("matchend"),Integer.toString(p.points)));
			p.leaveMatch(matchLeaveLoc);
			
		}
		for(int i=0;i<playersBlue.size();i++)
		{
			PVPPlayer p = playersBlue.get(i);
			p.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("matchend"),Integer.toString(p.points)));
			p.leaveMatch(matchLeaveLoc);
		}
		if(this.canEnvironmentBeDamaged() || this.canExplosionsDamageEnvironment())
		{
			
		}
		else
		{
			for(Entry<PVPPlayer, SentryGun> sg : sentries.entrySet())
			{
				if(sg.getValue() != null && sg.getValue().dispenser.getBlock() != null)
				{
					sg.getValue().dispenser.getBlock().setTypeIdAndData(Material.AIR.getId(),(byte)0, true);
				}
			}
			for(Entry<PVPPlayer, List<C4>> c4hm : c4explosives.entrySet())
			{
				List<C4> c4s = c4hm.getValue();
				if(c4s != null)
				{
					for(C4 c4 : c4s)
					{
						c4.explode();
					}
				}
			}
		}
		teamRed = new TeamRed();
		teamBlue = new TeamBlue();
		playersBlue = new ArrayList<PVPPlayer>();
		playersRed = new ArrayList<PVPPlayer>();
		infSs = new ArrayList<InformationSign>();
		flags = new ArrayList<Flag>();
		claymores.clear();
		imss.clear();
		c4explosives.clear();
		handGrenades.clear();
		rpgs.clear();
		sentries.clear();
		sentryArrows.clear();
		Main.gameEngine.removeMatch(this);
	}

	public String playerDeath(Player entity, String deathMessage, List<ItemStack> drops)
	{
		PVPPlayer player = this.getPlayerExact(entity);
		if(player != null)
		{
			c4explosives.remove(player);
			if(Main.gameEngine.configuration.getPreventItemDropOnDeath(world, gmode))
			{
				drops.clear();
			}
			if(player.normalDeathBlocked)
			{
				return "";
			}
			Player killer = entity.getKiller();
			PVPPlayer PVPkiller = null;
			if(killer != null)
			{
				PVPkiller = this.getPlayerExact(killer);
			}
			String weapon = Main.gameEngine.dict.get("killed");
			deathMessage = deathMessage.toLowerCase();
			if((deathMessage.contains("shot") || deathMessage.contains("bow")) && PVPkiller.getCombatClass().wt == WeaponType.SNIPER)
			{
				weapon = "SNIPER";
			}
			if(this.canKill(PVPkiller, player))
			{
				kill(PVPkiller, player, weapon,false);
			}
			return "";
		}
		return deathMessage;
	}

	public boolean playerDamage(Player entity, DamageCause damageCause)
	{
		PVPPlayer player = this.getPlayerExact(entity);
		if(player != null)
		{
			if(player.isSpawned())
			{
				if(damageCause.equals(DamageCause.FALL))
				{
					return !Main.gameEngine.configuration.isFalldamageActive(world, gmode);
				}
				else if(damageCause.equals(DamageCause.ENTITY_EXPLOSION) || damageCause.equals(DamageCause.BLOCK_EXPLOSION))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}

	public void rightClickSign(Player p, Block clickedBlock)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null)
		{
			if(player.isSpawned())
			{
				return;
			}
			Block b = clickedBlock;
			Sign sign = (Sign)b.getState();
			String cmd = sign.getLines()[1];
			String joinCmd = Main.gameEngine.configuration.config.getString("GameControl.Sign.joinCmd");
			if(cmd.equalsIgnoreCase(joinCmd))
			{
				player.thePlayer.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("spawnmsg"),player.getTeam().color+player.getTeam().getName().toUpperCase()+ChatColor.RESET));
				this.spawnPlayer(player);
			}
			else
			{
				CombatClass cc = Main.gameEngine.combatClasses.get(cmd.toLowerCase().trim());
				if(cc != null)
				{
					this.setCombatClass(cc,player);
				}
			}
		}
	}

	private void setCombatClass(CombatClass cc, PVPPlayer player)
	{
		String name = cc.name;
		List<ItemStack> lis = cc.kit;
		PlayerInventory pi = player.thePlayer.getInventory();
		pi.clear();
		player.thePlayer.updateInventory();
		for(ItemStack is : lis)
		{
			pi.addItem(is);
		}
		pi.setHelmet(cc.armor[0]);
		pi.setChestplate(cc.armor[1]);
		pi.setLeggings(cc.armor[2]);
		pi.setBoots(cc.armor[3]);
		if(gmode.equals(Gamemode.Rush) || gmode.equals(Gamemode.Conquest))
		{
			if(gmode.equals(Gamemode.Rush))
			{
				pi.addItem(new ItemStack(Material.COMPASS,1));
				pi.addItem(new ItemStack(Material.REDSTONE_TORCH_ON,64));
				if(this.activeRadioStation != null)
				{
					player.thePlayer.setCompassTarget(activeRadioStation.getLocation());
				}
			}
		}
		player.thePlayer.updateInventory(); //Seems NOT to work without updateInventory
		player.thePlayer.sendMessage(String.format(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("classchange"),name));
		player.setCombatClass(cc);
	}
	private void spawnPlayer(PVPPlayer player)
	{
		player.setSpawned(true);
		Location loc = this.getSpawnLoc(player);
		player.hasMap = Main.gameEngine.configuration.isMinimapEnabled(this.world);
		if(player.hasMap)
		{
			player.getMapView().setCenterX(loc.getBlockX());
			player.getMapView().setCenterZ(loc.getBlockZ());
			player.thePlayer.getInventory().addItem(new ItemStack(Material.MAP,1,player.getMapView().getId()));
			player.thePlayer.updateInventory();
		}
		player.teleport(loc);
	}

	private Location getSpawnLoc(PVPPlayer p)
	{
		if(this.gmode.equals(Gamemode.Teamdeathmatch))
		{
			return this.spawnLoc;
		}
		if(this.gmode.equals(Gamemode.Rush))
		{
			if(activeRadioStation != null)
			{
				Location baseLoc = activeRadioStation.getLocation();
				double inner = 0;
				double outer = 0;
				if(teamRed.equals(p.getTeam()))
				{
					inner = Main.gameEngine.configuration.getAttackerInnerSpawnRadius(world);
					outer = Main.gameEngine.configuration.getAttackerOuterSpawnRadius(world);
				}
				else
				{
					inner = Main.gameEngine.configuration.getDefenderInnerSpawnRadius(world);
					outer = Main.gameEngine.configuration.getDefenderOuterSpawnRadius(world);
				}
				Random rand = new Random();
				double angle = rand.nextDouble()*Math.PI*2d;
				double rad = inner+rand.nextDouble()*(outer-inner);
				double x = Math.sin(angle)*rad;
				double z = Math.cos(angle)*rad;
				double avgHeight = 0;
				for(RadioStation rs : radioStations)
				{
					avgHeight += rs.getLocation().getY();
				}
				avgHeight /= radioStations.size();
				Location loc = baseLoc.clone().add(x,5d,z);
				List<Location> locs = TeleportUtil.getSafeTeleportLocations(loc,200);
				if(locs.size() > 0)
				{
					double hdiff = Double.MAX_VALUE;
					loc = locs.get(0);
					for(Location l : locs)
					{
						if(Math.abs(l.getY()-avgHeight) < hdiff)
						{
							hdiff = Math.abs(l.getY()-avgHeight);
							loc = l;
						}
					}
				}
				return loc;
			}
		}
		if(this.gmode.equals(Gamemode.Conquest))
		{
			List<Flag> teamFlags = new ArrayList<Flag>();
			for(Flag f : flags)
			{
				if(p.getTeam() == f.getOwner())
				{
					teamFlags.add(f);
				}
			}
			if(teamFlags.size() > 0)
			{
				Random rand = new Random();
				int flagNum = rand.nextInt(teamFlags.size());
				Flag flag = teamFlags.get(flagNum);
				Location baseLoc = flag.getLocation();
				double inner = 4d;
				double outer = 8d;
				double angle = rand.nextDouble()*Math.PI*2d;
				double rad = inner+rand.nextDouble()*(outer-inner);
				double x = Math.sin(angle)*rad;
				double z = Math.cos(angle)*rad;
				double avgHeight = 0;
				for(Flag f : flags)
				{
					avgHeight += f.getLocation().getY();
				}
				avgHeight /= flags.size();
				Location loc = baseLoc.clone().add(x,5d,z);
				List<Location> locs = TeleportUtil.getSafeTeleportLocations(loc,200);
				if(locs.size() > 0)
				{
					double hdiff = Double.MAX_VALUE;
					loc = locs.get(0);
					for(Location l : locs)
					{
						if(Math.abs(l.getY()-avgHeight) < hdiff)
						{
							hdiff = Math.abs(l.getY()-avgHeight);
							loc = l;
						}
					}
				}
				return loc;				
			}		
		}
		return this.spawnLoc;
	}

	public void playerChangedWorld(Player player)
	{
		PVPPlayer p = this.getPlayerExact(player);
		if(p != null)
		{
			p.leaveMatch(matchLeaveLoc);
		}
	}

	public void rightClickBlockWithLapis(Player p, Block clickedBlock,PlayerInventory playerInventory)
	{
		List<ProtectedArea> lpa = Main.gameEngine.configuration.protectionRegions.get(clickedBlock.getWorld());
		boolean isProtected = false;
		if(lpa != null)
		{
			for(ProtectedArea pa : lpa)
			{
				isProtected = pa.isBlockInsideRegion(clickedBlock);
				if(isProtected)
				{
					break;
				}
			}
		}
		if(clickedBlock.getType().equals(Material.BEDROCK) || isProtected)
		{
			return;
		}
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			List<C4> c4s = c4explosives.get(player);
			if(c4s == null)
			{
				c4s = new ArrayList<C4>();
			}
			float mod = Main.gameEngine.configuration.getC4KillRangeMod();
			C4 explosive = new C4(clickedBlock,null,Main.gameEngine.configuration.getC4ExploStr(),player,this,mod);
			c4s.add(explosive);
			c4explosives.put(player, c4s);
			InventorySyncCalls.removeItemStack(playerInventory, new ItemStack(Material.INK_SACK,1,(short)4));
		}
	}

	public void rightClickWithDiamond(Player p) 
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			List<C4> c4s = this.c4explosives.get(player);
			if(c4s != null)
			{
				for(C4 c4 : c4s)
				{
					c4.explode();
				}
			}
			this.c4explosives.remove(player);
		}
	}

	public void playerQuit(Player p)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null)
		{
			this.c4explosives.remove(player);
			player.leaveMatch(matchLeaveLoc);
		}
	}

	public int blockPlace(Player p, Block b)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			if(b.getType().equals(Material.DISPENSER))
			{
				SentryGun sg = new SentryGun(this,(Dispenser)b.getState(), player, Main.gameEngine.configuration.getSentryArrowSpeed(), Main.gameEngine.configuration.getSentryMissileSpeed(),Main.gameEngine.configuration.getSentryMissileExploStr(),Main.gameEngine.configuration.getSentryMissileKillRangeMod());
				this.sentries.put(player, sg);
			}
			else if(gmode.equals(Gamemode.Rush) && (b.getType().equals(Material.REDSTONE_TORCH_ON) || b.getType().equals(Material.REDSTONE_TORCH_OFF)))
			{
				if(activeRadioStation != null && activeRadioStation.getLocation().distance(b.getLocation()) < 3d)
				{
					return 0;
				}
				else
				{
					return 2;
				}
			}
			else if(b.getType().equals(Material.WALL_SIGN))
			{
				Location facing = ResupplyStation.getFacing((Sign)b.getState());
				if(b.getLocation().clone().add(facing).getBlock().getType().equals(Material.WOOL))
				{
					double ammoRefillDist = Main.gameEngine.configuration.getAmmoResupplyRange();
					double ammoRefillSpeed = Main.gameEngine.configuration.getAmmoResupplySpeed();
					int fill = Main.gameEngine.configuration.getAmmoResupplyAmount();
					this.resupplyStations.add(new ResupplyStation((Sign)b.getState(), this, player, ammoRefillDist, ammoRefillSpeed, fill));
				}
			}
			else if(!Main.gameEngine.configuration.canEvironmentBeDamaged(gmode, world))
			{
				return 2;
			}
			return 1;
		}
		return 2;
	}

	public void ClickWithWoodenSword(Player p, boolean rightClick)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			SentryGun sg = sentries.get(player);
			if(sg != null)
			{
				HashSet<Byte> trans = new HashSet<Byte>();
				trans.add((byte)31);
				trans.add((byte)0);
				trans.add((byte)20);
				trans.add((byte)102);
				Block b = p.getTargetBlock(trans,200);
				if(b != null)
				{
					if(rightClick)
					{
						SentryMissile sm = sg.shootMissile(b.getLocation().clone());
						if(sm == null)
						{
							player.thePlayer.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("sentry_missile"));
						}
						else
						{
							sentryMissiles.put(sm.getArrow(),sm);
						}
					}
					else
					{
						Arrow arr = sg.shoot(b.getLocation().clone());
						if(arr == null)
						{
							player.thePlayer.sendMessage(ChatColor.DARK_RED+Main.gameEngine.dict.get("sentry_ammo"));
						}
						else
						{
								sentryArrows.put(arr, sg);
						}
					}
				}
			}
		}
	}

	public boolean arrowLaunchedByPlayer(Player p, Arrow arrow)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			double critpProbab = Main.gameEngine.configuration.getCritProbability(world, gmode);
			Random rand = new Random();
			SimpleProjectile sp = new SimpleProjectile(player, ProjectileType.GENERAL, rand.nextDouble() <= critpProbab, 1.0d, arrow);
			if(player.getCombatClass() != null)
			{
				if(player.getCombatClass().wt.equals(WeaponType.SNIPER))
				{
					float speed = Main.gameEngine.configuration.getSniperMuzzleVelocity();
					/*double len = arrow.getVelocity().length();
					if(len != 0)
					{
						Vector vel = arrow.getVelocity().clone().multiply(speed/len);
						arrow.setVelocity(vel);
					}*/
					arrow.setVelocity(arrow.getVelocity().clone().multiply(speed));
					sp.type = ProjectileType.SNIPER;
				}
			}
			this.projectiles.put(arrow, sp);
			return false;
		}
		return true;
	}

	public void registerTickControlled(TickControlledWeapon tickControlledWeapon)
	{
		ltcw.add(tickControlledWeapon);
	}

	public void unregisterTickControlled(TickControlledWeapon tickControlledWeapon) 
	{
		ltcw.remove(tickControlledWeapon);
	}

	public void unregisterHandGrenade(Item item)
	{
		this.handGrenades.remove(item);
	}

	public String[] getInformationSignText()
	{
		String[] info = new String[4];
		info[0] = this.gmode.toString();
		info[1] = ChatColor.RED+"RED"+ChatColor.RESET+"|"+ChatColor.BLUE+"BLUE";
		info[2] = "Points:"+Integer.toString((int)Math.round(teamRed.getPoints()))+"|"+Integer.toString((int)Math.round(teamBlue.getPoints()));
		info[3] = "Players: "+Integer.toString(playersRed.size())+" | "+Integer.toString(playersBlue.size());
		return info;
	}
	
	public List<PVPPlayer> getPlayers()
	{
		List<PVPPlayer> players = new ArrayList<PVPPlayer>();
		for(int i=0;i<playersRed.size();i++)
		{
			players.add(playersRed.get(i));
		}
		for(int i=0;i<playersBlue.size();i++)
		{
			players.add(playersBlue.get(i));
		}
		return players;
	}
	
	public TeamRed getTeamRed()
	{
		return teamRed;
	}
	
	public TeamBlue getTeamBlue()
	{
		return teamBlue;
	}

	public int getFlagsRed()
	{
		return this.flagsRed;
	}

	public int getFlagsBlue()
	{
		return this.flagsBlue;
	}
	
	public int getFlagNum()
	{
		return this.flags.size();
	}
	
	public Location getMatchLeaveLoc()
	{
		return matchLeaveLoc;
	}
	
	public void sendTeamMessage(Team team, String message) //Team = NULL will send a message to all players
	{
		if(team != teamBlue)
		{
			for(int i=0;i<playersRed.size();i++)
			{
				try
				{
					PVPPlayer p = playersRed.get(i);
					p.thePlayer.sendMessage(message);
				}
				catch(Exception ex)
				{
					
				}
			}
		}
		else if(team != teamRed)
		{
			for(int i=0;i<playersBlue.size();i++)
			{
				try
				{
					PVPPlayer p = playersBlue.get(i);
					p.thePlayer.sendMessage(message);
				}
				catch(Exception ex)
				{
					
				}
			}			
		}
	}

	public boolean arrowHitPlayer(Player p, Arrow a,double damage)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null)
		{
			if(player.isSpawned())
			{
				boolean headshot = false;
				double deltaY = p.getLocation().clone().add(0d, 2d, 0d).getY()-a.getLocation().getY();
				double multi = 1d;
				if(deltaY > (2d/3d)) //Legshot
				{
					multi = Main.gameEngine.configuration.getLegshotDamageMultiplier(world, gmode);
				}
				else if(deltaY < (1d/3d)) //Headshot
				{
					headshot = true;
					multi = Main.gameEngine.configuration.getHeadshotDamageMultiplier(world, gmode);
				}
				SentryGun sg = this.sentryArrows.get(a);
				if(sg != null)
				{
					PVPPlayer attacker = sg.getOwner();
					if(attacker != null)
					{
						if(this.canKill(attacker, player))
						{
							if(headshot)
							{
								attacker.thePlayer.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("headshot")+"!");
							}
							player.normalDeathBlocked = true;
							player.thePlayer.damage((int)Math.round(damage*multi));
							if(player.thePlayer.getHealth() <= 0)
							{
								this.kill(attacker, player, "SENTRY", false);
								player.normalDeathBlocked = false;
								return false;
							}
							else
							{
								player.addKillhelper(attacker, (int)Math.round(damage*multi));
							}
							player.normalDeathBlocked = false;
						}
					}
					this.sentryArrows.remove(a);
					return !this.canKill(attacker, player);
				}
				SentryMissile sm = sentryMissiles.get(a);
				if(sm != null)
				{
					sm.explode();
				}
				SimpleProjectile sp = this.projectiles.get(a);
				if(sp != null)
				{
					PVPPlayer attacker = sp.shooter;
					if(attacker != null)
					{
						if(this.canKill(attacker, player))
						{
							switch(sp.type)
							{
								case GENERAL: damage = Main.gameEngine.configuration.getGeneralDamage(world, gmode); break;
								case SNIPER: damage = Main.gameEngine.configuration.getSniperDamage(world, gmode); break;
								default: break;
							}
							//Bukkit.getServer().broadcastMessage(sp.type.toString()+" "+damage);
							if(sp.isCritical)
							{
								attacker.thePlayer.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("crit")+"!");
								multi *= Main.gameEngine.configuration.getCritMultiplier(world, gmode);
							}
							if(headshot)
							{
								attacker.thePlayer.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("headshot")+"!");
							}
							//Bukkit.getServer().broadcastMessage(Double.toString((int)Math.round(damage*multi)));
							player.normalDeathBlocked = true;
							player.thePlayer.damage((int)Math.round(damage*multi));
							if(player.thePlayer.getHealth() <= 0)
							{
								this.kill(attacker, player, "M82A1", false);
								player.normalDeathBlocked = false;
								return false;
							}
							else
							{
								player.addKillhelper(attacker, (int)Math.round(damage*multi));
							}
							player.normalDeathBlocked = false;
						}
					}
					this.projectiles.remove(a);
					return !this.canKill(attacker, player);
				}
				return false;
			}
		}
		return true;
	}

	public String playerChangeTeam(PVPPlayer player)
	{
		Team from = player.getTeam();
		Team to = (from == teamRed ? teamBlue : teamRed);
		int numFrom = (from == teamRed ? playersRed.size() : playersBlue.size());
		int numTo = (to == teamRed ? playersRed.size() : playersBlue.size());
		boolean autoBalance = Main.gameEngine.configuration.getAutobalance(world, gmode);
		if((!autoBalance) || (numFrom > numTo))
		{
			this.kill(player, player, "", player.thePlayer.getHealth() > 0);
			this.setTeam(player,to);
			return ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("playerChangeTeam"),to.color+to.getName().toUpperCase()+ChatColor.DARK_GREEN);
		}
		else
		{
			return ChatColor.DARK_RED+Main.gameEngine.dict.get("teamchangeUnbalanced");
		}
	}

	public Location playerRespawn(Player p, Location respawnLocation)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null)
		{
			return this.classSelectLoc;
		}
		return respawnLocation;
	}

	public void rightClickWithBone(Player p)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			HashSet<Byte> trans = new HashSet<Byte>();
			trans.add((byte)31);
			trans.add((byte)0);
			trans.add((byte)20);
			trans.add((byte)102);
			Block b = player.thePlayer.getTargetBlock(trans, 200);
			if(b != null)
			{
				Location playerEyeLoc = player.thePlayer.getLocation().add(0d,2.0d,0d); 
				Vector locHelp = b.getLocation().subtract(playerEyeLoc).toVector();
				Location launchLoc = playerEyeLoc.add(locHelp.multiply(1.5d/locHelp.length()));
				Vector vec = b.getLocation().subtract(launchLoc).toVector();
				double maxSpeed = Main.gameEngine.configuration.getRPGMaxSpeed();
				double accel = Main.gameEngine.configuration.getRPGAcceleration();
				double throtle = Main.gameEngine.configuration.getRPGThrotle();
				double lifeTime = Main.gameEngine.configuration.getRPGLifeTime();
				double exploStr = Main.gameEngine.configuration.getRPGExploStr();
				Arrow arr = this.world.spawnArrow(launchLoc, locHelp.multiply(accel*maxSpeed/locHelp.length()), 1f, 1f);
				float mod = Main.gameEngine.configuration.getRPGKillRangeMod();
				RPG rpg = new RPG(this, arr, (float)exploStr, lifeTime, maxSpeed, accel, vec, throtle, player,mod);
				rpgs.put(arr, rpg);
				p.getInventory().removeItem(new ItemStack(Material.BONE, 1));
			}
		}
	}
	
	public void unregisterRPG(RPG rpg)
	{
		this.rpgs.remove(rpg.getProjectile());
	}

	public void arrowHit(Arrow arr) 
	{
		RPG rpg = this.rpgs.get(arr);
		if(rpg != null)
		{
			rpg.explode();
			return;
		}
		SentryMissile sm = this.sentryMissiles.get(arr);
		if(sm != null)
		{
			sm.explode();
			return;
		}
	}
	
	public List<PVPPlayer> getSpawnedPlayersNearLocation(Location loc, double dist)
	{
		List<PVPPlayer> nearPlayers = new ArrayList<PVPPlayer>();
		List<PVPPlayer> players = this.getPlayers();
		for(PVPPlayer p : players)
		{
			if(p.thePlayer.getLocation().distance(loc) <= dist && p.isSpawned())
			{
				nearPlayers.add(p);
			}
		}
		return nearPlayers;
	}
	
	public void unregisterIMS(Item item)
	{
		imss.remove(item);
	}

	public void radioStationDestroyed(RadioStation radioStation)
	{
		this.sendTeamMessage(teamRed,ChatColor.GOLD+Main.gameEngine.dict.get("rsDestroyed"));
		this.sendTeamMessage(teamBlue,ChatColor.GOLD+Main.gameEngine.dict.get("rsLost"));
		this.teamBlue.subPoints(1.0d);
		if(radioStationIterator.hasNext())
		{
			activeRadioStation = radioStationIterator.next();
			List<PVPPlayer> players = this.getPlayers();
			for(PVPPlayer p : players)
			{
				p.thePlayer.setCompassTarget(activeRadioStation.getLocation());
			}
		}
		else
		{
			this.teamBlue.setPoints(-1d);
		}
	}

	public int getRemainingStations()
	{
		return (int)Math.round(teamBlue.getPoints());
	}
	
	public double getTeamPoints(Team team)
	{
		if(teamRed.equals(team))
		{
			return teamRed.getPoints();
		}
		if(teamBlue.equals(team))
		{
			if(gmode.equals(Gamemode.Rush))
			{
				return (double)this.getRemainingStations();
			}
			return teamBlue.getPoints();
		}
		return 0d;
	}

	public int blockBreak(Player p, Block b)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null && player.isSpawned())
		{
			if(gmode.equals(Gamemode.Rush) && (b.getType().equals(Material.REDSTONE_TORCH_ON) || b.getType().equals(Material.REDSTONE_TORCH_OFF)))
			{
				if(activeRadioStation != null && activeRadioStation.getLocation().distance(b.getLocation()) < 3d)
				{
					return 0;
				}
				else
				{
					return 2;
				}
			}
			else if(!Main.gameEngine.configuration.canEvironmentBeDamaged(gmode, world))
			{
				return 2;
			}
			return 1;
		}
		return 2;
	}
	
	public void win(Team winner)
	{
		this.sendTeamMessage(winner,ChatColor.DARK_GREEN+Main.gameEngine.dict.get("won"));
		this.sendTeamMessage(winner.equals(teamRed) ? teamBlue : teamRed,ChatColor.DARK_RED+Main.gameEngine.dict.get("lost"));
		this.endMatch();
	}

	public void unregisterSentryMissile(SentryMissile sentryMissile) 
	{
		this.sentryMissiles.remove(sentryMissile.getArrow());
	}
	
	public int getPlayerNumRed()
	{
		return this.playersRed.size();
	}
	
	public int getPlayerNumBlue()
	{
		return this.playersBlue.size();
	}
	
	public List<Flag> getFlags()
	{
		return this.flags;
	}
	
	public List<RadioStation> getRadioStations()
	{
		return this.radioStations;
	}

	public boolean playerDamagePlayer(Player damager, Player damaged, int damage)
	{
		PVPPlayer attacker = this.getPlayerExact(damager);
		PVPPlayer player = this.getPlayerExact(damaged);
		if(attacker != null && player != null)
		{
			if(this.canKill(attacker, player))
			{
				player.addKillhelper(attacker, damage);
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	public String playerChat(String format, Player p)
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null)
		{
			format = "<"+player.getName()+"> %2$s";
		}
		return format;
	}

	public boolean foodLevelChange(Player p) 
	{
		PVPPlayer player = this.getPlayerExact(p);
		if(player != null)
		{
			return !Main.gameEngine.configuration.isHungerActive(world, gmode);
		}
		return true;
	}

	public void unregisterResupply(ResupplyStation resupplyStation) 
	{
		this.resupplyStations.remove(resupplyStation);
	}
	
	private void createFakeExplosion(PVPPlayer issuer, Location loc, float exploStr, boolean b, boolean doDamage, String weapon)
	{
		List<PVPPlayer> players = new ArrayList<PVPPlayer>(this.getPlayers());
		for(PVPPlayer p : players)
		{
			p.createFakeExplosion(loc, exploStr, b);
			if(issuer != null && doDamage)
			{
				if(this.canKill(issuer, p))
				{
					double dist = loc.distance(p.thePlayer.getLocation());
					double radius = 1.24d*exploStr;
					if(dist < radius)
					{
						double expo = 0.8d; //TODO
						double impact = (1d - dist/radius) * expo;
						double dmg = (Math.pow(impact, 2) + impact) * 8d * exploStr + 1d;
						p.thePlayer.damage((int)Math.floor(dmg));
						if(p.thePlayer.getHealth() <= 0)
						{
							this.kill(issuer, p, weapon, false);
						}
						else
						{
							p.addKillhelper(issuer, (int)Math.floor(dmg));
						}
					}
				}
			}
		}
	}

	public void createExplosion(PVPPlayer issuer, Location loc,float exploStr, float deathRange, String weapon)
	{
		boolean isProtected = false;
		if(this.canExplosionsDamageEnvironment())
		{
			List<ProtectedArea> lpa = Main.gameEngine.configuration.protectionRegions.get(loc.getWorld());
			if(lpa != null)
			{
				for(ProtectedArea pa : lpa)
				{
					isProtected = pa.isCoordInsideRegion(loc);
					if(isProtected)
					{
						break;
					}
				}
			}
		}
		else
		{
			isProtected = true;
		}
		if(!isProtected)
		{
			EffectSyncCalls.createExplosion(loc, exploStr);
			this.createFakeExplosion(issuer,loc, exploStr, false, true, weapon);
		}
		else
		{
			this.createFakeExplosion(issuer,loc, exploStr, true, true, weapon);
		}
		/*List<PVPPlayer> players = this.getSpawnedPlayersNearLocation(loc,deathRange);
		for(PVPPlayer p : players)
		{
			if(this.canKill(issuer,p))
			{
				this.kill(issuer,p,weapon,p.thePlayer.getHealth() > 0);
			}
		}*/
	}
}
