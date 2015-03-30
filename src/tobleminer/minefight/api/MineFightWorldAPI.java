package tobleminer.minefight.api;

import java.util.List;

import org.bukkit.World;

import tobleminer.minefight.Main;

public class MineFightWorldAPI 
{
	public static MineFightWorldAPI instance;

	public MineFightWorldAPI()
	{
		instance = this;
	}

	public List<World> getKnownWorlds()
	{
		return Main.gameEngine.configuration.getLoadtimeWorlds();
	}
}
