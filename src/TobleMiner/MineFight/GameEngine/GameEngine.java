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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import TobleMiner.MineFight.Main;
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
import TobleMiner.MineFight.Protection.ProtectedArea;

public class GameEngine
{
	public HashMap<String,CombatClass> combatClasses = new HashMap<String,CombatClass>();
	public final Config configuration;
	public final FileConfiguration config;
	private final List<Match> matches = new ArrayList<Match>();
	public final Langfile dict;
	public final StatHandler stathandler;
	private WeaponIndex weapons;
	
	public static double tps = 20.0d;
	
	public GameEngine(Main mane)
	{
		this.config = mane.getConfig();
		this.configuration = new Config(mane, config);
		this.dict = new Langfile(mane.getPluginDir());
		this.stathandler = new StatHandler(mane.getDatabase());
		this.configuration.read();
		this.dict.loadLanguageFile(configuration.getLangFile());
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
	
	public boolean playerDroppedItem(PlayerDropItemEvent pdie)
	{
		Match m = this.getMatch(pdie.getPlayer().getWorld());
		if(m != null)
		{
			return m.playerDroppedItem(pdie);
		}
		return false;
	}

	public boolean playerPickUpItem(PlayerPickupItemEvent ppie)
	{
		Match m = this.getMatch(ppie.getPlayer().getWorld());
		if(m != null)
		{
			return m.playerPickUpItem(ppie);
		}
		return false;
	}

	public boolean itemDespawn(Item is)
	{
		Match m = this.getMatch(is.getWorld());
		if(m != null)
		{
			return m.itemDespawn(is);
		}
		return false;
	}

	public String playerDeath(Player entity, String deathMessage, List<ItemStack> drops)
	{
		World w = entity.getWorld();
		Match m = this.getMatch(w);
		if(m != null)
		{
			return m.playerDeath(entity,deathMessage,drops);
		}
		return deathMessage;
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

	public void rightClickBlockWithLapis(Player p, Block clickedBlock, PlayerInventory playerInventory)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			m.rightClickBlockWithLapis(p,clickedBlock,playerInventory);
		}
	}

	public void rightClickWithDiamond(Player p) 
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			m.rightClickWithDiamond(p);
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

	public void ClickWithWoodenSword(Player p, boolean rightclick)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			m.ClickWithWoodenSword(p,rightclick);
		}		
	}

	public boolean blockPlace(Player p, Block b)
	{
		Match m = this.getMatch(p.getWorld());
		int veto = 1;
		List<ProtectedArea> lpa = Main.gameEngine.configuration.getProtectedAreasByWorld(p.getWorld());
		boolean isBlockProtected = false;
		if(lpa != null)
		{
			for(ProtectedArea pa : lpa)
			{
				isBlockProtected = pa.isBlockInsideRegion(b);
				if(isBlockProtected)
				{
					break;
				}
			}
		}
		if(m != null)
		{
			veto = m.blockPlace(p,b);
		}
		return (isBlockProtected && veto != 0) || veto == 2 || !Main.gameEngine.configuration.canEvironmentBeDamaged(p.getWorld());
	}

	public boolean arrowLaunchedByPlayer(Player p, Arrow arrow)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			return m.arrowLaunchedByPlayer(p,arrow);
		}
		return false;
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

	public boolean arrowHitPlayer(Arrow a, Player p, double d)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			return m.arrowHitPlayer(p,a,d);
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

	public void arrowHit(Arrow arr)
	{
		Match m = this.getMatch(arr.getWorld());
		if(m != null)
		{
			m.arrowHit(arr);
		}		
	}

	public boolean blockBreak(Player p, Block b)
	{
		if(p != null)
		{
			Match m = this.getMatch(p.getWorld());
			int veto = 1;
			List<ProtectedArea> lpa = Main.gameEngine.configuration.getProtectedAreasByWorld(p.getWorld());
			boolean isBlockProtected = false;
			if(lpa != null)
			{
				for(ProtectedArea pa : lpa)
				{
					isBlockProtected = pa.isBlockInsideRegion(b);
					if(isBlockProtected)
					{
						break;
					}
				}
			}
			if(m != null)
			{
				veto = m.blockBreak(p,b);
			}
			return (isBlockProtected && veto != 0) || veto == 2 || !Main.gameEngine.configuration.canEvironmentBeDamaged(p.getWorld());
		}
		return false;
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

	public String playerChat(String format, Player p)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			return m.playerChat(format,p);
		}
		return format;
	}

	public boolean foodLevelChange(Player p)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			return m.foodLevelChange(p);
		}
		return false;
	}

	public void rightClickWithStick(Player p) 
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			m.rightClickWithStick(p);
		}
	}

	public boolean entityExplosion(EntityExplodeEvent event)
	{
		Match m = this.getMatch(event.getLocation().getWorld());
		if(m != null)
		{
			return m.entityExplosion(event);
		}
		return false;
	}

	public void playerInteract(PlayerInteractEvent event) 
	{
		Match m = this.getMatch(event.getPlayer().getWorld());
		if(m != null)
		{
			m.playerInteract(event);
		}
	}

	public boolean entityDamage(EntityDamageEvent ede) 
	{
		Match m = this.getMatch(ede.getEntity().getWorld());
		if(m != null)
		{
			return m.entityDamage(ede);
		}
		return false;
	}

	public boolean entityCombust(EntityCombustEvent event) 
	{
		Match m = this.getMatch(event.getEntity().getWorld());
		if(m != null)
		{
			return m.entityCombust(event);
		}
		return false;
	}
}
