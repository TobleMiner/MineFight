package TobleMiner.MineFight.GameEngine.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.server.v1_5_R3.Packet60Explosion;
import net.minecraft.server.v1_5_R3.Vec3D;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Team.Team;
import TobleMiner.MineFight.GameEngine.Player.CombatClass.CombatClass;
import TobleMiner.MineFight.GameEngine.Player.Info.MapInfoRenderer;
import TobleMiner.MineFight.Util.Util;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.InventorySyncCalls;
import TobleMiner.MineFight.Weapon.WeaponType;

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
	public int points;
	private final HashMap<PVPPlayer,Killhelper> killHelpers = new HashMap<PVPPlayer,Killhelper>();
	public boolean normalDeathBlocked = false;
	public int timer = 1;
	private final double flamethrowerIgniDist;
	private final int flamethrowerDmg;
	private final double medigunHealingDist;
	private final double medigunHealingRate;
	private final MapView mv;
	private final MapInfoRenderer mir;
	private ItemStack[] inventoryBackup;
	private ItemStack helmetBackup;
	private ItemStack bodyarmorBackup;
	private ItemStack legginsBackup;
	private ItemStack bootBackup; //hehe
	public boolean hasMap;
	
	public PVPPlayer(Player thePlayer,Team team,Match match,double flamethrowerIgnDist,int flamethrowerDmg, double medigunHealingDist, double medigunHealingRate, MapView mv)
	{
		this.thePlayer = thePlayer;
		this.team = team;
		this.match = match;
		this.flamethrowerIgniDist = flamethrowerIgnDist;
		this.flamethrowerDmg = flamethrowerDmg;
		this.medigunHealingDist = medigunHealingDist;
		this.medigunHealingRate = medigunHealingRate;
		this.mv = mv;
		this.mv.setScale(Scale.CLOSEST);
		mv.addRenderer(this.mir = new MapInfoRenderer(match));
	}
	
	public void addKillhelper(PVPPlayer damager, int damage)
	{
		Killhelper kh = this.killHelpers.get(damager);
		if(kh == null)
		{
			kh = new Killhelper(damager);
		}
		int maxHealth = this.thePlayer.getMaxHealth();
		kh.addDamage(100d*(((double)damage)/((double)maxHealth)));
		this.killHelpers.put(damager, kh);
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
		return this.team.color+this.thePlayer.getName()+ChatColor.RESET;
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
				kh.damager.killAsist((int)Math.round(kh.getDamage()));
			}
		}
		this.killHelpers.clear();
	}
	
	public void killAsist(int points)
	{
		this.points += points;
		this.thePlayer.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("killassist"),Integer.toString(points)));
	}
	
	public void killed()
	{
		this.kills++;
		this.killstreak++;
		points += 100;
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
		CombatClass cc = this.getCombatClass();
		if(this.thePlayer.isBlocking() && this.isSpawned() && this.combatClass != null)
		{
			if(timer > 10)
			{
				timer = 0;
				PlayerInventory pi = this.thePlayer.getInventory();
				if(cc.wt == WeaponType.FLAMETHROWER)
				{
					if(pi.contains(Material.BLAZE_POWDER))
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
							List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.thePlayer.getLocation(),this.flamethrowerIgniDist);
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
								target.thePlayer.damage(flamethrowerDmg);
								if(target.thePlayer.getHealth() <= 0)
								{
									this.match.kill(this, target,"FLAMETHROWER", target.thePlayer.getHealth() > 0);
								}
								else
								{
									target.thePlayer.setFireTicks(100);
								}
								target.normalDeathBlocked = false;
							}
							InventorySyncCalls.removeItemStack(pi, new ItemStack(Material.BLAZE_POWDER,1));
						}
						List<Block> potIgniBlocks = this.thePlayer.getLineOfSight(null,(int)Math.round(this.flamethrowerIgniDist));
						for(Block block : potIgniBlocks)
						{
							if(block.getType().isFlammable() && (!Util.protect.isBlockProtected(block)))
							{
								Util.block.ignite(block);
							}
						}
					}
				}
				else if(cc.wt == WeaponType.MEDIGUN)
				{
					if(pi.getItemInHand() != null && pi.getItemInHand().getType() == Material.GOLD_SWORD)
					{
						List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.thePlayer.getLocation(),this.medigunHealingDist);
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
							if(target.getTeam() == this.getTeam() && target != this && target.thePlayer.getHealth() < 20)
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
								int health = target.thePlayer.getHealth()+(int)this.medigunHealingRate;
								if(health > 20)
								{
									health = 20;
								}
								target.thePlayer.setHealth(health);
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
}
