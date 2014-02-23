package TobleMiner.MineFight.Weapon.TickControlled;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;

public class RPG extends TickControlledWeapon
{

	private Arrow arr;
	private int timer = 1;
	private double time = 0d;
	private final float exploStr;
	private final double lifeTime;
	private final double maxSpeed;
	private final double accel;
	private final Vector launchVec;
	private double speed = 0d;
	private final PVPPlayer owner;
	
	public RPG(Match match, Arrow arr, float exploStr, double lifeTime, double maxSpeed, double accel, Vector launchVec, double throtle, PVPPlayer owner)
	{
		super(match);
		this.arr = arr;
		this.exploStr = exploStr;
		this.lifeTime = lifeTime;
		this.maxSpeed = maxSpeed;
		this.launchVec = launchVec;
		this.accel = accel;
		this.owner = owner;
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
			Vector vec = launchVec.clone();
			if(this.speed < this.maxSpeed)
			{
				this.speed += this.accel/10d;
			}
			else
			{
				this.speed = this.maxSpeed;
			}
			double mul = this.speed/vec.length();
			this.arr.setVelocity(vec.clone().multiply(mul)/*.add(new Vector((rand.nextDouble()-0.5d)*throtle*speed,(rand.nextDouble()-0.5d)*throtle*speed,(rand.nextDouble()-0.5d)*throtle*speed))*/);
			launchVec.setY(launchVec.getY()*0.97d);
		}
		timer++;
	}
	
	public Arrow getProjectile()
	{
		return this.arr;
	}

	public void explode() 
	{
		match.unregisterRPG(this);
		this.unregisterTickControlled();
		match.createExplosion(owner, arr.getLocation(), exploStr, "RPG");
		EntitySyncCalls.removeEntity(this.arr);
	}
}
