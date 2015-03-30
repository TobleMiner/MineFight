package tobleminer.minefight.weapon.tickcontrolled.missile;

import org.bukkit.entity.Arrow;

import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.syncderp.EntitySyncCalls;
import tobleminer.minefight.weapon.tickcontrolled.TickControlledWeapon;

public abstract class Missile extends TickControlledWeapon
{
	protected final PVPPlayer shooter;
	protected final Arrow arr;
	
	public Missile(Match m, PVPPlayer shooter, Arrow arr)
	{
		super(m);
		this.arr = arr;
		this.shooter = shooter;
		match.addMissile(this);
	}
	
	protected boolean canTarget(PVPPlayer target)
	{
		return (this.shooter.getTeam() != target.getTeam() && target.isSpawned());
	}
	
	public void explode()
	{
		this.unregisterTickControlled();
		match.rmMissile(this);
		EntitySyncCalls.removeEntity(arr);
	}
	
	public Arrow getProjectile()
	{
		return this.arr;
	}
}
