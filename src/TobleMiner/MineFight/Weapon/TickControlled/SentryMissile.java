package TobleMiner.MineFight.Weapon.TickControlled;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;
import TobleMiner.MineFight.Weapon.Stationary.SentryGun;

public class SentryMissile extends TickControlledWeapon
{
	private final Vector dir;
	private final SentryGun sentry;
	private final float exploStr;
	private final Arrow arr;
	private final double speed;
	private final float killRangeMod;
	private int timer = 1;
	private double time = 0d;
	private double lifeTime = 40d;
	
	public SentryMissile(Match match, Vector dir, SentryGun sentry, float exploStr, Arrow arr, double speed, float killRangeMod)
	{
		super(match);
		this.dir = dir;
		this.sentry = sentry;
		this.exploStr = exploStr;
		this.arr = arr;
		this.speed = speed;
		this.killRangeMod = killRangeMod;
	}

	@Override
	public void doUpdate()
	{
		if(timer >= GameEngine.tps/10d)
		{
			time += 0.1d;
			timer = 0;
			if(time > lifeTime)
			{
				this.explode();
			}
			Location loc = this.arr.getLocation();
			EffectSyncCalls.showEffect(loc, Effect.SMOKE, 0);
			EffectSyncCalls.showEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
			Vector vec = dir.clone();
			double mul = this.speed/vec.length();
			this.arr.setVelocity(vec.clone().multiply(mul));
		}
		timer++;
	}
	
	public void explode()
	{
		match.unregisterSentryMissile(this);
		this.unregisterTickControlled();
		match.createExplosion(sentry.getOwner(),arr.getLocation(), exploStr, exploStr*killRangeMod,"SENTRY");
		EntitySyncCalls.removeEntity(this.arr);
	}
	
	public Arrow getArrow()
	{
		return this.arr;
	}
}
