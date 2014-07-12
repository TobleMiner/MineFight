package TobleMiner.MineFight.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.API.MineFightAPI;
import TobleMiner.MineFight.Configuration.Config;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.Configuration.Weapon.WeaponConfig;
import TobleMiner.MineFight.Configuration.Weapon.WeaponIndex;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.GameEngine.Match.Statistics.StatHandler;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.GameEngine.Player.CombatClass.CombatClass;
import TobleMiner.MineFight.Language.Langfile;
import TobleMiner.MineFight.Util.Protection.ProtectionUtil;

public class GameEngine
{
	public HashMap<String,CombatClass> combatClasses = new HashMap<String,CombatClass>();
	public final Config configuration;
	public final FileConfiguration config;
	private final List<Match> matches = new ArrayList<Match>();
	public final Langfile dict;
	public final StatHandler stathandler;
	public final WeaponRegistry weaponRegistry;
	private final ProtectionUtil protection;
	private WeaponIndex weapons;
	
	public static double tps = 20.0d;
	
	public GameEngine(Main mane)
	{
		this.config = mane.getConfig();
		this.configuration = new Config(mane, config);
		this.configuration.read();
		Main.gameEngine = this; //Getting the configinstance into the static reference from Main
		this.dict = new Langfile(mane.getPluginDir());
		this.stathandler = new StatHandler(mane.getDatabase());
		this.dict.loadLanguageFile(configuration.getLangFile());
		this.protection = new ProtectionUtil();
		this.weaponRegistry = new WeaponRegistry();
		new MineFightAPI();
	}
	
	public void reload()
	{
		init();
	}
	
	private void init()
	{
		this.stathandler.reload(this);
		this.weapons = WeaponConfig.getConfigs(Main.main.getDataFolder());
		Main.logger.log(Level.INFO, String.format(this.dict.get("weaponconfloaded"), weapons.count()));
		this.combatClasses = this.configuration.getCombatClasses(this.weapons);
		Main.logger.log(Level.INFO, String.format(this.dict.get("kitconfloaded"), this.combatClasses.size()));
	}
	
	public void doUpdate()
	{
		this.weaponRegistry.onTick();
		for(int i=0;i<matches.size();i++)
		{
			Match m = matches.get(i);
			m.doUpdate();
		}
	}
	
	public void startNewMatch(World w, Gamemode g, String name, boolean hardcore)
	{
		List<Sign> signs = configuration.getInfoSigns(w, g);
		List<FlagContainer> flags = configuration.getFlags(w);
		List<RadioStationContainer> radioStations = configuration.getRadioStations(w);
		Match match = new Match(w, g, name, hardcore, this.weapons, signs, flags, radioStations, this.stathandler);
		this.matches.add(match);
		this.weaponRegistry.matchCreated(match);
	}
	
	public PVPPlayer getPlayerByName(String name)
	{
		for(int i=0;i<matches.size();i++)
		{
			Match match = matches.get(i);
			PVPPlayer p = match.getPlayerByName(name);
			if(p != null)
			{
				return p;
			}
		}
		return null;
	}

	
	public PVPPlayer getPlayerExact(Player player)
	{
		for(int i=0;i<matches.size();i++)
		{
			Match match = matches.get(i);
			PVPPlayer p = match.getPlayerExact(player);
			if(p != null)
			{
				return p;
			}
		}
		return null;
	}

	public Match getMatch(World w)
	{
		for(int i=0;i<matches.size();i++)
		{
			Match m = matches.get(i);
			if(m.getWorld().equals(w))
			{
				return m;
			}
		}
		return null;
	}

	public Match getMatchByNameIgCase(String name)
	{
		for(int i=0;i<matches.size();i++)
		{
			Match m = matches.get(i);
			if(m.name.equalsIgnoreCase(name))
			{
				return m;
			}
		}
		return null;
	}
	
	public List<String> getMatchNames()
	{
		List<String> matchNames = new ArrayList<String>();
		for(int i=0;i<matches.size();i++)
		{
			matchNames.add(matches.get(i).name);
		}
		return matchNames;
	}
	
	public void removeMatch(Match m)
	{
		matches.remove(m);
	}
	
	public void playerDroppedItem(PlayerDropItemEvent pdie)
	{
		Match m = this.getMatch(pdie.getPlayer().getWorld());
		if(m != null)
		{
			m.playerDroppedItem(pdie);
		}
	}

	public void playerPickUpItem(PlayerPickupItemEvent ppie)
	{
		Match m = this.getMatch(ppie.getPlayer().getWorld());
		if(m != null)
		{
			m.playerPickUpItem(ppie);
		}
	}

	public void itemDespawn(ItemDespawnEvent event)
	{
		Match m = this.getMatch(event.getEntity().getWorld());
		if(m != null)
		{
			m.itemDespawn(event);
		}
	}

	public void playerDeath(PlayerDeathEvent event)
	{
		World w = event.getEntity().getWorld();
		Match m = this.getMatch(w);
		if(m != null)
		{
			m.playerDeath(event);
		}
	}

	public void rightClickSign(Player p, Block clickedBlock)
	{
		World w = p.getWorld();
		Match m = this.getMatch(w);
		if(m != null)
		{
			m.rightClickSign(p,clickedBlock);
		}
	}

	public void playerChangedWorld(Player player, World from)
	{
		Match m = this.getMatch(from);
		if(m != null)
		{
			m.playerChangedWorld(player);
		}
	}

