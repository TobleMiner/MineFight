package TobleMiner.MineFight.Weapon.TickControlled.Missile;

import org.bukkit.entity.Arrow;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFight.Weapon.TickControlled.TickControlledWeapon;

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
