package TobleMiner.MineFight.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.Protection.ProtectedArea;

public class Config
{
	public final HashMap<World,List<ProtectedArea>> protectionRegions = new HashMap<World,List<ProtectedArea>>();
	public final FileConfiguration config;
	
	private final String flname = "plugins"+File.separator+"MineFight"+File.separator+"mineFight.conf";
	
	public Config(FileConfiguration config)
	{
		this.config = config;
	}
	
	public void read()
	{
		this.load();
		boolean makeConfig = config.getBoolean("config.reset",true);
		if(makeConfig)
		{
			List<World> worlds = Bukkit.getServer().getWorlds();
			for(int i=0;i<worlds.size();i++)
			{
				World world = worlds.get(i);
				String name = world.getName();
				Location spawn = world.getSpawnLocation();
				String wpref = "Worlds."+name;
				config.set(wpref+".mpvp",false);
				config.set(wpref+".classSelection.X",spawn.getBlockX());
				config.set(wpref+".classSelection.Y",spawn.getBlockY());
				config.set(wpref+".classSelection.Z",spawn.getBlockZ());
				config.set(wpref+".battleSpawn.X",spawn.getBlockX());
				config.set(wpref+".battleSpawn.Y",spawn.getBlockY());
				config.set(wpref+".battleSpawn.Z",spawn.getBlockZ());
				ConfigurationSection cs = config.createSection("Worlds."+name+".protections");
				String s = Integer.toString(spawn.getBlockX())+","+Integer.toString(spawn.getBlockY())+","+Integer.toString(spawn.getBlockZ());
				cs.set(s+".pos1.X",spawn.getBlockX());
				cs.set(s+".pos1.Y",spawn.getBlockY());
				cs.set(s+".pos1.Z",spawn.getBlockZ());
				cs.set(s+".pos2.X",spawn.getBlockX());
				cs.set(s+".pos2.Y",spawn.getBlockY());
				cs.set(s+".pos2.Z",spawn.getBlockZ());
				cs.set(s+".enabled",true);
				for(Gamemode gmode : Gamemode.values())
				{
					String wgmpref = wpref+".gamemodes."+gmode.toString().toLowerCase();
					config.set(wgmpref+".points",500);
					config.set(wgmpref+".enabled",true);					
					config.set(wgmpref+".autobalance",true);					
					config.set(wgmpref+".player.preventItemDropOnDeath",true);					
					config.set(wgmpref+".player.preventItemDrop",true);					
					config.set(wgmpref+".player.enableFallDamage",true);					
					config.set(wgmpref+".player.enableHunger",false);					
					config.createSection(wgmpref+".infoSigns");
					if(gmode.equals(Gamemode.Conquest))
					{
						config.createSection(wgmpref+".flags");
						config.set(wgmpref+".flagCaptureDistance",10);
						config.set(wgmpref+".flagCaptureSpeed",10);
						config.set(wgmpref+".flagCaptureAccelerationPerPerson",1.2d);
						config.set(wgmpref+".pointlossPerFlagPerSecond",1.0d);
						config.set(wgmpref+".losePointsWhenEnemyHasLessThanHalfFlags",false);
					}
					else if(gmode.equals(Gamemode.Rush))
					{						
						config.createSection(wgmpref+".radioStations");
						config.set(wgmpref+".destructTime",10d);
						config.set(wgmpref+".defenderOuterSpawnRadius",20d);
						config.set(wgmpref+".defenderInnerSpawnRadius",5d);
						config.set(wgmpref+".attackerOuterSpawnRadius",70d);
						config.set(wgmpref+".attackerInnerSpawnRadius",30d);
					}
					config.set(wgmpref+".weapon.sniperDamage",10d);					
					config.set(wgmpref+".weapon.generalDamage",5d);					
					config.set(wgmpref+".weapon.headshotMultiplier",2d);					
					config.set(wgmpref+".weapon.legshotMultiplier",0.5d);					
					config.set(wgmpref+".weapon.critProbability",0.02d);					
					config.set(wgmpref+".weapon.critMultiplier",2d);					
					config.set(wgmpref+".environment.canBeDamaged",true);					
					config.set(wgmpref+".environment.doExplosionsDamageEnvironment",true);					
				}
				config.set(wpref+".environment.canBeDamaged",true);
				config.set(wpref+".environment.doExplosionsDamageEnvironment",true);
				config.set(wpref+".leaveWorld","world");
				config.set(wpref+".minimapEnabled",true);
			}
			config.set("CombatClass.Sniper.Kit","261:0,1;262:0,64;337:0,4;268:0,1");
			config.set("CombatClass.Sniper.Armor","298,299,300,301");
			config.set("CombatClass.Heavy.Kit","267:0,1;264:0,1;351:4,5");
			config.set("CombatClass.Heavy.Armor","310,311,312,313");
			config.set("CombatClass.Engineer.Kit","267:0,1;268:0,1;23:0,1;262:0,64");
			config.set("CombatClass.Engineer.Armor","306,307,308,309");
			config.set("CombatClass.Medic.Kit","267:0,1;268:0,1;23:0,1;262:0,64");
			config.set("CombatClass.Medic.Armor","306,307,308,309");
			config.set("CombatClass.Pyro.Kit","267:0,1;268:0,1;23:0,1;262:0,64");
			config.set("CombatClass.Pyro.Armor","306,307,308,309");
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
			config.set("GameProps.flamethrower.ignitionDist", 4.0d);
			config.set("GameProps.flamethrower.directDamage", 1);
			config.set("GameProps.medigun.healingDist", 4.0d);
			config.set("GameProps.medigun.healingRate", 1);
			config.set("GameProps.killassist.pointsPerPercent", 1d);
			config.set("GameProps.lang","EN_us.lang");
			config.set("config.reset",false);
			config.set("debug",false);
			this.save();
		}
		this.protectionRegions.clear();
		List<World> worlds = Bukkit.getServer().getWorlds();
		for(int i=0;i<worlds.size();i++)
		{
			World world = worlds.get(i);
			ConfigurationSection cs = config.getConfigurationSection("Worlds."+world.getName()+".protections");
			List<ProtectedArea> lpa = new ArrayList<ProtectedArea>();
			Set<String> keys = cs.getValues(false).keySet();
			for(String key : keys)
			{
				double x1 = cs.getDouble(key+".pos1.X");
				double y1 = cs.getDouble(key+".pos1.Y");
				double z1 = cs.getDouble(key+".pos1.Z");
				double x2 = cs.getDouble(key+".pos2.X");
				double y2 = cs.getDouble(key+".pos2.Y");
				double z2 = cs.getDouble(key+".pos2.Z");
				if(this.isProtectionEnabled(cs,key))
				{
					lpa.add(new ProtectedArea(new Location(world, x1, y1, z1), new Location(world, x2, y2, z2)));
				}
			}
			this.protectionRegions.put(world, lpa);
		}
	}
	
