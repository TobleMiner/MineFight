package TobleMiner.MineFight;

import java.util.TimerTask;

import TobleMiner.MineFight.Air.Airwhatever;

public class GlobalTimer extends TimerTask
{
	public void run()
	{
		Airwhatever.doUpdate();
		Main.gameEngine.doUpdate();
	}
}
