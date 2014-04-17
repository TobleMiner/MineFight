package TobleMiner.MineFight.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.Weapon.Weapon;

public class WeaponRegistry 
{
	private HashMap<Material, HashMap<Short, Object>> weapons = new HashMap<>();
	private HashMap<String, List<Weapon>> events = new HashMap<>();
	
	public void preregisterMaterial(Material mat, short subId) //Used internally to block materials that are essential for gameplay
	{
		HashMap<Short, Object> wpBySubId = this.weapons.get(mat);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		wpBySubId.put(subId, new Object());
		this.weapons.put(mat, wpBySubId);
	}
	
	public boolean registerWeapon(Weapon weapon)
	{
		HashMap<Short, Object> wpBySubId = this.weapons.get(weapon.material);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		else
			if(wpBySubId.get(weapon.subId) != null)
				return false;

		wpBySubId.put(weapon.subId, weapon);
		this.weapons.put(weapon.material, wpBySubId);
		if(weapon.getRequiredEvents() != null)
		{
			for(String event : weapon.getRequiredEvents())
			{
				List<Weapon> weapons = this.events.get(event);
				if(weapons == null)
					weapons = new ArrayList<Weapon>();
				weapons.add(weapon);
				this.events.put(event, weapons);
			}
		}
		return true;
	}

	public void executeEvent(Match m, Event event) 
	{
		List<Weapon> weapons = this.events.get(event.getClass().getSimpleName());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onEvent(m, event);
	}
}
