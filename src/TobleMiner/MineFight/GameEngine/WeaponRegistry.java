package TobleMiner.MineFight.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;

import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.Weapon;

public class WeaponRegistry 
{
	private HashMap<World, HashMap<Material, HashMap<Short, Object>>> weaponsByWorld = new HashMap<>();
	private HashMap<String, List<Weapon>> events = new HashMap<>();
	private List<Weapon> weapons = new ArrayList<Weapon>();
	
	public void preregisterMaterial(Material mat, short subId, World w) //Used internally to block materials that are essential for gameplay
	{
		HashMap<Material, HashMap<Short, Object>> wpByMat= this.weaponsByWorld.get(w);
		if(wpByMat == null)
			wpByMat = new HashMap<Material, HashMap<Short, Object>>();
		HashMap<Short, Object> wpBySubId = wpByMat.get(mat);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		wpBySubId.put(subId, new Object());
		wpByMat.put(mat, wpBySubId);
		this.weaponsByWorld.put(w, wpByMat);
	}
	
	public boolean registerWeapon(Weapon weapon, World w)
	{
		short subId = weapon.getSubId(w);
		Material mat = weapon.getMaterial(w);
		HashMap<Material, HashMap<Short, Object>> wpByMat = this.weaponsByWorld.get(w);
		if(wpByMat == null)
			wpByMat = new HashMap<Material, HashMap<Short, Object>>();
		HashMap<Short, Object> wpBySubId = wpByMat.get(subId);
		if(wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		else
			if(wpBySubId.get(subId) != null)
				return false;
		wpBySubId.put(subId, weapon);
		wpByMat.put(mat, wpBySubId);
		this.weaponsByWorld.put(w, wpByMat);
		List<Class<?>> events = new ArrayList<>();
		weapon.getRequiredEvents(events);
		for(Class<?> event : events)
		{
			List<Weapon> weaponsByEvent = this.events.get(event.getSimpleName());
			if(weaponsByEvent == null)
				weaponsByEvent = new ArrayList<Weapon>();
			if(!weaponsByEvent.contains(weapon)) 
				weaponsByEvent.add(weapon);
			this.events.put(event.getSimpleName(), weaponsByEvent);
		}
		if(!weapons.contains(weapon))
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

	public void playerJoined(Match m, PVPPlayer player)
	{
		for(Weapon weapon : this.weapons)
			weapon.onJoin(m, player);
	}

	public void playerLeft(Match m, PVPPlayer player)
	{
		for(Weapon weapon : this.weapons)
			weapon.onLeave(m, player);
	}

	public void playerChangedTeam(Match m, PVPPlayer player)
	{
		for(Weapon weapon : this.weapons)
			weapon.onTeamchange(m, player);
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
