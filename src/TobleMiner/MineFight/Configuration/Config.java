package TobleMiner.MineFight.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Container.ClaymoreContainer;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.Configuration.Container.KillstreakConfig;
import TobleMiner.MineFight.Configuration.Container.PlayerSeekerContainer;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Score;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.Protection.ProtectedArea;
import TobleMiner.MineFight.Weapon.Projectile.ProjectileType;

public class Config
{
	public final HashMap<String,WorldConfig> configByWorldName = new HashMap<String,WorldConfig>();
	public final FileConfiguration config;
	private final Main mane;
	
	private final File file;
	
	public Config(Main mane,FileConfiguration config)
	{
		this.file = new File(mane.getPluginDir(),"mineFight.conf");
		this.config = config;
		this.mane = mane;
	}
	
	public void read()
	{
		this.load();
		List<World> worlds = Bukkit.getServer().getWorlds();
		this.configByWorldName.clear();
		for(World world : worlds)
		{
			this.configByWorldName.put(world.getName(), new WorldConfig(this.mane, world));
		}
		boolean makeConfig = config.getBoolean("config.reset",true);
		if(makeConfig)
		{
			config.set("CombatClass.Sniper.Kit","BOW:0,1;ARROW:0,64;CLAY_BALL:0,4;WOOD_SWORD:0,1;IRON_PICKAXE:0,1;IRON_SPADE:0,1;IRON_AXE:0,1;DIRT:0,64;LADDER:0,32");
			config.set("CombatClass.Sniper.Armor","LEATHER_HELMET,LEATHER_CHESTPLATE,LEATHER_LEGGINGS,LEATHER_BOOTS");
			config.set("CombatClass.Heavy.Kit","IRON_SWORD:0,1;DIAMOND:0,1;INK_SACK:4,5;IRON_PICKAXE:0,1;IRON_SPADE:0,1;IRON_AXE:0,1;DIRT:0,64;LADDER:0,32;BONE:0,3");
			config.set("CombatClass.Heavy.Armor","DIAMOND_HELMET,DIAMOND_CHESTPLATE,DIAMOND_LEGGINGS,DIAMOND_BOOTS");
			config.set("CombatClass.Engineer.Kit","IRON_SWORD:0,1;WOOD_SWORD:0,1;DISPENSER:0,1;ARROW:0,64;SULPHUR:0,16;IRON_PICKAXE:0,1;IRON_SPADE:0,1;IRON_AXE:0,1;DIRT:0,64;LADDER:0,32");
			config.set("CombatClass.Engineer.Armor","IRON_HELMET,IRON_CHESTPLATE,IRON_LEGGINGS,IRON_BOOTS");
			config.set("CombatClass.Medic.Kit","IRON_SWORD:0,1;GOLD_SWORD:0,1;ARROW:0,64;IRON_PICKAXE:0,1;IRON_SPADE:0,1;IRON_AXE:0,1;DIRT:0,64;LADDER:0,32");
			config.set("CombatClass.Medic.Armor","IRON_HELMET,IRON_CHESTPLATE,IRON_LEGGINGS,IRON_BOOTS");
			config.set("CombatClass.Pyro.Kit","IRON_SWORD:0,1;WOOD_SWORD:0,1;IRON_PICKAXE:0,1;IRON_SPADE:0,1;IRON_AXE:0,1;BLAZE_POWDER:0,64;BLAZE_POWDER:0,64;DIRT:0,64;LADDER:0,32");
			config.set("CombatClass.Pyro.Armor","IRON_HELMET,IRON_CHESTPLATE,IRON_LEGGINGS,IRON_BOOTS");
			config.set("GameControl.Sign.joinCmd","Join game");
			config.set("GameProps.C4.exploStr",4.0d);
			config.set("GameProps.C4.throwSpeed",0.5d);
			config.set("GameProps.C4.killRangeMod",0.5d);
			config.set("GameProps.C4.allowedInsideProtection",false);
			config.set("GameProps.M18.exploStr",4.0d);
			config.set("GameProps.M18.killRangeMod",0.5d);
			config.set("GameProps.M18.allowedInsideProtection",false);
			config.set("GameProps.sentry.projectileSpeed",4.5d);
			config.set("GameProps.sentry.missileSpeed",2.0d);
			config.set("GameProps.sentry.missileExploStr",3.0d);
			config.set("GameProps.sentry.missileKillRangeMod",0.5d);
			config.set("GameProps.sniperRifle.projectileSpeed",5.0d);
			config.set("GameProps.handGrenade.exploStr",4.0d);
			config.set("GameProps.handGrenade.fuse",3.0d);
			config.set("GameProps.handGrenade.throwSpeed",1.5d);
			config.set("GameProps.handGrenade.killRangeMod",0.5d);
			config.set("GameProps.handGrenade.allowedInsideProtection",true);
			config.set("GameProps.rpg.speed",1.5d);
			config.set("GameProps.rpg.accel",0.1d);
			config.set("GameProps.rpg.throtle",0.1d);
			config.set("GameProps.rpg.exploStr",3.0d);
			config.set("GameProps.rpg.lifeTime",10.0d);
			config.set("GameProps.rpg.killRangeMod",0.5d);
			config.set("GameProps.ims.triggerDist",12.0d);
			config.set("GameProps.ims.shots",6);
			config.set("GameProps.ims.allowedInsideProtection",false);
			config.set("GameProps.ims.grenade.speed",1.0d);
			config.set("GameProps.ims.grenade.height",20.0d);
			config.set("GameProps.ims.grenade.exploStr",4.0d);
			config.set("GameProps.ammoResupply.range", 4.0d);
			config.set("GameProps.ammoResupply.speed", 4.0d);
			config.set("GameProps.ammoResupply.amount", 20);
			config.set("GameProps.flamethrower.ignitionDist", 6.0d);
			config.set("GameProps.flamethrower.directDamage", 10d);
			config.set("GameProps.medigun.healingDist", 4.0d);
			config.set("GameProps.medigun.healingRate", 10d);
			config.set("GameProps.killassist.pointsPerPercent", 1d);
			config.set("GameProps.lang","EN_us.lang");
			config.set("GameProps.statistics.local.enabled",false);
			config.set("GameProps.statistics.local.instantUpdate.enabled",false);
			config.set("GameProps.statistics.global.enabled",false);
			config.set("GameProps.statistics.global.instantUpdate.enabled",false);
			config.set("config.reset",false);
			config.set("debug",false);
			this.save();
		}
	}
	
