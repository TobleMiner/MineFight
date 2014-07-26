package TobleMiner.MineFight.API;

import org.bukkit.World;

import TobleMiner.MineFight.Main;

public class MineFightEventAPI 
{
	public static MineFightEventAPI instance;
	
	public MineFightEventAPI()
	{
		instance = this;
	}
	
	public void registerEventListener(MineFightEventListener listener, World w)
	{
		Main.gameEngine.registerEventListener(listener, w);
	}

	public void unregisterEventListener(MineFightEventListener listener, World w)
	{
		Main.gameEngine.unregisterEventListener(listener, w);
	}
}
