package TobleMiner.MineFight.Configuration;

import java.io.File;
import java.util.ArrayList;
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
import org.bukkit.configuration.file.YamlConfiguration;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.Configuration.Container.Killstreak;
import TobleMiner.MineFight.Configuration.Container.KillstreakConfig;
import TobleMiner.MineFight.Configuration.Container.PlayerSeekerContainer;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Score;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.Protection.Area3D;

public class WorldConfig
{
	public final List<Area3D> protectedRegions = new ArrayList<Area3D>();

	private final FileConfiguration config;
	private final FileConfiguration regions;
	private final File conffl;
	private final File regionfl;
	private final World world;
	
	public WorldConfig(Main mane, World w)
	{
		this.config = new YamlConfiguration();
		this.regions = new YamlConfiguration();
		File folder = new File(mane.getPluginDir(),"worlds");
		if(!folder.exists())
		{
			folder.mkdir();
		}
		folder = new File(folder,w.getName());
		if(!folder.exists())
		{
			folder.mkdir();
		}
		this.world = w;
		this.conffl = new File(folder,"world.conf");
		this.regionfl = new File(folder,"regions.conf");
		this.load();
	}
	
	public void save()
	{
		try
		{
			this.config.save(this.conffl);
			this.regions.save(this.regionfl);
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed saving configuration!","The worldconfiguration could not be loaded: "+ex.getMessage(),"The plugin won't work until this error is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.SEVERE);
			ErrorReporter.reportError(error);
		}
	}
	
