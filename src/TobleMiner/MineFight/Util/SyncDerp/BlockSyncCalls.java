package TobleMiner.MineFight.Util.SyncDerp;

import org.bukkit.block.BlockState;

import TobleMiner.MineFight.Main;

public class BlockSyncCalls 
{
	public static void updateBlockstate(final BlockState bs)
	{
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				bs.update();
			}
		});
	}
}
