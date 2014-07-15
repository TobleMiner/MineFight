package TobleMiner.MineFight.GameEngine.Match.Spawning;

import org.bukkit.Location;

import TobleMiner.MineFight.GameEngine.Match.Match;

public class Spawnengine
{
	private Match match;
	
	public Spawnengine(Match m)
	{
		this.match = m;
	}
	
	public Location findSafeSpawn(Location base)
	{
		return base;
	}
}
