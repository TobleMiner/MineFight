package TobleMiner.MineFight.API;

import org.bukkit.plugin.Plugin;

import TobleMiner.MineFight.ErrorHandling.Logger;

public class MineFightAPI 
{
	public static MineFightAPI instance;
	
	public MineFightAPI()
	{
		instance = this;
	}
	
	public Logger getLogger(Plugin p)
	{
		return new Logger(p);
	}
}
