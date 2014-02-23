package TobleMiner.MineFight.Weapon.TickControlled;

import org.bukkit.Location;
import org.bukkit.entity.Item;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;

public class Claymore
{
	public final Item claymore;
	public final PVPPlayer owner;
	private final float exploStr;
	private final Match match;
	private boolean exploded = false;
	
	public Claymore(Item is, PVPPlayer owner, float exploStr, Match match)
	{
		this.claymore = is;
		this.owner = owner;
		this.exploStr = exploStr;
		this.match = match;
	}
	
	public void explode()
	{
		if(exploded) return;
		this.exploded = true;
		Location loc = this.claymore.getLocation().clone();
		EntitySyncCalls.removeEntity(claymore);
		match.createExplosion(this.owner, loc, this.exploStr, "CLAYMORE");
	}
}
