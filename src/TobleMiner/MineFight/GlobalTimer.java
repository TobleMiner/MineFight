package TobleMiner.MineFight;

import java.util.TimerTask;

public class GlobalTimer extends TimerTask
{
	public void run()
	{
		Main.gameEngine.doUpdate();
	}
}
