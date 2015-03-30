package tobleminer.minefight.config.weapon;

import java.util.HashMap;

import org.bukkit.Material;

import tobleminer.minefight.config.weapon.WeaponDescriptor.DamageType;
import tobleminer.minefight.config.weapon.WeaponDescriptor.WeaponUseType;

public class WeaponIndex 
{
	private HashMap<String, WeaponDescriptor> byName = new HashMap<String, WeaponDescriptor>();
	private HashMap<Material, WeaponDescriptor> byMaterial = new HashMap<Material, WeaponDescriptor>();
	private HashMap<WeaponUseType, WeaponIndex> byUseType = new HashMap<WeaponUseType, WeaponIndex>();
	private HashMap<DamageType, WeaponIndex> byDmgType = new HashMap<DamageType, WeaponIndex>();
	private int num = 0;
	
	public void add(WeaponDescriptor wd)
	{
		this.add(wd, true);
	}
	
	private void add(WeaponDescriptor wd, boolean createSub)
	{
		this.byName.put(wd.name.toLowerCase().trim(), wd);
		this.byMaterial.put(wd.material, wd);
		if(createSub)
		{
			WeaponIndex wi_ut = this.byUseType.get(wd.wut);
			if(wi_ut == null) wi_ut = new WeaponIndex();
			wi_ut.add(wd, false);
			this.byUseType.put(wd.wut, wi_ut);
			WeaponIndex wi_dt = this.byDmgType.get(wd.dmgType);
			if(wi_dt == null) wi_dt = new WeaponIndex();
			wi_dt.add(wd, false);
			this.byDmgType.put(wd.dmgType, wi_dt);
		}
		num++;
	}
	
	public WeaponDescriptor get(String s)
	{
		return this.byName.get(s);
	}
	
	public WeaponDescriptor get(Material m)
	{
		return this.byMaterial.get(m);
	}
	
	public WeaponIndex get(WeaponUseType wut)
	{
		return this.byUseType.get(wut);
	}
	
	public WeaponIndex get(DamageType dt) 
	{
		return this.byDmgType.get(dt);
	}

	public int count()
	{
		return this.num;
	}
}
