package TobleMiner.MineFight.Weapon.TickControlled;

import org.bukkit.Location;
import org.bukkit.entity.Item;

import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;

public class HandGrenade extends TickControlledWeapon
{
	private final Item item;
	private final PVPPlayer owner;
	private final Match match;
	private int timer = 0;
	private final float exploStr;
	private final float fuse;
	private final float killRangeMod;
	
	public HandGrenade(Item item,PVPPlayer owner,Match match,float exploStr,float fuse,float throwSpeed,float killRangeMod)
	{
		super(match);
		this.match = match;
		this.item = item;
		this.owner = owner;
		this.exploStr = exploStr;
		this.fuse = fuse;
		double fact = throwSpeed/item.getVelocity().clone().length();
		item.setVelocity(item.getVelocity().clone().multiply(fact));
		this.killRangeMod = killRangeMod;
	}

	@Override
	public void doUpdate()
	{
		timer++;
		if(timer > (this.fuse*GameEngine.tps))
		{
			Location loc = item.getLocation();
			EntitySyncCalls.removeEntity(item);
			this.match.createExplosion(owner, loc, exploStr, exploStr*killRangeMod,"M67 GRENADE");
			this.unregisterTickControlled();
			match.unregisterHandGrenade(this.item);
		}
	}
}
