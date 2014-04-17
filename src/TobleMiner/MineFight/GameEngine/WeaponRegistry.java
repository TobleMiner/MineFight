package TobleMiner.MineFight.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.Event;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.Weapon;

public class WeaponRegistry 
{
	private HashMap<Material, HashMap<Short, Object>> weaponsByMaterial = new HashMap<>();
	private HashMap<String, List<Weapon>> events = new HashMap<>();
	private List<Weapon> weapons = new ArrayList<Weapon>();
	
	public void preregisterMaterial(Material mat, short subId) //Used internally to block materials that are essential for gameplay
	{
		HashMap<Short, Object> wpBySubId = this.weaponsByMaterial.get(mat);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		wpBySubId.put(subId, new Object());
		this.weaponsByMaterial.put(mat, wpBySubId);
	}
	
	public boolean registerWeapon(Weapon weapon)
	{
		HashMap<Short, Object> wpBySubId = this.weaponsByMaterial.get(weapon.material);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		else
			if(wpBySubId.get(weapon.subId) != null)
				return false;

		wpBySubId.put(weapon.subId, weapon);
		this.weaponsByMaterial.put(weapon.material, wpBySubId);
		if(weapon.getRequiredEvents() != null)
		{
			for(String event : weapon.getRequiredEvents())
			{
				List<Weapon> weaponsByMaterial = this.events.get(event);
				if(weaponsByMaterial == null)
					weaponsByMaterial = new ArrayList<Weapon>();
				weaponsByMaterial.add(weapon);
				this.events.put(event, weaponsByMaterial);
			}
		}
		weapons.add(weapon);
		return true;
	}

	public void executeEvent(Match m, Event event) 
	{
		List<Weapon> weapons = this.events.get(event.getClass().getSimpleName());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onEvent(m, event);
	}
	
	public void playerKilled(Match m, PVPPlayer killer, PVPPlayer killed)
	{
		for(Weapon weapon : this.weapons)
			weapon.onKill(m, killer, killed);
	}
	
	public void playerDied(Match m, PVPPlayer killed, PVPPlayer killer)
	{
		for(Weapon weapon : this.weapons)
			weapon.onDeath(m, killed, killer);
	}
	
	public void playerRespawned(Match m, PVPPlayer player)
	{
		for(Weapon weapon : this.weapons)
			weapon.onRespawn(m, player);
	}
	
	public void matchCreated(Match m)
	{
		for(Weapon weapon : this.weapons)
			weapon.matchCreated(m);
	}
	
	public void matchEnded(Match m)
	{
		for(Weapon weapon : this.weapons)
			weapon.matchEnded(m);
	}
	
	public void onTick()
	{
		for(Weapon weapon : this.weapons)
			weapon.onTick();
	}
}
