package tobleminer.minefight.api;

import java.util.List;

import org.bukkit.event.Event;

import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;

public interface MineFightEventListener 
{
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
