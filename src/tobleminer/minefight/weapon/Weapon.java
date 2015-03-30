package tobleminer.minefight.weapon;

import org.bukkit.Material;
import org.bukkit.World;

public interface Weapon 
{	
	public abstract Material getMaterial(World w);
	public abstract short getSubId(World w);
}
