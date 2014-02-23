package TobleMiner.MineFight.GameEngine.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.server.v1_6_R3.Packet60Explosion;
import net.minecraft.server.v1_6_R3.Vec3D;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Container.Killstreak;
import TobleMiner.MineFight.Configuration.Weapon.WeaponDescriptor;
import TobleMiner.MineFight.Configuration.Weapon.WeaponDescriptor.DamageType;
import TobleMiner.MineFight.Configuration.Weapon.WeaponDescriptor.WeaponUseType;
import TobleMiner.MineFight.Configuration.Weapon.WeaponIndex;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Score;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Statistics.StatType;
import TobleMiner.MineFight.GameEngine.Match.Statistics.StatUpdateType;
import TobleMiner.MineFight.GameEngine.Match.Team.Team;
import TobleMiner.MineFight.GameEngine.Player.CombatClass.CombatClass;
import TobleMiner.MineFight.GameEngine.Player.Info.MapInfoRenderer;
import TobleMiner.MineFight.Util.Util;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.InventorySyncCalls;

public class PVPPlayer
{
	public final Player thePlayer;
	private CombatClass combatClass;
	private boolean spawned = false;
	private Team team;
	private final Match match;
	public int kills;
	public int deaths;
	public int killstreak;
	private double points;
	private final HashMap<PVPPlayer,Killhelper> killHelpers = new HashMap<PVPPlayer,Killhelper>();
	public boolean normalDeathBlocked = false;
	public int timer = 1;
	private final MapView mv;
	private final MapInfoRenderer mir;
	private ItemStack[] inventoryBackup;
	private ItemStack helmetBackup;
	private ItemStack bodyarmorBackup;
	private ItemStack legginsBackup;
	private ItemStack bootBackup; //hehe
	public boolean hasMap;
	public final List<Killstreak> killstreaks = new ArrayList<Killstreak>();
	
	public PVPPlayer(Player thePlayer,Team team,Match match, MapView mv)
	{
		this.thePlayer = thePlayer;
		this.team = team;
		this.match = match;
		this.mv = mv;
		this.mv.setScale(Scale.CLOSEST);
		mv.addRenderer(this.mir = new MapInfoRenderer(match));
	}
	
	public void addKillhelper(PVPPlayer damager, double d)
	{
		if(damager != this && damager.getTeam() != this.getTeam())
		{
			Killhelper kh = this.killHelpers.get(damager);
			if(kh == null)
			{
				kh = new Killhelper(damager);
			}
			double maxHealth = this.thePlayer.getMaxHealth();
			kh.addDamage(d/maxHealth);
			this.killHelpers.put(damager, kh);
		}
	}
	
	public void storeInventory()
	{
		PlayerInventory pi = this.thePlayer.getInventory();
		this.inventoryBackup = pi.getContents();
		this.helmetBackup = pi.getHelmet();
		this.bodyarmorBackup = pi.getChestplate();
		this.legginsBackup = pi.getChestplate();
		this.bootBackup = pi.getBoots();
	}
	
	public void loadInventory()
	{
		if(this.inventoryBackup != null)
		{
			PlayerInventory pi = this.thePlayer.getInventory();
			pi.setContents(this.inventoryBackup);
			pi.setHelmet(this.helmetBackup);
			pi.setChestplate(this.bodyarmorBackup);
			pi.setLeggings(this.legginsBackup);
			pi.setBoots(this.bootBackup);
		}
	}
	
	public void setCombatClass(CombatClass cc)
	{
		this.combatClass = cc;
	}
	
	public CombatClass getCombatClass()
	{
		return this.combatClass;
	}
		
	public boolean isSpawned()
	{
		return spawned;
	}
	
	public String getName()
	{
		String name = this.thePlayer.getName();
		if(name.length() > 12)
		{
			name = name.substring(0,9)+"..";
		}
		return this.team.color+name+ChatColor.RESET;
	}
	
