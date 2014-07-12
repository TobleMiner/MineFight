package TobleMiner.MineFight.Weapon;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

public interface Weapon 
{	
	public abstract Material getMaterial(World w);
	public abstract short getSubId(World w);
	
	public abstract void getRequiredEvents(List<Class<?>> events);
	public abstract void onEvent(Match m, Event event);
	
	public abstract void onJoin(Match m, PVPPlayer player);
	public abstract void onLeave(Match m, PVPPlayer player);
	public abstract void onTeamchange(Match m, PVPPlayer player);
	public abstract void onKill(Match m, PVPPlayer killer, PVPPlayer killed);
	public abstract void onDeath(Match m, PVPPlayer killed, PVPPlayer killer);
	public abstract void onRespawn(Match m, PVPPlayer player);
	
	public abstract void matchCreated(Match m);
	public abstract void matchEnded(Match m);
	public abstract void onTick();
}