	public void load()
	{
		try
		{
			config.load(file.getAbsoluteFile());
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed loading configuration! This is normal if you start the plugin for the first time.","The pluginconfiguration could not be loaded: "+ex.getMessage(),"The plugin won't work until this error is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.SEVERE);
			ErrorReporter.reportError(error);
		}
	}
	
	public void save()
	{
		try
		{
			config.save(file.getAbsoluteFile());
			for(WorldConfig wcfg : this.configByWorldName.values())
			{
				wcfg.save();
			}
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed saving configuration!","The pluginconfiguration could not be saved: "+ex.getMessage(),"The plugin won't work until this error is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.SEVERE);
			ErrorReporter.reportError(error);
		}
		
	}
	
	public List<ProtectedArea> getProtectedAreasByWorld(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.protectedRegions;
		}
		Error err = new Error("Tried to get protected regions for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return null;
	}
	
	public Location getRoundEndSpawnForWorld(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getRoundEndSpawn();
		}
		Error err = new Error("Tried to fetch leave world for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return null;
	}
	
	public Location getRespawnForWorld(World w)
	{	
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getRespawnLoc();
		}
		Error err = new Error("Tried to fetch respawn point for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return null;
	}
	
	public Location getSpawnForWorld(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getSpawnLoc();
		}
		Error err = new Error("Tried to fetch spawn point for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return null;		
	}
	
	public float getC4ExploStr()
	{
		return (float)config.getDouble("GameProps.C4.exploStr");
	}
	