	public Player getPlayer()
	{
		return thePlayer;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public void setTeam(Team t)
	{
		if(match.anounceTeamchange(this,team,t))
		{
			this.team = t;
			this.thePlayer.setDisplayName(this.team.color+this.thePlayer.getName()+ChatColor.RESET);
		}
	}
	
	public void leaveMatch()
	{
		this.mv.removeRenderer(this.mir);
		match.anouncePlayerLeave(this);
		this.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("matchLeaveMsg"),points,((double)kills)/((double)deaths)));
	}
	
	public void leaveMatch(Location matchLeaveLoc)
	{
		this.leaveMatch();
		if(matchLeaveLoc != null)
		{
			this.thePlayer.teleport(matchLeaveLoc);
			this.thePlayer.sendMessage(ChatColor.DARK_GREEN+Main.gameEngine.dict.get("matchTpMsg"));
		}
	}
	
	public boolean createFakeExplosion(Location loc, float strength, boolean playSound)
	{
		try
		{
			Packet60Explosion pack = new Packet60Explosion(loc.getX(), loc.getY(), loc.getZ(), strength, new ArrayList<Object>(), Vec3D.a(0d, 0d, 0d));
			((CraftPlayer)(this.thePlayer)).getHandle().playerConnection.sendPacket(pack);
			if(playSound)
			{
				EffectSyncCalls.playSound(loc, Sound.EXPLODE, 63f, 0.5f);
			}
			return true;
		}
		catch(Exception ex)
		{
			Error err = new Error("Failed sending fake explosion packet!","Fake explosion could not be sent to "+thePlayer.getName()+".","This isn't normal at all, but it won't affect the gameply much.", this.getClass().getName(), ErrorSeverity.WARNING);
			ErrorReporter.reportError(err);
		}
		return false;
	}
	
	public Match getMatch()
	{
		return match;
	}
	
	public void onKill(PVPPlayer killer)
	{
		this.deaths++;
		this.killstreak = 0;
		Collection<Killhelper> khelpers = this.killHelpers.values(); 
		for(Killhelper kh : khelpers)
		{
			if(kh.damager != killer && kh.damager != null)
			{
				kh.damager.killAsist(kh.getDamage()*Main.gameEngine.configuration.getScore(kh.damager.thePlayer.getWorld(),Score.KILL));
			}
		}
		this.killHelpers.clear();
		this.setCombatClass(null);
		this.match.sh.updatePlayer(this, StatType.DEATHS, StatUpdateType.ADD, new Long(1L));
	}
	
	public void killAsist(double d)
	{
		this.addPoints(d);
		this.thePlayer.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("killassist"),d));
	}
	
	public void killed()
	{
		this.kills++;
		this.killstreak++;
		double p = Main.gameEngine.configuration.getScore(this.thePlayer.getWorld(),Score.KILL);
		this.addPoints(p);
		this.match.sh.updatePlayer(this, StatType.KILLS, StatUpdateType.ADD, new Long(1L));
	}
	
	public void flagCaptured()
	{
		double points = Main.gameEngine.configuration.getScore(this.match.getWorld(), Score.FLAGCAP);
		this.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("flagcappoints"),points));
		this.addPoints(points);
		this.match.sh.updatePlayer(this, StatType.FLAGCAP, StatUpdateType.ADD, new Long(1L));
	}

	public void teleport(Location loc)
	{
		this.thePlayer.teleport(loc);
	}
	
	public void setSpawned(boolean b)
	{
		this.spawned = b;
	}
	
	public void doUpdate()
	{
		if(this.thePlayer.isBlocking() && this.isSpawned() && this.combatClass != null)
		{
			PlayerInventory pi = this.thePlayer.getInventory();
			ItemStack inHand = pi.getItemInHand();
			if(inHand != null)
			{
				WeaponIndex wi = this.match.weapons.get(WeaponUseType.BLOCK);
				if(wi != null)
				{
					WeaponDescriptor wd = wi.get(inHand.getType());
					if(wd != null)
					{
						if(wd.cadence > 0 && (timer % ((int)Math.round(1200d / (double)wd.cadence))) == 0)
						{
							if(wd.ammomat == null || pi.contains(wd.ammomat))
							{
								if(wd.ammomat != null) InventorySyncCalls.removeItemStack(pi, new ItemStack(wd.ammomat,1));
								if(wd.dmgType == DamageType.PROJECTILEHIT)
								{
									Block b = this.thePlayer.getTargetBlock(null, 200);
									if(b != null)
									{
										Location playerEyeLoc = this.thePlayer.getLocation().add(0d,2.0d,0d); 
										Vector locHelp = b.getLocation().subtract(playerEyeLoc).toVector();
										if(locHelp.length() > 0)
										{
											double speed = wd.speed;
											Vector velocity = locHelp.clone().multiply(speed / locHelp.length());
											match.createWeaponProjectile(this, playerEyeLoc.clone().add(velocity.clone().multiply(1.5d/velocity.length())), velocity, wd, false);
										}
									}
								}
								else if(wd.dmgType == DamageType.FLAMETHROWER)
								{
									HashSet<Byte> trans = new HashSet<Byte>();
									trans.add((byte)31);
									trans.add((byte)0);
									trans.add((byte)20);
									trans.add((byte)102);
									Block b = this.thePlayer.getTargetBlock(trans, 200);
									if(b != null)
									{
										Location playerEyeLoc = this.thePlayer.getLocation().add(0d,1.0d,0d); 
										Vector locHelp = b.getLocation().subtract(playerEyeLoc).toVector();
										Location launchLoc = playerEyeLoc.add(locHelp.multiply(1.5d/locHelp.length()));
										launchLoc.getWorld().playEffect(launchLoc, Effect.MOBSPAWNER_FLAMES, 5);
										List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.thePlayer.getLocation(), (int)Math.round(wd.maxDist));
										PVPPlayer target = null;
										for(PVPPlayer p : players)
										{
											if(match.canKill(this,p) && p != this)
											{
												target = p;
												break;
											}
										}
										if(target != null)
										{
											target.normalDeathBlocked = true;
											target.thePlayer.damage(wd.getDamage(this.thePlayer.getLocation().distance(target.thePlayer.getLocation())) * target.thePlayer.getMaxHealth());
											if(target.thePlayer.getHealth() <= 0)
											{
												this.match.kill(this, target, wd.getName(), target.thePlayer.getHealth() > 0);
											}
											else
											{
												target.thePlayer.setFireTicks(100);
											}
											target.normalDeathBlocked = false;
										}
									}
									List<Block> potIgniBlocks = this.thePlayer.getLineOfSight(null,(int)Math.round(wd.maxDist));
									for(Block block : potIgniBlocks)
									{
										if(block.getType().isFlammable() && (!Util.protect.isBlockProtected(block)) && this.match.damageEnviron)
										{
											Util.block.ignite(block);
										}
									}
								}
								else if(wd.dmgType == DamageType.MEDIGUN)
								{
									List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.thePlayer.getLocation(), (int)Math.round(wd.maxDist));
									PVPPlayer target = null;
									for(PVPPlayer p : players)
									{
										if(p.getTeam() == this.getTeam() && p != this && p.thePlayer.getHealth() < p.thePlayer.getMaxHealth())
										{
											target = p;
											break;
										}
									}
									if(target != null)
									{
										Vector dir = target.thePlayer.getLocation().clone().subtract(this.thePlayer.getLocation().clone()).toVector();
										int len = (int)Math.round(dir.length());
										if(len != 0)
										{
											for(int i=0;i<=len;i++)
											{
												this.thePlayer.getWorld().playEffect(this.thePlayer.getLocation().clone().add(0d,1d,0d).add(dir.clone().multiply(((double)i)/((double)len))),Effect.ENDER_SIGNAL,0);
											}
										}
										double health = target.thePlayer.getHealth() - wd.getDamage(this.thePlayer.getLocation().distance(target.thePlayer.getLocation())) * target.thePlayer.getMaxHealth();
										if(health > target.thePlayer.getMaxHealth())
										{
											health = target.thePlayer.getMaxHealth();
										}
										target.thePlayer.setHealth(health);
									}
								}
							}
						}
					}
				}
			}
			timer++;	
		}
	}
	
	public MapView getMapView()
	{
		return this.mv;
	}
	
	private void addPoints(double points)
	{
		this.points += points;
		this.match.sh.updatePlayer(this, StatType.POINTS, StatUpdateType.ADD, new Double(points));
	}

	public void radioDestroyed()
	{
		double pDest = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RSDEST);
		this.addPoints(pDest);
		this.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("rsdestpoints"),pDest));
		this.match.sh.updatePlayer(this, StatType.RSDESTROY, StatUpdateType.ADD, new Long(1L));
	}
	
	public double getPoints()
	{
		return this.points;
	}

	public void resupplyGiven() 
	{
		double p = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RESUPPLY);
		this.addPoints(p);
		this.thePlayer.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("pointsResupply"), p));
	}

	public void radioArmed()
	{
		double pArm = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RSARM);
		this.addPoints(pArm);
		this.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("rsarmpoints"),pArm));
		this.match.sh.updatePlayer(this, StatType.RSARM, StatUpdateType.ADD, new Long(1L));
	}

	public void radioDisarmed()
	{
		double pDisarm = Main.gameEngine.configuration.getScore(this.match.getWorld(),Score.RSDISARM);
		this.addPoints(pDisarm);
		this.thePlayer.sendMessage(ChatColor.DARK_GREEN+String.format(Main.gameEngine.dict.get("rsdisarmpoints"),pDisarm));
		this.match.sh.updatePlayer(this, StatType.RSDISARM, StatUpdateType.ADD, new Long(1L));
	}

	public void addKillstreak(Killstreak ks) 
	{
		this.killstreaks.add(ks);
		this.thePlayer.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("killstreak"));
		this.thePlayer.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get(ks.transname));
		Inventory i = this.thePlayer.getInventory();
		switch(ks)
		{
			case IMS: InventorySyncCalls.addItemStack(i, new ItemStack(Material.REDSTONE)); break;
			case PLAYERSEEKER: InventorySyncCalls.addItemStack(i, new ItemStack(Material.STICK)); break;
		}		
	}
	
	public enum HitZone
	{
		HEAD("head"),
		TORSO("torso"),
		LEG("leg");
		
		public final String name;
		
		private HitZone(String name)
		{
			this.name = name;
		}
	}
}