	public void load()
	{
		if(this.conffl.exists())
		{
			try
			{
				this.config.load(this.conffl);
			}
			catch(Exception ex)
			{
				Error error = new Error("Failed loading world configuration!",String.format("The configuration for the world '%s' could not be loaded: ", this.world.getName()) + ex.getMessage(),"The plugin won't work for this particular world until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.SEVERE);
				ErrorReporter.reportError(error);
			}
		}
		if(this.regionfl.exists())
		{
			try
			{
				this.regions.load(this.regionfl);
			}
			catch(Exception ex)
			{
				Error error = new Error("Failed loading world regions!",String.format("The regions for the world '%s' could not be loaded: ", this.world.getName()) + ex.getMessage(),"The protection won't work for this particular world until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.SEVERE);
				ErrorReporter.reportError(error);
			}
		}
		boolean makeConfig = config.getBoolean("config.reset",true);
		Location spawn = this.world.getSpawnLocation();
		if(makeConfig)
		{
			config.set("mpvp",false);
			config.set("classSelection.pos1.X", spawn.getBlockX());
			config.set("classSelection.pos1.y", spawn.getBlockY());
			config.set("classSelection.pos1.z", spawn.getBlockZ());
			config.set("classSelection.pos2.X", spawn.getBlockX());
			config.set("classSelection.pos2.y", spawn.getBlockY());
			config.set("classSelection.pos2.z", spawn.getBlockZ());
			config.set("battleSpawn.pos1.X",spawn.getBlockX());
			config.set("battleSpawn.pos1.Y",spawn.getBlockY());
			config.set("battleSpawn.pos1.Z",spawn.getBlockZ());
			config.set("battleSpawn.pos2.X",spawn.getBlockX());
			config.set("battleSpawn.pos2.Y",spawn.getBlockY());
			config.set("battleSpawn.pos2.Z",spawn.getBlockZ());
			config.set("battleSpawn.red.pos1.X",spawn.getBlockX());
			config.set("battleSpawn.red.pos1.Y",spawn.getBlockY());
			config.set("battleSpawn.red.pos1.Z",spawn.getBlockZ());
			config.set("battleSpawn.red.pos2.X",spawn.getBlockX());
		 	config.set("battleSpawn.red.pos2.Y",spawn.getBlockY());
			config.set("battleSpawn.red.pos2.Z",spawn.getBlockZ());
			config.set("battleSpawn.blue.pos1.X",spawn.getBlockX());
			config.set("battleSpawn.blue.pos1.Y",spawn.getBlockY());
			config.set("battleSpawn.blue.pos1.Z",spawn.getBlockZ());
			config.set("battleSpawn.blue.pos2.X",spawn.getBlockX());
			config.set("battleSpawn.blue.pos2.Y",spawn.getBlockY());
			config.set("battleSpawn.blue.pos2.Z",spawn.getBlockZ());
			for(Gamemode gmode : Gamemode.values())
			{
				String gmpref = "gamemodes."+gmode.toString().toLowerCase();
				config.set(gmpref+".tickets",500);
				config.set(gmpref+".enabled",true);			
				config.set(gmpref+".autobalance",true);
				config.set(gmpref+".player.preventItemDropOnDeath",true);					
				config.set(gmpref+".player.preventItemDrop",true);					
				config.set(gmpref+".player.enableFallDamage",true);					
				config.set(gmpref+".player.enableHunger",false);					
				config.createSection(gmpref+".infoSigns");
				if(gmode.equals(Gamemode.Conquest))
				{
					config.createSection(gmpref+".flags");
					config.set(gmpref+".flagCaptureDistance",10);
					config.set(gmpref+".flagCaptureSpeed",10);
					config.set(gmpref+".flagCaptureAccelerationPerPerson",1.2d);
					config.set(gmpref+".pointlossPerFlagPerSecond",1.0d);
					config.set(gmpref+".losePointsWhenEnemyHasLessThanHalfFlags",false);
				}
				else if(gmode.equals(Gamemode.Rush))
				{						
					config.createSection(gmpref+".radioStations");
					config.set(gmpref+".destructTime",10d);
					config.set(gmpref+".defenderOuterSpawnRadius",20d);
					config.set(gmpref+".defenderInnerSpawnRadius",5d);
					config.set(gmpref+".attackerOuterSpawnRadius",70d);
					config.set(gmpref+".attackerInnerSpawnRadius",30d);
				}
				config.set(gmpref+".projectile.damage",5d);					
				config.set(gmpref+".weapon.headshotMultiplier",2d);					
				config.set(gmpref+".weapon.legshotMultiplier",0.5d);					
				config.set(gmpref+".weapon.critProbability",0.02d);					
				config.set(gmpref+".weapon.critMultiplier",2d);					
				config.set(gmpref+".weapon.claymore.canAvoid",true);					
				config.set(gmpref+".weapon.claymore.canPickup",true);					
				config.set(gmpref+".weapon.claymore.maxNum",5);					
				ConfigurationSection cs = config.createSection(gmpref+".weapon.killstreak.killstreaks");
				cs.set("3", "ims");
				cs.set("5", "playerseeker");
				config.set(gmpref+".weapon.killstreak.playerseeker.detectDist",20d);					
				config.set(gmpref+".weapon.killstreak.playerseeker.exploStr",10d);					
				config.set(gmpref+".weapon.killstreak.playerseeker.maxSpeed",10d);					
				config.set(gmpref+".weapon.killstreak.playerseeker.peakHeight",10d);					
				config.set(gmpref+".weapon.killstreak.playerseeker.maxAccel",5d);					
				config.set(gmpref+".weapon.killstreak.playerseeker.threshold",5d);
				config.set(gmpref+".environment.canBeDamaged",true);					
				config.set(gmpref+".environment.doExplosionsDamageEnvironment",true);
				config.set(gmpref+"infoBeaconInterval", 30);
			}
			config.set("gameProps.score.flagCapture",100d);
			config.set("gameProps.score.kill", 100d);
			config.set("gameProps.score.radioArm", 200d);
			config.set("gameProps.score.radioDest", 400d);
			config.set("gameProps.score.radioDisarm", 250d);
			config.set("gameProps.score.resupply", 20d);
			config.set("environment.canBeDamaged",true);
			config.set("environment.doExplosionsDamageEnvironment",true);
			config.set("leaveWorld",this.world.getName());
			config.set("minimapEnabled",true);
			config.set("config.reset",false);
		}
		boolean makeRegions = this.regions.getBoolean("regions.reset",true);
		if(makeRegions)
		{
			ConfigurationSection cs = this.regions.createSection("protections");
			String s = Integer.toString(spawn.getBlockX())+","+Integer.toString(spawn.getBlockY())+","+Integer.toString(spawn.getBlockZ());
			cs.set(s+".pos1.X",spawn.getBlockX());
			cs.set(s+".pos1.Y",spawn.getBlockY());
			cs.set(s+".pos1.Z",spawn.getBlockZ());
			cs.set(s+".pos2.X",spawn.getBlockX());
			cs.set(s+".pos2.Y",spawn.getBlockY());
			cs.set(s+".pos2.Z",spawn.getBlockZ());
			cs.set(s+".enabled",true);
			this.regions.set("regions.reset",false);
		}
		if(makeConfig || makeRegions)
		{
			this.save();
		}
		ConfigurationSection cs = this.regions.getConfigurationSection("protections");
		Set<String> keys = cs.getValues(false).keySet();
		protectedRegions.clear();
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
				protectedRegions.add(new Area3D(new Location(world, x1, y1, z1), new Location(world, x2, y2, z2)));
			}
		}
	}

