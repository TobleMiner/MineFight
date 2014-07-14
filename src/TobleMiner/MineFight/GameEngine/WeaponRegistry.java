package TobleMiner.MineFight.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.Weapon;

public class WeaponRegistry 
{
	private HashMap<World, HashMap<Material, HashMap<Short, Object>>> weaponsByWorldByMaterialBySubid = new HashMap<>();
	private HashMap<World, HashMap<String, List<Weapon>>> eventsByWorld = new HashMap<>();
	private HashMap<World, List<Weapon>> weaponsByWorld = new HashMap<>();
	
	public void preregisterMaterial(Material mat, short subId, World w) //Used internally to block materials that are essential for gameplay
	{
		HashMap<Material, HashMap<Short, Object>> wpByMat= this.weaponsByWorldByMaterialBySubid.get(w);
		if(wpByMat == null)
			wpByMat = new HashMap<Material, HashMap<Short, Object>>();
		HashMap<Short, Object> wpBySubId = wpByMat.get(mat);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		wpBySubId.put(subId, new Object());
		wpByMat.put(mat, wpBySubId);
		this.weaponsByWorldByMaterialBySubid.put(w, wpByMat);
	}
	
	public boolean registerWeapon(Weapon weapon, World w)
	{
		short subId = weapon.getSubId(w);
		Material mat = weapon.getMaterial(w);
		HashMap<Material, HashMap<Short, Object>> wpByMat = this.weaponsByWorldByMaterialBySubid.get(w);
		if(wpByMat == null)
			wpByMat = new HashMap<Material, HashMap<Short, Object>>();
		HashMap<Short, Object> wpBySubId = wpByMat.get(mat);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		else
			if(wpBySubId.get(subId) != null)
				return false;
		wpBySubId.put(subId, weapon);
		wpByMat.put(mat, wpBySubId);
		this.weaponsByWorldByMaterialBySubid.put(w, wpByMat);
		List<Class<?>> events = new ArrayList<>();
		weapon.getRequiredEvents(events);
		for(Class<?> event : events)
		{
			HashMap<String, List<Weapon>> eventsByName = this.eventsByWorld.get(w);
			if(eventsByName == null)
				eventsByName = new HashMap<>();
			List<Weapon> weaponsByEvent = eventsByName.get(event.getSimpleName());
			if(weaponsByEvent == null)
				weaponsByEvent = new ArrayList<Weapon>();
			weaponsByEvent.add(weapon);
			eventsByName.put(event.getSimpleName(), weaponsByEvent);
			this.eventsByWorld.put(w, eventsByName);
		}
		List<Weapon> weapons = this.weaponsByWorld.get(w);
		if(weapons == null)
			weapons = new ArrayList<>();
		weapons.add(weapon);
		this.weaponsByWorld.put(w, weapons);
		return true;
	}

	public boolean unregisterWeapon(Weapon weapon, World w)
	{
		HashMap<Material, HashMap<Short, Object>> wpByMat = this.weaponsByWorldByMaterialBySubid.get(w);
		if(wpByMat == null)
			return false; //Weapon obviously not registered for the given world
		for(Material mat : wpByMat.keySet())
		{
			HashMap<Short, Object> wpBySubId = wpByMat.get(mat);
			if(wpBySubId == null)
				return false;
			boolean found = false;
			for(Short subid : wpBySubId.keySet())
			{
				Object wp = wpBySubId.get(subid);
				if(wp == weapon)
				{
					wpBySubId.remove(weapon);
					found = true;
				}
				if(!found)
					return false;
			}
			wpByMat.put(mat, wpBySubId);
		}
		this.weaponsByWorldByMaterialBySubid.put(w, wpByMat);
		List<Weapon> weapons = this.weaponsByWorld.get(w);
		if(weapons == null)
			return false;
		if(!weapons.remove(weapon))
			return false;
		HashMap<String, List<Weapon>> weaponsByEvent = this.eventsByWorld.get(w);
		for(String eventname : weaponsByEvent.keySet())
		{
			List<Weapon> weaponsEvent = weaponsByEvent.get(eventname);
			if(weaponsEvent != null)
				weaponsEvent.remove(weapon);
		}
		return true;
	}
	
	public void executeEvent(Match m, Event event) 
	{
		HashMap<String, List<Weapon>> events = this.eventsByWorld.get(m.getWorld());
		if(events == null) return;
		List<Weapon> weapons = events.get(event.getClass().getSimpleName());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onEvent(m, event);
	}

	public void playerJoined(Match m, PVPPlayer player)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onJoin(m, player);
	}

	public void playerLeft(Match m, PVPPlayer player)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onLeave(m, player);
	}

	public void playerChangedTeam(Match m, PVPPlayer player)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onTeamchange(m, player);
	}

	public void playerKilled(Match m, PVPPlayer killer, PVPPlayer killed)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onKill(m, killer, killed);
	}
	
	public void playerDied(Match m, PVPPlayer killed, PVPPlayer killer)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onDeath(m, killed, killer);
	}
	
	public void playerRespawned(Match m, PVPPlayer player)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onRespawn(m, player);
	}
	
	public void matchCreated(Match m)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.matchCreated(m);
	}
	
	public void matchEnded(Match m)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.matchEnded(m);
	}
	
	public void onTick(Match m)
	{
		List<Weapon> weapons = this.weaponsByWorld.get(m.getWorld());
		if(weapons == null) return;
		for(Weapon weapon : weapons)
			weapon.onTick();
	}
}