	public void playerQuit(Player p)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			m.playerQuit(p);
		}
	}

	public boolean blockPlace(BlockPlaceEvent event)
	{
		Match m = this.getMatch(event.getBlock().getWorld());
		int veto = 1;
		if(m != null)
		{
			veto = m.blockPlace(event);
		}
		return (protection.isBlockProtected(event.getBlock()) && veto != 0) || veto == 2 || !Main.gameEngine.configuration.canEnvironmentBeDamaged(event.getBlock().getWorld());
	}

	public void endAllMatches() 
	{
		for(int i=0;i<matches.size();i++)
		{
			matches.get(i).endMatch();
		}
	}

	public boolean playerLeave(Player p)
	{
		PVPPlayer player = getPlayerExact(p);
		if(player != null)
		{
			player.leaveMatch(player.getMatch().getMatchLeaveLoc());
			return true;
		}
		return false;
	}

	public String playerChangeTeam(Player p)
	{
		PVPPlayer player = getPlayerExact(p);
		if(player != null)
		{
			return player.getMatch().playerChangeTeam(player);
		}
		return ChatColor.DARK_RED+Main.gameEngine.dict.get("notJoinedYet");
	}

	public String playerJoinMatch(Player p, String s)
	{
		Match m = getMatchByNameIgCase(s);
		if(m != null)
		{
			return (m.join(p));
		}
		else
		{
			return ChatColor.DARK_RED+String.format(Main.gameEngine.dict.get("noMatch"),s);
		}
	}

	public Location playerRespawn(Player p, Location respawnLocation)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			return m.playerRespawn(p,respawnLocation);
		}		
		return respawnLocation;
	}

	public void rightClickWithBone(Player p)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			m.rightClickWithBone(p);
		}		
	}

	public boolean blockBreak(BlockBreakEvent event)
	{
		Match m = this.getMatch(event.getBlock().getWorld());
		int veto = 1;
		if(m != null)
		{
			veto = m.blockBreak(event);
		}
		return (protection.isBlockProtected(event.getBlock()) && veto != 0) || veto == 2 || !Main.gameEngine.configuration.canEnvironmentBeDamaged(event.getBlock().getWorld());
	}

	public boolean playerDamagePlayer(Player damager, Player damaged, double d)
	{
		Match m = this.getMatch(damager.getWorld());
		if(m != null)
		{
			return m.playerDamagePlayer(damager,damaged,d);
		}
		return false;
	}

	public void playerChat(AsyncPlayerChatEvent event)
	{
		Match m = this.getMatch(event.getPlayer().getWorld());
		if(m != null)
		{
			m.playerChat(event);
		}
	}

	public void foodLevelChange(FoodLevelChangeEvent event)
	{
		Match m = this.getMatch(event.getEntity().getWorld());
		if(m != null)
		{
			m.foodLevelChange(event);
		}
	}

	public void rightClickWithStick(Player p) 
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			m.rightClickWithStick(p);
		}
	}

	public void entityExplosion(EntityExplodeEvent event)
	{
		Match m = this.getMatch(event.getLocation().getWorld());
		if(m != null)
		{
			m.entityExplosion(event);
		}
	}

	public void playerInteract(PlayerInteractEvent event) 
	{
		Match m = this.getMatch(event.getPlayer().getWorld());
		if(m != null)
		{
			m.playerInteract(event);
		}
	}

	public void entityDamage(EntityDamageEvent ede) 
	{
		Match m = this.getMatch(ede.getEntity().getWorld());
		if(m != null)
		{
			m.entityDamage(ede);
		}
	}

	public void entityCombust(EntityCombustEvent event) 
	{
		Match m = this.getMatch(event.getEntity().getWorld());
		if(m != null)
		{
			m.entityCombust(event);
		}
	}

	public void projectileLaunched(ProjectileLaunchEvent event) 
	{
		Match m = this.getMatch(event.getEntity().getWorld());
		if(m != null)
		{
			m.projectileLaunched(event);
		}
	}

	public boolean blockDamaged(BlockDamageEvent event)
	{
		Match m = this.getMatch(event.getBlock().getWorld());
		int veto = 1;
		if(m != null)
		{
			veto = m.blockDamaged(event);
		}
		return (protection.isBlockProtected(event.getBlock()) && veto != 0) || veto == 2 || !Main.gameEngine.configuration.canEnvironmentBeDamaged(event.getBlock().getWorld());
	}

	public boolean entityChangeBlock(EntityChangeBlockEvent event)
	{
		Match m = this.getMatch(event.getBlock().getWorld());
		int veto = 1;
		if(m != null)
		{
			veto = m.blockChanged(event);
		}
		return (protection.isBlockProtected(event.getBlock()) && veto != 0) || veto == 2 || !Main.gameEngine.configuration.canEnvironmentBeDamaged(event.getBlock().getWorld());
	}

	public void entityDamageByEntity(EntityDamageByEntityEvent event)
	{
		Match m = this.getMatch(event.getEntity().getWorld());
		if(m != null)
		{
			m.entityDamageByEntity(event);
		}
	}

	public void projectileHit(ProjectileHitEvent event)
	{
		Match m = this.getMatch(event.getEntity().getWorld());
		if(m != null)
		{
			m.projectileHit(event);
		}		
	}
}