	public boolean isProtectionEnabled(ConfigurationSection cs,String key)
	{
		return 	cs.getBoolean(key+".enabled",true);
	}

	public void addProtectedRegion(Location pos1, Location pos2)
	{
		ConfigurationSection cs = this.regions.getConfigurationSection("protections");
		String prefix = Integer.toString(pos1.getBlockX())+","+Integer.toString(pos1.getBlockY())+","+Integer.toString(pos1.getBlockZ());
		cs.set(prefix+".pos1.X", pos1.getX());
		cs.set(prefix+".pos1.Y", pos1.getY());
		cs.set(prefix+".pos1.Z", pos1.getZ());
		cs.set(prefix+".pos2.X", pos2.getX());
		cs.set(prefix+".pos2.Y", pos2.getY());
		cs.set(prefix+".pos2.Z", pos2.getZ());
		cs.set(prefix+".enabled",true);
		this.save();
		this.protectedRegions.add(new Area3D(pos1, pos2));
	}
	
	public Location getRoundEndSpawn()
	{
		String worldStr = config.getString("leaveWorld");
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

	public Area3D getRespawnArea() 
	{
		Double x1 = config.getDouble("classSelection.pos1.X");
		Double y1 = config.getDouble("classSelection.pos1.Y");
		Double z1 = config.getDouble("classSelection.pos1.Z");
		Double x2 = config.getDouble("classSelection.pos2.X");
		Double y2 = config.getDouble("classSelection.pos2.Y");
		Double z2 = config.getDouble("classSelection.pos2.Z");
		return new Area3D(new Location(this.world, x1, y1, z1), new Location(this.world, x2, y2, z2));
	}

	public Area3D getSpawnArea() 
	{
		Double x1 = config.getDouble("battleSpawn.pos1.X");
		Double y1 = config.getDouble("battleSpawn.pos1.Y");
		Double z1 = config.getDouble("battleSpawn.pos1.Z");
		Double x2 = config.getDouble("battleSpawn.pos2.X");
		Double y2 = config.getDouble("battleSpawn.pos2.Y");
		Double z2 = config.getDouble("battleSpawn.pos2.Z");
		return new Area3D(new Location(this.world, x1, y1, z1), new Location(this.world, x2, y2, z2));
	}

	public Area3D getSpawnAreaRed() 
	{
		Double x1 = config.getDouble("battleSpawn.red.pos1.X");
		Double y1 = config.getDouble("battleSpawn.red.pos1.Y");
		Double z1 = config.getDouble("battleSpawn.red.pos1.Z");
		Double x2 = config.getDouble("battleSpawn.red.pos2.X");
		Double y2 = config.getDouble("battleSpawn.red.pos2.Y");
		Double z2 = config.getDouble("battleSpawn.red.pos2.Z");
		return new Area3D(new Location(this.world, x1, y1, z1), new Location(this.world, x2, y2, z2));
	}
	

	public Area3D getSpawnAreaBlue() 
	{
		Double x1 = config.getDouble("battleSpawn.blue.pos1.X");
		Double y1 = config.getDouble("battleSpawn.blue.pos1.Y");
		Double z1 = config.getDouble("battleSpawn.blue.pos1.Z");
		Double x2 = config.getDouble("battleSpawn.blue.pos2.X");
		Double y2 = config.getDouble("battleSpawn.blue.pos2.Y");
		Double z2 = config.getDouble("battleSpawn.blue.pos2.Z");
		return new Area3D(new Location(this.world, x1, y1, z1), new Location(this.world, x2, y2, z2));
	}

	public boolean isGamemodeEnabled(Gamemode gmode) 
	{
		return config.getBoolean("gamemodes."+gmode.toString().toLowerCase()+".enabled",false);
	}

	public int getTickets(Gamemode gmode)
	{
		return config.getInt("gamemodes."+gmode.toString().toLowerCase()+".tickets");
	}

	public void addInfoSign(Gamemode gm, Sign sign) 
	{
		ConfigurationSection cs = config.getConfigurationSection("gamemodes."+gm.toString().toLowerCase()+".infoSigns");
		if(cs != null)
		{
			String signName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(signName+".X",sign.getLocation().getX());
			cs.set(signName+".Y",sign.getLocation().getY());
			cs.set(signName+".Z",sign.getLocation().getZ());
			this.save();
		}
	}

	public void removeInfoSign(Gamemode gm, Sign sign) 
	{
		ConfigurationSection cs = config.getConfigurationSection("gamemodes."+gm.toString().toLowerCase()+".infoSigns");
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

	public List<Sign> getInfoSigns(Gamemode gm) 
	{
		List<Sign> signs = new ArrayList<Sign>();
		try
		{
			ConfigurationSection cs = config.getConfigurationSection("gamemodes."+gm.toString().toLowerCase()+".infoSigns");
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

	public boolean getPreventItemDropOnDeath(Gamemode gm) 
	{
		return config.getBoolean("gamemodes."+gm.toString().toLowerCase()+".player.preventItemDropOnDeath",true);
	}

	public boolean getPreventItemDrop(Gamemode gm) 
	{
		return config.getBoolean("gamemodes."+gm.toString().toLowerCase()+".player.preventItemDrop",true);
	}

	public void addFlag(FlagContainer fc) 
	{
		ConfigurationSection cs = config.getConfigurationSection("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flags");
		if(cs != null)
		{
			String flagName = Integer.toString(fc.sign.getLocation().getBlockX())+","+Integer.toString(fc.sign.getLocation().getBlockY())+","+Integer.toString(fc.sign.getLocation().getBlockZ());
			cs.set(flagName+".X",fc.sign.getLocation().getX());
			cs.set(flagName+".Y",fc.sign.getLocation().getY());
			cs.set(flagName+".Z",fc.sign.getLocation().getZ());
			cs.set(flagName+".name",fc.name);
			cs.set(flagName+".sky", fc.sky);
			this.save();
		}
	}
	
	public void removeFlag(Sign sign)
	{
		ConfigurationSection cs = config.getConfigurationSection("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flags");
		if(cs != null)
		{
			String flagName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(flagName+".X",null);
			cs.set(flagName+".Y",null);
			cs.set(flagName+".Z",null);
			cs.set(flagName+".name",null);
			cs.set(flagName+".sky",null);
			cs.set(flagName,null);
			this.save();
		}		
	}

	public List<FlagContainer> getFlags()
	{
		List<FlagContainer> signs = new ArrayList<FlagContainer>();
		try
		{
			ConfigurationSection cs = config.getConfigurationSection("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flags");
			if(cs != null)
			{
				for(String s : cs.getValues(false).keySet())
				{
					Location loc = new Location(world,cs.getDouble(s+".X"),cs.getDouble(s+".Y"),cs.getDouble(s+".Z"));
					String name = cs.getString(s+".name","");
					boolean sky = cs.getBoolean(s+".sky",true);
					Block b = world.getBlockAt(loc);
					if(b.getType().equals(Material.WALL_SIGN))
					{
						signs.add(new FlagContainer((Sign)b.getState(), name, sky));
					}
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return signs;
	}

	public double getFlagCaptureDistance()
	{
		return config.getDouble("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flagCaptureDistance",10d);
	}

	public double getFlagCaptureSpeed() 
	{
		return config.getDouble("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flagCaptureSpeed",10d);
	}

	public double getFlagCaptureAcceleration()
	{
		return config.getDouble("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".flagCaptureAccelerationPerPerson",1.2d);
	}

	public double getPointlossPerFlagPerSecond()
	{
		return config.getDouble("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".pointlossPerFlagPerSecond",1.0d);
	}

	public boolean getLosePointsWhenEnemyHasLessThanHalfFlags() 
	{
		return config.getBoolean("gamemodes."+Gamemode.Conquest.toString().toLowerCase()+".losePointsWhenEnemyHasLessThanHalfFlags",false);
	}

	public boolean getAutobalance(Gamemode gm) 
	{
		return config.getBoolean("gamemodes."+gm.toString().toLowerCase()+".autobalance",true);		
	}

	public List<RadioStationContainer> getRadioStations() 
	{
		List<RadioStationContainer> signs = new ArrayList<RadioStationContainer>();
		try
		{
			ConfigurationSection cs = config.getConfigurationSection("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".radioStations");
			if(cs != null)
			{
				for(String s : cs.getValues(false).keySet())
				{
					Location loc = new Location(world,cs.getDouble(s+".X"),cs.getDouble(s+".Y"),cs.getDouble(s+".Z"));
					String name = cs.getString(s+".name","");
					boolean sky = cs.getBoolean(s+".sky",true);
					Block b = world.getBlockAt(loc);
					if(b.getType().equals(Material.WALL_SIGN))
					{
						signs.add(new RadioStationContainer((Sign)b.getState(),name,sky));
					}
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return signs;
	}

	public void addRadioStation(RadioStationContainer rsc)
	{
		ConfigurationSection cs = config.getConfigurationSection("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".radioStations");
		if(cs != null)
		{
			Sign sign = rsc.sign;
			String rsName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(rsName+".X",sign.getLocation().getX());
			cs.set(rsName+".Y",sign.getLocation().getY());
			cs.set(rsName+".Z",sign.getLocation().getZ());
			cs.set(rsName+".name",rsc.name);
			cs.set(rsName+".sky",rsc.sky);
			this.save();
		}
	}

	public void removeRadioStation(Sign sign) 
	{
		ConfigurationSection cs = config.getConfigurationSection("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".radioStations");
		if(cs != null)
		{
			String rsName = Integer.toString(sign.getLocation().getBlockX())+","+Integer.toString(sign.getLocation().getBlockY())+","+Integer.toString(sign.getLocation().getBlockZ());
			cs.set(rsName+".X",null);
			cs.set(rsName+".Y",null);
			cs.set(rsName+".Z",null);
			cs.set(rsName+".name",null);
			cs.set(rsName+".sky",null);
			cs.set(rsName,null);
			this.save();
		}		
	}

	public double getRadioStationDestructTime()
	{
		return config.getDouble("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".destructTime",10d);
	}

	public double getDefenderInnerSpawnRadius()
	{
		return config.getDouble("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".defenderInnerSpawnRadius",5d);
	}

	public double getDefenderOuterSpawnRadius()
	{
		return config.getDouble("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".defenderOuterSpawnRadius",10d);		
	}

	public double getAttackerInnerSpawnRadius()
	{
		return config.getDouble("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".attackerInnerSpawnRadius",30d);	
	}

	public double getAttackerOuterSpawnRadius() 
	{
		return config.getDouble("gamemodes."+Gamemode.Rush.toString().toLowerCase()+".attackerOuterSpawnRadius",70d);		
	}

	public boolean isFalldamageActive(Gamemode gm) 
	{
		return config.getBoolean("gamemodes."+gm.toString().toLowerCase()+".player.enableFallDamage",true);
	}

	public boolean isHungerActive(Gamemode gm)
	{
		return config.getBoolean("gamemodes."+gm.toString().toLowerCase()+".player.enableHunger",false);
	}

	public boolean isMpvpEnabled() 
	{
		return config.getBoolean("mpvp",false);
	}

	public boolean isMinimapEnabled() 
	{
		return config.getBoolean("minimapEnabled",true);	
	}

	public double getProjectileDamage(Gamemode g) 
	{
		return config.getDouble("gamemodes."+g.toString().toLowerCase()+".projectile.damage",0.1d);					
	}

	public double getHeadshotDamageMultiplier(Gamemode g) 
	{
		return config.getDouble("gamemodes."+g.toString().toLowerCase()+".weapon.headshotMultiplier",2d);					
	}

	public double getLegshotDamageMultiplier(Gamemode g)
	{
		return config.getDouble("gamemodes."+g.toString().toLowerCase()+".weapon.legshotMultiplier",0.5d);					
	}

	public double getCritProbability(Gamemode g) 
	{
		return config.getDouble("gamemodes."+g.toString().toLowerCase()+".weapon.critProbability",0.02d);					
	}

	public double getCritMultiplier(Gamemode g) 
	{
		return config.getDouble("gamemodes."+g.toString().toLowerCase()+".weapon.critMultiplier",2.0d);					
	}

	public boolean canEnvironmentBeDamaged(Gamemode gmode) 
	{
		return config.getBoolean("gamemodes."+gmode.toString().toLowerCase()+".environment.canBeDamaged",true) && config.getBoolean("environment.canBeDamaged",true);					
	}

	public boolean canExplosionDamageEvironment(Gamemode gmode) 
	{
		return config.getBoolean("gamemodes."+gmode.toString().toLowerCase()+".environment.doExplosionsDamageEnvironment",true) && config.getBoolean("environment.doExplosionsDamageEnvironment",true);					
	}

	public boolean canEnvironmentBeDamaged() 
	{
		return config.getBoolean("environment.canBeDamaged",true);
	}
	
	public double getScoreForAction(Score s)
	{
		return config.getDouble("gameProps.score."+s.name,0d);
	}
	
	public int getInfoBeaconInterval(Gamemode gmode) 
	{
		return config.getInt("gamemodes."+gmode.toString().toLowerCase()+".infoBeaconInterval",30);
	}

	public PlayerSeekerContainer getPlayerSeekerConf(Gamemode gmode) 
	{
		String pref = "gamemodes."+gmode.toString().toLowerCase()+"weapon.killstreak.playerseeker.";
		double detectDist = config.getDouble(pref+"detectDist",20d);					
		float exploStr = (float)config.getDouble(pref+"exploStr",10d);					
		double maxSpeed = config.getDouble(pref+"maxSpeed",10d);					
		double peakHeight = config.getDouble(pref+"peakHeight",10d);					
		double maxAccel = config.getDouble(pref+"maxAccel",5d);					
		double threshold = config.getDouble(pref+"threshold",5d);					
		return new PlayerSeekerContainer(detectDist, exploStr, maxSpeed, peakHeight, maxAccel, threshold);
	}
	
	public KillstreakConfig getKillstreaks(Gamemode gmode)
	{
		KillstreakConfig kc = new KillstreakConfig();
		ConfigurationSection cs = this.config.getConfigurationSection("gamemodes."+gmode.toString().toLowerCase()+".weapon.killstreak.killstreaks");
		if(cs != null)
		{
			for(String key : cs.getValues(false).keySet())
			{
				try
				{
					int num = Integer.parseInt(key);
					String ksname = cs.getString(Integer.toString(num));
					Killstreak ks = Killstreak.getByName(ksname);
					if(ks == Killstreak.NONE)
					{
						Error err = new Error("Error parsing killstreak config.",String.format("The name \"%s\" is not a valid killstreak name."),"Take a look at the coresponding config file.",this.getClass().getName(),ErrorSeverity.ERROR);
						ErrorReporter.reportError(err);
					}
					kc.add(num, ks);
				}
				catch(Exception ex)
				{
					Error err = new Error("Error parsing killstreak config.",String.format("The key \"%s\" is not numeric."),"Take a look at the coresponding config file.",this.getClass().getName(),ErrorSeverity.ERROR);
					ErrorReporter.reportError(err);
				}
			}
		}
		else
		{
			Error err = new Error("No killstreak section found!", String.format("The killstreak config for world \"%s\", gamemode \"%s\" could not be found. Try regenerating the config file.",this.world.getName(),Main.gameEngine.dict.get(gmode.transname)), "There will be no killstreaks available for this particular gamemode in the world.", this.getClass().getName(), ErrorSeverity.ERROR);
			ErrorReporter.reportError(err);
		}
		return kc;
	}
}
