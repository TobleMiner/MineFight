package TobleMiner.MineFight.Weapon;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.Event;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

public abstract class Weapon 
{	
	public Material material;
	public short subId;
	
	public abstract List<String> getRequiredEvents();
	public abstract void onEvent(Match m, Event event);
	
	public abstract String onKill(Match m, PVPPlayer killer, PVPPlayer killed);
	public abstract String onDeath(Match m, PVPPlayer killed, PVPPlayer killer);
	public abstract void onRespawn(Match m, PVPPlayer player);
	
	public abstract void matchCreated(Match m);
	public abstract void matchEnded(Match m);
	public abstract void onTick();
	
}
