package TobleMiner.MineFight.Configuration.Weapon;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer.HitZone;

public class WeaponDescriptor
{
	public final String name;
	private final boolean translate;
	public final double cadence;
	public final double speed;
	public final WeaponUseType wut;
	public final DamageType dmgType;
	public final Material material;
	public final short itemdmg;
	public final HashMap<Double, Double> damage = new HashMap<Double, Double>();
	public final HashMap<HitZone, Double> multipliers = new HashMap<HitZone, Double>();
	public final double maxDist;
	
	public WeaponDescriptor(String name, boolean doTranslate, double cadence, double speed, String useType, String dmgType, Material material, short itemdmg, List<Entry<Double ,Double>> damage, List<Entry<HitZone ,Double>> multipliers)
	{
		this.name = name;
		this.translate = doTranslate;
		this.cadence = cadence;
		this.speed = speed;
		this.wut = WeaponUseType.get(useType);
		this.dmgType = DamageType.get(dmgType);
		this.material = material;
		this.itemdmg = itemdmg;
		double maxDist = Double.MAX_VALUE;
		for(Entry<Double, Double> entry : damage)
		{
			this.damage.put(entry.getKey(), entry.getValue());
			if(entry.getValue() == 0d)
			{
				maxDist = Math.min(maxDist, entry.getKey());
			}
		}
		this.maxDist = maxDist;
		for(Entry<HitZone, Double> entry : multipliers)
		{
			this.multipliers.put(entry.getKey(), entry.getValue());
		}
	}
	
	public String getName()
	{
		if(translate)
		{
			return Main.gameEngine.dict.get(this.name);
		}
		return this.name;
	}
	
	public double getDamage(double length)
	{
		Entry<Double, Double> above = null;
		Entry<Double, Double> under = null;
		for(Entry<Double, Double> entry : this.damage.entrySet())
		{
			if((above == null && entry.getKey() > length) || (above != null && entry.getKey() > length && entry.getKey() < above.getKey()))
			{
				above = entry;
				continue;
			}
			if((under == null && entry.getKey() < length) || (under != null && entry.getKey() < length && entry.getKey() > under.getKey()))
			{
				under = entry;
				continue;
			}
		}
		if(above != null || under != null)
		{
			if(above == null)
			{
				return under.getValue();
			}
			if(under == null)
			{
				return above.getValue();
			}
			double delta = under.getValue() - above.getValue();
			return (under.getValue() + delta * ((length - under.getKey()) / (above.getKey() - under.getKey())));
		}
		return 0d;
	}
	
	public static enum DamageType
	{
		HIT("hit"),
		FLAMETHROWER("flamethrower","flame"),
		MEDIGUN("medigun","medi"),
		PROJECTILEHIT("projectilehit","projhit","phit"),
		NONE("applejack");
		
		public final String[] names;
		
		private DamageType(String ... names)
		{
			this.names = names;
		}
		
		public static DamageType get(String name)
		{
			name = name.toLowerCase();
			for(DamageType dt : values())
			{
				for(String n : dt.names)
				{
					if(n.equals(name))
					{
						return dt;
					}
				}
			}
			return DamageType.NONE;
		}
	}
	
	public static enum WeaponUseType
	{
		HIT("hit"),
		BLOCK("block","blocking"),
		NONE("fluttershy");
		
		public final String[] names;
		
		private WeaponUseType(String ... names)
		{
			this.names = names;
		}
		
		public static WeaponUseType get(String name)
		{
			name = name.toLowerCase();
			for(WeaponUseType wut : values())
			{
				for(String n : wut.names)
				{
					if(n.equals(name))
					{
						return wut;
					}
				}
			}
			return WeaponUseType.NONE;
		}
	}
}
