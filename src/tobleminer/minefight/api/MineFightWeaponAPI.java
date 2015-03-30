package tobleminer.minefight.api;

import org.bukkit.World;

import tobleminer.minefight.Main;
import tobleminer.minefight.debug.Debugger;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.weapon.Weapon;

public class MineFightWeaponAPI 
{
	public static MineFightWeaponAPI instance;
	
	public MineFightWeaponAPI()
	{
		MineFightWeaponAPI.instance = this;
	}
			
	public boolean registerWeapon(Weapon weapon, World w)
	{
		Debugger.writeDebugOut(String.format("Registering '%s' World: '%s'", weapon.getClass().getSimpleName(), w.getName()));
		Match m = Main.gameEngine.getMatch(w);
		if(m != null)
		{
			if(weapon instanceof MineFightEventListener)
			{
				MineFightEventListener listener = (MineFightEventListener)weapon;
				listener.matchCreated(m);
				for(PVPPlayer p : m.getPlayers())
				{
					listener.onJoin(m, p);
				}
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
			if(weapon instanceof MineFightEventListener)
			{
				MineFightEventListener listener = (MineFightEventListener)weapon;
				for(PVPPlayer p : m.getPlayers())
				{
					listener.onLeave(m, p);
				}
				listener.matchEnded(m);
			}
		}
		return Main.gameEngine.weaponRegistry.unregisterWeapon(weapon, w);
	}		
}
