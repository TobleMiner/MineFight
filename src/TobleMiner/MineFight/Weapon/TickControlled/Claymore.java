package TobleMiner.MineFight.Weapon.TickControlled;

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
	private final float killRangeMod;
	
	public Claymore(Item is,PVPPlayer owner,float exploStr,Match match,float killRangeMod)
	{
		this.claymore = is;
		this.owner = owner;
		this.exploStr = exploStr;
		this.match = match;
		this.killRangeMod = killRangeMod;
	}
	
	public void explode()
	{
		EntitySyncCalls.removeEntity(claymore);
		match.createExplosion(this.owner,this.claymore.getLocation(),exploStr,this.exploStr*this.killRangeMod,"M18 CLAYMORE");
	}
}
