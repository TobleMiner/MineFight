package tobleminer.minefight.config.container;

import java.util.HashMap;

public class KillstreakConfig 
{
	private HashMap<Integer, Killstreak> killstreaks = new HashMap<Integer, Killstreak>();
		
	public void add(int streak, Killstreak ks)
	{
		this.killstreaks.put(streak, ks);
	}
	
	public Killstreak getKillstreak(int streak)
	{
		return this.killstreaks.get(streak);
	}
}
