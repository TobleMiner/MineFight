package TobleMiner.MineFight.Util.SyncDerp;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import TobleMiner.MineFight.Main;

public class InventorySyncCalls 
{
	public static void removeItemStack(final Inventory i, final ItemStack is)
	{
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				i.removeItem(is);
			}
		});
	}
}
