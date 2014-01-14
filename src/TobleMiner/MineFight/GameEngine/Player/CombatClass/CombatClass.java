package TobleMiner.MineFight.GameEngine.Player.CombatClass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import TobleMiner.MineFight.Weapon.WeaponType;

public class CombatClass 
{
	public final String name;
	public final WeaponType wt;
	public List<ItemStack> kit = new ArrayList<ItemStack>();
	public ItemStack[] armor = new ItemStack[4];
	
	public CombatClass(String name, WeaponType wt)
	{
		this.name = name;
		this.wt = wt;
	}
}
