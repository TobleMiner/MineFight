package TobleMiner.MineFight.API;

import org.bukkit.World;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.Weapon;

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