	public void load()
	{
		try
		{
			config.load(flname);
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
			config.save(flname);
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed saving configuration!","The pluginconfiguration could not be saved: "+ex.getMessage(),"The plugin won't work until this error is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.SEVERE);
			ErrorReporter.reportError(error);
		}
		
	}
	
	public Location getRoundEndSpawnForWorld(World w)
	{
		String worldStr = config.getString("Worlds."+w.getName()+".leaveWorld");
		if(worldStr != null)
		{
			World world = Bukkit.getServer().getWorld(worldStr);
			if(world != null)
			{
				return world.getSpawnLocation();
			}
		}
		return null;
	}

	public boolean isProtectionEnabled(ConfigurationSection cs,String key)
	{
		return 	cs.getBoolean(key+".enabled",true);
	}
	
	public Location getRespawnForWorld(World w)
	{
		Double x = config.getDouble("Worlds."+w.getName()+".classSelection.X");
		Double y = config.getDouble("Worlds."+w.getName()+".classSelection.Y");
		Double z = config.getDouble("Worlds."+w.getName()+".classSelection.Z");
		return new Location(w, x, y, z);
	}
	
	public Location getSpawnForWorld(World w)
	{
		Double x = config.getDouble("Worlds."+w.getName()+".battleSpawn.X");
		Double y = config.getDouble("Worlds."+w.getName()+".battleSpawn.Y");
		Double z = config.getDouble("Worlds."+w.getName()+".battleSpawn.Z");
		return new Location(w, x, y, z);
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
		return config.getBoolean("Worlds."+w.getName()+".gamemodes."+gmode.toString().toLowerCase()+".enabled",false);
	}
	
