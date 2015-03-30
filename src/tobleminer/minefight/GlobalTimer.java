package tobleminer.minefight;

import org.bukkit.scheduler.BukkitRunnable;

public class GlobalTimer extends BukkitRunnable
{
	public void run()
	{
		Main.gameEngine.doUpdate();
	}
}