	public float getC4KillRangeMod()
	{
		return (float)config.getDouble("GameProps.C4.killRangeMod");
	}
	
	public float getM18ExploStr()
	{
		return (float)config.getDouble("GameProps.M18.exploStr");
	}

	public float getM18KillRangeMod()
	{
		return (float)config.getDouble("GameProps.M18.killRangeMod");
	}

	public float getSentryArrowSpeed()
	{
		return (float)config.getDouble("GameProps.sentry.projectileSpeed");
	}
	
	public float getSniperMuzzleVelocity()
	{
		return (float)config.getDouble("GameProps.sniperRifle.projectileSpeed");
	}
	
	public float getHandGrenadeExploStr()
	{
		return (float)config.getDouble("GameProps.handGrenade.exploStr");
	}

	public float getHandGrenadeFuse()
	{
		return (float)config.getDouble("GameProps.handGrenade.fuse");
	}
		
	public float getHandGrenadeThrowSpeed()
	{
		return (float)config.getDouble("GameProps.handGrenade.throwSpeed");
	}
	
	public float getHandGrenadeKillRangeMod()
	{
		return (float)config.getDouble("GameProps.handGrenade.killRangeMod");
	}
	
	public boolean isGamemodeEnabledInWorld(World w, Gamemode gmode)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.isGamemodeEnabled(gmode);
		}
		Error err = new Error("Tried to get gmode enabled for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;		
	}
	
	public int getPointsForGamemodeInWorld(World w, Gamemode gmode)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getTickets(gmode);
		}
		Error err = new Error("Tried to get max tickets for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 500;		
	}
	
	public void addInfoSign(Sign sign,Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(sign.getWorld().getName());
		if(wcfg != null)
		{
			wcfg.addInfoSign(gm,sign);
			return;
		}
		Error err = new Error("Tried to add info sign to unknown world.",String.format("World: \"%s\"", sign.getWorld().getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
	}
	
	public void removeInfoSign(Sign sign,Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(sign.getWorld().getName());
		if(wcfg != null)
		{
			wcfg.removeInfoSign(gm,sign);
			return;
		}
		Error err = new Error("Tried to remove info sign from unknown world.",String.format("World: \"%s\"", sign.getWorld().getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
	}
	
	public List<Sign> getInfoSigns(World world,Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(world.getName());
		if(wcfg != null)
		{
			return wcfg.getInfoSigns(gm);
		}
		Error err = new Error("Tried to get info signs for unknown world.",String.format("World: \"%s\"", world.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return new ArrayList<Sign>();
	}
	
	public boolean getPreventItemDropOnDeath(World world,Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(world.getName());
		if(wcfg != null)
		{
			return wcfg.getPreventItemDropOnDeath(gm);
		}
		Error err = new Error("Tried to get prevent item drop on death for unknown world.",String.format("World: \"%s\"", world.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}

	public boolean getPreventItemDrop(World world,Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(world.getName());
		if(wcfg != null)
		{
			return wcfg.getPreventItemDrop(gm);
		}
		Error err = new Error("Tried to get prevent item drop for unknown world.",String.format("World: \"%s\"", world.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public void addFlag(FlagContainer fc)
	{
		WorldConfig wcfg = this.configByWorldName.get(fc.sign.getWorld().getName());
		if(wcfg != null)
		{
			wcfg.addFlag(fc);
			return;
		}
		Error err = new Error("Tried to add flag to unknown world.",String.format("World: \"%s\"", fc.sign.getWorld().getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
	}
	
	public void removeFlag(Sign sign)
	{
		WorldConfig wcfg = this.configByWorldName.get(sign.getWorld().getName());
		if(wcfg != null)
		{
			wcfg.removeFlag(sign);
			return;
		}
		Error err = new Error("Tried to remove flag from unknown world.",String.format("World: \"%s\"", sign.getWorld().getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
	}
	
	public List<FlagContainer> getFlags(World world)
	{
		WorldConfig wcfg = this.configByWorldName.get(world.getName());
		if(wcfg != null)
		{
			return wcfg.getFlags();
		}
		Error err = new Error("Tried to get flags for unknown world.",String.format("World: \"%s\"", world.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return new ArrayList<FlagContainer>();
	}
	
	public double getFlagCaptureDistance(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getFlagCaptureDistance();
		}
		Error err = new Error("Tried to remove flag from unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 10d;
	}
	
	public double getFlagCaptureSpeed(World w) //% per second
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getFlagCaptureSpeed();
		}
		Error err = new Error("Tried to get flag capture speed for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 10d;
	}

	public double getFlagCaptureAcceleration(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getFlagCaptureAcceleration();
		}
		Error err = new Error("Tried to get flag capture acceleration for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 1.2d;
	}
	
	public double getPointlossPerFlagPerSecond(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getPointlossPerFlagPerSecond();
		}
		Error err = new Error("Tried to get flag pointloss for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 1.0d;
	}
	
	public boolean getLosePointsWhenEnemyHasLessThanHalfFlags(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getLosePointsWhenEnemyHasLessThanHalfFlags();
		}
		Error err = new Error("Tried to get lose points when enemy has less than half flags for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public boolean getAutobalance(World w,Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getAutobalance(gm);
		}
		Error err = new Error("Tried to get autobalance for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public double getRPGMaxSpeed()
	{
		return config.getDouble("GameProps.rpg.speed",1.5d);
	}
	
	public double getRPGAcceleration()
	{
		return config.getDouble("GameProps.rpg.accel",0.1d);
	}

	public double getRPGThrotle()
	{
		return config.getDouble("GameProps.rpg.throtle",0.1d);
	}
	
	public float getRPGExploStr()
	{
		return (float)config.getDouble("GameProps.rpg.exploStr",3.0d);
	}
	
	public double getRPGLifeTime()
	{
		return config.getDouble("GameProps.rpg.lifeTime",10.0d);
	}

	public float getRPGKillRangeMod()
	{
		return (float)config.getDouble("GameProps.rpg.killRangeMod",0.5d);
	}
	
	public double getIMSTriggerDist()
	{
		return config.getDouble("GameProps.ims.triggerDist",12.0d);
	}
	
	public int getIMSShots()
	{
		return config.getInt("GameProps.ims.shots",6);
	}
	
	public double getIMSGrenadeSpeed()
	{
		return config.getDouble("GameProps.ims.grenade.speed",1.0d);
	}
	
	public double getIMSGrenadePeekHeight()
	{
		return config.getDouble("GameProps.ims.grenade.height",20.0d);
	}
	
	public float getIMSGrenadeExploStr()
	{
		return (float)config.getDouble("GameProps.ims.grenade.exploStr",4.0d);
	}
	
	public List<RadioStationContainer> getRadioStations(World world)
	{
		WorldConfig wcfg = this.configByWorldName.get(world.getName());
		if(wcfg != null)
		{
			return wcfg.getRadioStations();
		}
		Error err = new Error("Tried to get radio stations for unknown world.",String.format("World: \"%s\"", world.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return new ArrayList<RadioStationContainer>();
	}
	
	public void addRadioStation(RadioStationContainer rsc)
	{
		WorldConfig wcfg = this.configByWorldName.get(rsc.sign.getWorld().getName());
		if(wcfg != null)
		{
			wcfg.addRadioStation(rsc);
		}
		Error err = new Error("Tried to add radio station to unknown world.",String.format("World: \"%s\"", rsc.sign.getWorld().getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
	}
	
	public void removeRadioStation(Sign sign)
	{
		WorldConfig wcfg = this.configByWorldName.get(sign.getWorld().getName());
		if(wcfg != null)
		{
			wcfg.removeRadioStation(sign);
		}
		Error err = new Error("Tried to remove radio station from unknown world.",String.format("World: \"%s\"", sign.getWorld().getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
	}
	
	public double getRadioStationDestructTime(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getRadioStationDestructTime();
		}
		Error err = new Error("Tried to get radio station detonation time for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 15.0d;
	}
	
	public double getDefenderInnerSpawnRadius(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getDefenderInnerSpawnRadius();
		}
		Error err = new Error("Tried to get inner defender spawn radius for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 3.0d;
	}
	
	public double getDefenderOuterSpawnRadius(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getDefenderOuterSpawnRadius();
		}
		Error err = new Error("Tried to get outer defender spawn radius for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 15.0d;
	}
	
	public double getAttackerInnerSpawnRadius(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getAttackerInnerSpawnRadius();
		}
		Error err = new Error("Tried to get inner attacker spawn radius for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 30.0d;
	}
	
	public double getAttackerOuterSpawnRadius(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getAttackerOuterSpawnRadius();
		}
		Error err = new Error("Tried to get outer attacker spawn radius for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 50.0d;
	}
	
	public double getSentryMissileSpeed()
	{
		return config.getDouble("GameProps.sentry.missileSpeed",2D);
	}
	
	public float getSentryMissileExploStr()
	{
		return (float)config.getDouble("GameProps.sentry.missileExploStr",3.0d);
	}
	
	public float getSentryMissileKillRangeMod()
	{
		return (float)config.getDouble("GameProps.sentry.missileKillRangeMod",0.5d);
	}
	
	public double getC4ThrowSpeed()
	{
		return config.getDouble("GameProps.C4.throwSpeed",0.5d);
	}
	
	public void addNewProtectedRegion(Location pos1, Location pos2)
	{
		WorldConfig wcfg = this.configByWorldName.get(pos1.getWorld().getName());
		if(wcfg != null)
		{
			wcfg.addProtectedRegion(pos1,pos2);
		}
		else
		{
			Error err = new Error("Tried to register protection for unknown world.",String.format("World: \"%s\"", pos1.getWorld().getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
			ErrorReporter.reportError(err);
		}
	}
	
	public boolean getC4allowedInsideProtection()
	{
		return config.getBoolean("GameProps.C4.allowedInsideProtection",false);
	}
	
	public boolean getM18allowedInsideProtection()
	{
		return config.getBoolean("GameProps.M18.allowedInsideProtection",false);
	}

	public boolean getHandGrenadeAllowedInsideProtection()
	{
		return config.getBoolean("GameProps.handGrenade.allowedInsideProtection",true);
	}

	public boolean getIMSAllowedInsideProtection()
	{
		return config.getBoolean("GameProps.ims.allowedInsideProtection",false);
	}
	
	public boolean isFalldamageActive(World w, Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.isFalldamageActive(gm);
		}
		Error err = new Error("Tried to get falldamage active for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public boolean isHungerActive(World w, Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.isHungerActive(gm);
		}
		Error err = new Error("Tried to get hunger active for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public double getAmmoResupplyRange()
	{
		return config.getDouble("GameProps.ammoResupply.range", 4.0d);
	}
		
	public double getAmmoResupplySpeed()
	{
		return config.getDouble("GameProps.ammoResupply.speed", 4.0d);
	}
	
	public int getAmmoResupplyAmount()
	{
		return config.getInt("GameProps.ammoResupply.amount", 20);
	}
	
	public double getFlamethrowerIgnitionDist()
	{
		return config.getDouble("GameProps.flamethrower.ignitionDist", 4.0d);
	}
	
	public int getFlamethrowerDirectDamage()
	{
		return config.getInt("GameProps.flamethrower.directDamage",3);
	}
	
	public double getMedigunHealingDist()
	{
		return config.getDouble("GameProps.medigun.healingDist", 12.0d);
	}
	
	public double getMedigunHealingRate()
	{
		return config.getDouble("GameProps.medigun.healingRate",1.0d);
	}
	
	public boolean isMpvpEnabled(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.isMpvpEnabled();
		}
		Error err = new Error("Tried to get mpvp enabled for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public String getLangFile()
	{
		return config.getString("GameProps.lang","EN_us.lang");
	}
	
	public boolean isDebuging()
	{
		return config.getBoolean("debug",true);
	}
	
	public double getKillassistPointModifier()
	{
		return config.getDouble("GameProps.killassist.pointsPerPercent",1d);
	}
	
	public boolean isMinimapEnabled(World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.isMinimapEnabled();
		}
		Error err = new Error("Tried to get minimap enabled for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public double getProjectileDamage(World w, Gamemode g, ProjectileType pt)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getProjectileDamage(g, pt);
		}
		Error err = new Error("Tried to get projectile damage for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 10.0d;
	}
		
	public double getHeadshotDamageMultiplier(World w, Gamemode g)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getHeadshotDamageMultiplier(g);
		}
		Error err = new Error("Tried to get headshot multiplier for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 2.0d;
	}
	
	public double getLegshotDamageMultiplier(World w, Gamemode g)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getLegshotDamageMultiplier(g);
		}
		Error err = new Error("Tried to get legshot multiplier for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 0.5d;
	}
	
	public double getCritProbability(World w, Gamemode g)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getCritProbability(g);
		}
		Error err = new Error("Tried to get crit probability for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 0.02d;
	}

	public double getCritMultiplier(World w, Gamemode g)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getCritMultiplier(g);
		}
		Error err = new Error("Tried to get crit multiplier for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 2.0d;
	}
	
	public boolean canEvironmentBeDamaged(Gamemode gmode, World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.canEvironmentBeDamaged(gmode);
		}
		Error err = new Error("Tried to get can environment be damaged for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public boolean canExlosionsDamageEnvironment(Gamemode gmode, World w)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.canExplosionDamageEvironment(gmode);
		}
		Error err = new Error("Tried to get can explosions damage environment for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}

	public boolean canEvironmentBeDamaged(World w) 
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.canEvironmentBeDamaged();
		}
		Error err = new Error("Tried to get can environment be damaged for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return false;
	}
	
	public double getScore(World w, Score s)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getScoreForAction(s);
		}
		Error err = new Error(String.format("Tried to get score for action \"%s\" for unknown world.",s.name()),String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 0d;
	}
	
	public boolean lStatisticsEnabled()
	{
		return config.getBoolean("GameProps.statistics.local.enabled",false);
	}
	
	public boolean gStatisticsEnabled()
	{
		return config.getBoolean("GameProps.statistics.global.enabled",false);
	}
	
	public boolean lStatInstantUpdateEnabled()
	{
		return config.getBoolean("GameProps.statistics.local.instantUpdate.enabled",false);
	}
	
	public boolean gStatInstantUpdateEnabled()
	{
		return config.getBoolean("GameProps.statistics.global.instantUpdate.enabled",false);
	}
	
	public ClaymoreContainer getClaymoreConfig(World w, Gamemode gm)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getClaymoreConfig(gm);
		}
		Error err = new Error("Tried to get claymoreConfigurationContainer for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return new ClaymoreContainer(5, true, true);
	}
	
	public int getInfoBeaconInterval(Gamemode gmode, World w) 
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getInfoBeaconInterval(gmode);
		}
		Error err = new Error("Tried to get infoBeaconInterval for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return 30;
	}
	
	public PlayerSeekerContainer getPlayerSeekerConf(World w, Gamemode gmode)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getPlayerSeekerConf(gmode);
		}
		Error err = new Error("Tried to get playerSeekerContainer for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return new PlayerSeekerContainer(20d, 10f, 10d, 10d, 5d, 5d);
	}
	
	public KillstreakConfig getKillstreaks(World w, Gamemode gmode)
	{
		WorldConfig wcfg = this.configByWorldName.get(w.getName());
		if(wcfg != null)
		{
			return wcfg.getKillstreaks(gmode);
		}
		Error err = new Error("Tried to get killstreaks for unknown world.",String.format("World: \"%s\"", w.getName()), "This probably means that your configuration isn't up to date.", this.getClass().getName(), ErrorSeverity.WARNING);
		ErrorReporter.reportError(err);
		return new KillstreakConfig();
	}

}
