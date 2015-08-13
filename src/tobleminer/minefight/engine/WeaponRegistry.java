package tobleminer.minefight.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;

import tobleminer.minefight.weapon.Weapon;

public class WeaponRegistry
{
	private HashMap<World, HashMap<Material, HashMap<Short, Object>>>	weaponsByWorldByMaterialBySubid	= new HashMap<>();
	private HashMap<World, List<Weapon>>								weaponsByWorld					= new HashMap<>();

	public void preregisterMaterial(Material mat, short subId, World w) // Used
																		// internally
																		// to
																		// block
																		// materials
																		// that
																		// are
																		// essential
																		// for
																		// gameplay
	{
		HashMap<Material, HashMap<Short, Object>> wpByMat = this.weaponsByWorldByMaterialBySubid.get(w);
		if (wpByMat == null)
			wpByMat = new HashMap<Material, HashMap<Short, Object>>();
		HashMap<Short, Object> wpBySubId = wpByMat.get(mat);
		if (wpBySubId == null)
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
		if (wpByMat == null)
			wpByMat = new HashMap<Material, HashMap<Short, Object>>();
		HashMap<Short, Object> wpBySubId = wpByMat.get(mat);
		if (wpBySubId == null)
			wpBySubId = new HashMap<Short, Object>();
		else if (wpBySubId.get(subId) != null)
			return false;
		wpBySubId.put(subId, weapon);
		wpByMat.put(mat, wpBySubId);
		this.weaponsByWorldByMaterialBySubid.put(w, wpByMat);
		List<Weapon> weapons = this.weaponsByWorld.get(w);
		if (weapons == null)
			weapons = new ArrayList<>();
		weapons.add(weapon);
		this.weaponsByWorld.put(w, weapons);
		return true;
	}

	public boolean unregisterWeapon(Weapon weapon, World w)
	{
		HashMap<Material, HashMap<Short, Object>> wpByMat = this.weaponsByWorldByMaterialBySubid.get(w);
		if (wpByMat == null)
			return false; // Weapon obviously not registered for the given world
		for (Material mat : wpByMat.keySet())
		{
			HashMap<Short, Object> wpBySubId = wpByMat.get(mat);
			if (wpBySubId == null)
				return false;
			boolean found = false;
			for (Short subid : wpBySubId.keySet())
			{
				Object wp = wpBySubId.get(subid);
				if (wp == weapon)
				{
					wpBySubId.remove(weapon);
					found = true;
				}
				if (!found)
					return false;
			}
			wpByMat.put(mat, wpBySubId);
		}
		this.weaponsByWorldByMaterialBySubid.put(w, wpByMat);
		List<Weapon> weapons = this.weaponsByWorld.get(w);
		if (weapons == null)
			return false;
		if (!weapons.remove(weapon))
			return false;
		return true;
	}
}
