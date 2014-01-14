package TobleMiner.MineFight.Util.SyncDerp;

import org.bukkit.entity.Entity;

import TobleMiner.MineFight.Main;

public class EntitySyncCalls 
{
	public static void removeEntity(final Entity ent)
	{
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				ent.remove();
			}
		});
	}
}
