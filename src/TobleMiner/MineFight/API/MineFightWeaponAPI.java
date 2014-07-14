package TobleMiner.MineFight.API;

import java.io.File;
import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.Util;
import TobleMiner.MineFight.Util.Protection.ProtectionUtil;
import TobleMiner.MineFight.Weapon.Weapon;

public class MineFightWeaponAPI 
{
	public static MineFightWeaponAPI instance;
	
	public MineFightWeaponAPI()
	{
		MineFightWeaponAPI.instance = this;
	}
	
	public Logger getLogger(Plugin p)
	{
		return new Logger(p);
	}
	
	public void addTranslations(File langFile)
	{
		Main.gameEngine.dict.loadLanguageFileExt(langFile);
	}
	
	public boolean registerWeapon(Weapon weapon, World w)
	{
		Debugger.writeDebugOut(String.format("Registering '%s' World: '%s'", weapon.getClass().getSimpleName(), w.getName()));
		Match m = Main.gameEngine.getMatch(w);
		if(m != null)
		{
			weapon.matchCreated(m);
			for(PVPPlayer p : m.getPlayers())
			{
				weapon.onJoin(m, p);
			}
		}
		return Main.gameEngine.weaponRegistry.registerWeapon(weapon, w);
	}
	
	public boolean unregisterWeapon(Weapon weapon, World w)
	{
		Debugger.writeDebugOut(String.format("Unregistering '%s' World: '%s'", weapon.getClass().getSimpleName(), w.getName()));
		Match m = Main.gameEngine.getMatch(w);
		if(m != null)
		{
			for(PVPPlayer p : m.getPlayers())
			{
				weapon.onLeave(m, p);
			}
			weapon.matchEnded(m);
		}
		return Main.gameEngine.weaponRegistry.unregisterWeapon(weapon, w);
	}
	
	public List<World> getKnownWorlds()
	{
		return Main.gameEngine.configuration.getLoadtimeWorlds();
	}
	
	public ProtectionUtil getProtections()
	{
		return Util.protect;
	}
}
