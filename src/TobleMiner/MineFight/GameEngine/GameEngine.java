package TobleMiner.MineFight.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Config;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.GameEngine.Match.Statistics.StatHandler;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.GameEngine.Player.CombatClass.CombatClass;
import TobleMiner.MineFight.Language.Langfile;
import TobleMiner.MineFight.Protection.ProtectedArea;
import TobleMiner.MineFight.Weapon.WeaponType;

public class GameEngine
{
	public final HashMap<String,CombatClass> combatClasses = new HashMap<String,CombatClass>();
	public final Config configuration;
	public final FileConfiguration config;
	private final List<Match> matches = new ArrayList<Match>();
	public final Langfile dict;
	public final StatHandler stathandler;
	
	public static double tps = 20.0d;
	
	public GameEngine(Main mane)
	{
		this.config = mane.getConfig();
		this.configuration = new Config(mane, config);
		this.dict = new Langfile(mane.getPluginDir());
		this.stathandler = new StatHandler(mane.getDatabase());
		init();
	}
	
	public void reload()
	{
		init();
	}
	
	private void init()
	{
		configuration.read();
		dict.loadLanguageFile(configuration.getLangFile());
		stathandler.reload(this);
		
		CombatClass sniper = new CombatClass("sniper",WeaponType.SNIPER);
		String sniperKit = configuration.config.getString("CombatClass.Sniper.Kit");
		sniper.kit = this.getKitFromString(sniperKit);
		String sniperArmor = configuration.config.getString("CombatClass.Sniper.Armor");
		sniper.armor = this.getArmorFromString(sniperArmor);
		this.combatClasses.put("sniper",sniper);
		CombatClass heavy = new CombatClass("heavy",WeaponType.MACHINEGUN);
		String heavyKit = configuration.config.getString("CombatClass.Heavy.Kit");
		heavy.kit = this.getKitFromString(heavyKit);
		String heavyArmor = configuration.config.getString("CombatClass.Heavy.Armor");
		heavy.armor = this.getArmorFromString(heavyArmor);
		this.combatClasses.put("heavy",heavy);
		CombatClass engineer = new CombatClass("engineer",WeaponType.MACHINEGUN);
		String engineerKit = configuration.config.getString("CombatClass.Engineer.Kit");
		engineer.kit = this.getKitFromString(engineerKit);
		String engineerArmor = configuration.config.getString("CombatClass.Engineer.Armor");
		engineer.armor = this.getArmorFromString(engineerArmor);
		this.combatClasses.put("engineer",engineer);
		CombatClass medic = new CombatClass("medic",WeaponType.MEDIGUN);
		String medicKit = configuration.config.getString("CombatClass.Medic.Kit");
		medic.kit = this.getKitFromString(medicKit);
		String medicArmor = configuration.config.getString("CombatClass.Medic.Armor");
		medic.armor = this.getArmorFromString(medicArmor);
		this.combatClasses.put("medic",medic);
		CombatClass pyro = new CombatClass("pyro",WeaponType.FLAMETHROWER);
		String pyroKit = configuration.config.getString("CombatClass.Pyro.Kit");
		pyro.kit = this.getKitFromString(pyroKit);
		String pyroArmor = configuration.config.getString("CombatClass.Pyro.Armor");
		pyro.armor = this.getArmorFromString(pyroArmor);
		this.combatClasses.put("pyro",pyro);
	}
	
	public List<ItemStack> getKitFromString(String s)
	{
		List<ItemStack> kitItems = new ArrayList<ItemStack>();
		for(String kitItem : s.split(";"))
		{
			try
			{
				String[] kitItemParts = kitItem.split(",");
				int amount = Integer.parseInt(kitItemParts[1]);
				String[] itemWithSubId = kitItemParts[0].split(":");
				String matname = itemWithSubId[0];
				short subId = Short.parseShort(itemWithSubId[1]);
				ItemStack is = new ItemStack(Material.getMaterial(matname),amount,subId);
				kitItems.add(is);
			}
			catch(Exception ex)
			{
				Error error = new Error("Error parsing combat-class information!","Check your mineFight.conf! Problem: "+ex.getMessage(),"There will be problems with the player-equipment untilthis is fixed.",this.getClass().getCanonicalName(),ErrorSeverity.ERROR);
				ErrorReporter.reportError(error);
			}	
		}
		return kitItems;
	}
	
	public ItemStack[] getArmorFromString(String s)
	{
		ItemStack[] armor = new ItemStack[4];
		int i=0;
		String[] ids = s.split(",");
		for(String id : ids)
		{
			if(i >= 4)
			{
				break;
			}
			armor[i] = new ItemStack(Material.getMaterial(id),1);
			i++;
		}
		return armor;
	}
	
	public void doUpdate()
	{
		for(int i=0;i<matches.size();i++)
		{
			Match m = matches.get(i);
			m.doUpdate();
		}
	}
	
	public void startNewMatch(World w,Gamemode g,String name,boolean hardcore)
	{
		List<Sign> signs = configuration.getInfoSigns(w, g);
		List<FlagContainer> flags = configuration.getFlags(w);
		List<RadioStationContainer> radioStations = configuration.getRadioStations(w);
		Match match = new Match(w,g,name,hardcore,signs,flags,radioStations,this.stathandler);
		matches.add(match);
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
	
	public boolean playerDroppedItem(Item is,Player p)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			return m.playerDroppedItem(is,p);
		}
		return false;
	}

	public boolean playerPickUpItem(Item is,Player p)
	{
		Match m = this.getMatch(p.getWorld());
		if(m != null)
		{
			return m.playerPickUpItem(is,p);
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

	public boolean itemDamage(Item is, DamageCause cause)
	{
		Match m = this.getMatch(is.getWorld());
		if(m != null)
		{
			return m.itemDamage(is, cause);
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

	public boolean playerDamage(Player entity, DamageCause damageCause)
	{
		World w = entity.getWorld();
		Match m = this.getMatch(w);
		if(m != null)
		{
			return m.playerDamage(entity, damageCause);
		}
		return false;
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
}