	public int getPointsForGamemodeInWorld(World w, Gamemode gmode)
	{
		return config.getInt("Worlds."+w.getName()+".gamemodes."+gmode.toString().toLowerCase()+".points");
	}
	
	public void addInfoSign(Sign sign,Gamemode gm)
	{
		ConfigurationSection cs = config.getConfigurationSection("Worlds."+sign.getWorld().getName()+".gamemodes."+gm.toString().toLowerCase()+".infoSigns");
		if(cs != null)
		{
			String signName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(signName+".X",sign.getLocation().getX());
			cs.set(signName+".Y",sign.getLocation().getY());
			cs.set(signName+".Z",sign.getLocation().getZ());
			this.save();
		}
	}
	
	public void removeInfoSign(Sign sign,Gamemode gm)
	{
		ConfigurationSection cs = config.getConfigurationSection("Worlds."+sign.getWorld().getName()+".gamemodes."+gm.toString().toLowerCase()+".infoSigns");
		if(cs != null)
		{
			String signName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(signName+".X",null);
			cs.set(signName+".Y",null);
			cs.set(signName+".Z",null);
			cs.set(signName,null);
			this.save();
		}		
	}
	
	public List<Sign> getInfoSigns(World world,Gamemode gm)
	{
		List<Sign> signs = new ArrayList<Sign>();
		try
		{
			ConfigurationSection cs = config.getConfigurationSection("Worlds."+world.getName()+".gamemodes."+gm.toString().toLowerCase()+".infoSigns");
			if(cs != null)
			{
				for(String s : cs.getValues(false).keySet())
				{
					Location loc = new Location(world,cs.getDouble(s+".X"),cs.getDouble(s+".Y"),cs.getDouble(s+".Z"));
					Block b = world.getBlockAt(loc);
					if(b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST))
					{
						signs.add((Sign)b.getState());
					}
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return signs;
	}
	
	public boolean getPreventItemDropOnDeath(World world,Gamemode gm)
	{
		return config.getBoolean("Worlds."+world.getName()+".gamemodes."+gm.toString().toLowerCase()+".player.preventItemDropOnDeath",true);
	}

	public boolean getPreventItemDrop(World world,Gamemode gm)
	{
		return config.getBoolean("Worlds."+world.getName()+".gamemodes."+gm.toString().toLowerCase()+".player.preventItemDrop",true);
	}
	
	public void addFlag(Sign sign)
	{
		ConfigurationSection cs = config.getConfigurationSection("Worlds."+sign.getWorld().getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flags");
		if(cs != null)
		{
			String flagName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(flagName+".X",sign.getLocation().getX());
			cs.set(flagName+".Y",sign.getLocation().getY());
			cs.set(flagName+".Z",sign.getLocation().getZ());
			this.save();
		}
	}
	
	public void removeFlag(Sign sign)
	{
		ConfigurationSection cs = config.getConfigurationSection("Worlds."+sign.getWorld().getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flags");
		if(cs != null)
		{
			String flagName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(flagName+".X",null);
			cs.set(flagName+".Y",null);
			cs.set(flagName+".Z",null);
			cs.set(flagName,null);
			this.save();
		}		
	}
	
	public List<Sign> getFlags(World world)
	{
		List<Sign> signs = new ArrayList<Sign>();
		try
		{
			ConfigurationSection cs = config.getConfigurationSection("Worlds."+world.getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flags");
			if(cs != null)
			{
				for(String s : cs.getValues(false).keySet())
				{
					Location loc = new Location(world,cs.getDouble(s+".X"),cs.getDouble(s+".Y"),cs.getDouble(s+".Z"));
					Block b = world.getBlockAt(loc);
					if(b.getType().equals(Material.WALL_SIGN))
					{
						signs.add((Sign)b.getState());
					}
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return signs;
	}
	
	public double getFlagCaptureDistance(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flagCaptureDistance",10d);
	}
	
	public double getFlagCaptureSpeed(World w) //% per second
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flagCaptureSpeed",10d);
	}

	public double getFlagCaptureAcceleration(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flagCaptureAccelerationPerPerson",1.2d);
	}
	
	public double getPointlossPerFlagPerSecond(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".pointlossPerFlagPerSecond",1.0d);
	}
	
	public boolean getLosePointsWhenEnemyHasLessThanHalfFlags(World w)
	{
		return config.getBoolean("Worlds."+w.getName()+".gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".losePointsWhenEnemyHasLessThanHalfFlags",false);
	}
	
	public boolean getAutobalance(World w,Gamemode gm)
	{
		return config.getBoolean("Worlds."+w.getName()+".gamemodes."+gm.toString().toLowerCase()+".autobalance",true);		
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
	
	public List<Sign> getRadioStations(World world)
	{
		List<Sign> signs = new ArrayList<Sign>();
		try
		{
			ConfigurationSection cs = config.getConfigurationSection("Worlds."+world.getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".radioStations");
			if(cs != null)
			{
				for(String s : cs.getValues(false).keySet())
				{
					Location loc = new Location(world,cs.getDouble(s+".X"),cs.getDouble(s+".Y"),cs.getDouble(s+".Z"));
					Block b = world.getBlockAt(loc);
					if(b.getType().equals(Material.WALL_SIGN))
					{
						signs.add((Sign)b.getState());
					}
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return signs;
	}
	
	public void addRadioStation(Sign sign)
	{
		ConfigurationSection cs = config.getConfigurationSection("Worlds."+sign.getWorld().getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".radioStations");
		if(cs != null)
		{
			String rsName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(rsName+".X",sign.getLocation().getX());
			cs.set(rsName+".Y",sign.getLocation().getY());
			cs.set(rsName+".Z",sign.getLocation().getZ());
			this.save();
		}
	}
	
	public void removeRadioStation(Sign sign)
	{
		ConfigurationSection cs = config.getConfigurationSection("Worlds."+sign.getWorld().getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".radioStations");
		if(cs != null)
		{
			String rsName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(rsName+".X",null);
			cs.set(rsName+".Y",null);
			cs.set(rsName+".Z",null);
			cs.set(rsName,null);
			this.save();
		}		
	}
	
	public double getRadioStationDestructTime(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".destructTime",10d);
	}
	
	public double getDefenderInnerSpawnRadius(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".defenderInnerSpawnRadius",5d);
		
	}
	
	public double getDefenderOuterSpawnRadius(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".defenderOuterSpawnRadius",10d);
		
	}
	
	public double getAttackerInnerSpawnRadius(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".attackerInnerSpawnRadius",30d);
		
	}
	
	public double getAttackerOuterSpawnRadius(World w)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+Gamemode.Rush.toString().toLowerCase()+".attackerOuterSpawnRadius",70d);
		
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
		ConfigurationSection cs = config.getConfigurationSection("Worlds."+pos1.getWorld().getName()+".protections");
		String prefix = Integer.toString(pos1.getBlockX())+","+Integer.toString(pos1.getBlockY())+","+Integer.toString(pos1.getBlockZ());
		cs.set(prefix+".pos1.X", pos1.getX());
		cs.set(prefix+".pos1.Y", pos1.getY());
		cs.set(prefix+".pos1.Z", pos1.getZ());
		cs.set(prefix+".pos2.X", pos2.getX());
		cs.set(prefix+".pos2.Y", pos2.getY());
		cs.set(prefix+".pos2.Z", pos2.getZ());
		cs.set(prefix+".enabled",true);
		this.save();
		List<ProtectedArea> lpa = new ArrayList<ProtectedArea>();
		Set<String> keys = cs.getValues(false).keySet();
		for(String key : keys)
		{
			double x1 = cs.getDouble(key+".pos1.X");
			double y1 = cs.getDouble(key+".pos1.Y");
			double z1 = cs.getDouble(key+".pos1.Z");
			double x2 = cs.getDouble(key+".pos2.X");
			double y2 = cs.getDouble(key+".pos2.Y");
			double z2 = cs.getDouble(key+".pos2.Z");
			if(this.isProtectionEnabled(cs,key))
			{
				lpa.add(new ProtectedArea(new Location(pos1.getWorld(), x1, y1, z1), new Location(pos1.getWorld(), x2, y2, z2)));
			}
		}
		this.protectionRegions.put(pos1.getWorld(), lpa);
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
		return config.getBoolean("Worlds."+w.getName()+".gamemodes."+gm.toString().toLowerCase()+".player.enableFallDamage",true);
	}
	
	public boolean isHungerActive(World w, Gamemode gm)
	{
		return config.getBoolean("Worlds."+w.getName()+".gamemodes."+gm.toString().toLowerCase()+".player.enableHunger",false);
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
		return config.getBoolean("Worlds."+w.getName()+".mpvp",false);
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
		return config.getBoolean("Worlds."+w.getName()+".minimapEnabled",true);	
	}
	
	public double getSniperDamage(World w, Gamemode g)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+g.toString().toLowerCase()+".weapon.sniperDamage",10d);					
	}
	
	public double getGeneralDamage(World w, Gamemode g)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+g.toString().toLowerCase()+".weapon.generalDamage",5d);						
	}
	
	public double getHeadshotDamageMultiplier(World w, Gamemode g)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+g.toString().toLowerCase()+".weapon.headshotMultiplier",2d);					
	}
	
	public double getLegshotDamageMultiplier(World w, Gamemode g)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+g.toString().toLowerCase()+".weapon.legshotMultiplier",0.5d);					
	}
	
	public double getCritProbability(World w, Gamemode g)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+g.toString().toLowerCase()+".weapon.critProbability",0.02d);					
	}

	public double getCritMultiplier(World w, Gamemode g)
	{
		return config.getDouble("Worlds."+w.getName()+".gamemodes."+g.toString().toLowerCase()+".weapon.critMultiplier",2.0d);					
	}
	
	public boolean canEvironmentBeDamaged(Gamemode gmode, World w)
	{
		return config.getBoolean("Worlds."+w.getName()+".gamemodes."+gmode.toString().toLowerCase()+".environment.canBeDamaged",true) && config.getBoolean("Worlds."+w.getName()+".environment.canBeDamaged",true);					
	}
	
	public boolean canExlosionsDamageEnvironment(Gamemode gmode, World w)
	{
		return config.getBoolean("Worlds."+w.getName()+".gamemodes."+gmode.toString().toLowerCase()+".environment.doExplosionsDamageEnvironment",true) && config.getBoolean("Worlds."+w.getName()+".environment.doExplosionsDamageEnvironment",true);					
	}

	public boolean canEvironmentBeDamaged(World w) 
	{
		return config.getBoolean("Worlds."+w.getName()+".environment.canBeDamaged",true);
	}
}
