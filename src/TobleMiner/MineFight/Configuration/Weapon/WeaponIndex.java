package TobleMiner.MineFight.Configuration.Weapon;

import java.util.HashMap;

import org.bukkit.Material;

import TobleMiner.MineFight.Configuration.Weapon.WeaponDescriptor.WeaponUseType;

public class WeaponIndex 
{
	private HashMap<String, WeaponDescriptor> byName = new HashMap<String, WeaponDescriptor>();
	private HashMap<Material, WeaponDescriptor> byMaterial = new HashMap<Material, WeaponDescriptor>();
	private HashMap<WeaponUseType, WeaponIndex> byUseType = new HashMap<WeaponUseType, WeaponIndex>();
	private int num = 0;
	
	public void add(WeaponDescriptor wd)
	{
		this.add(wd, true);
	}
	
	private void add(WeaponDescriptor wd, boolean createSub)
	{
		this.byName.put(wd.name, wd);
		this.byMaterial.put(wd.material, wd);
		if(createSub)
		{
			WeaponIndex wi = this.byUseType.get(wd.wut);
			if(wi == null) wi = new WeaponIndex();
			wi.add(wd, false);
			this.byUseType.put(wd.wut, wi);
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
	
	public int count()
	{
		return this.num;
	}
}
