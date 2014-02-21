package TobleMiner.MineFight.GameEngine.Player.CombatClass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CombatClass 
{
	public final String name;
	public List<ItemStack> kit = new ArrayList<ItemStack>();
	public ItemStack[] armor = new ItemStack[4];
	
	public CombatClass(String name)
	{
		this.name = name;
	}
}
