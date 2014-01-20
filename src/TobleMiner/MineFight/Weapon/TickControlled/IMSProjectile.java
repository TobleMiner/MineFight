package TobleMiner.MineFight.Weapon.TickControlled;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;

public class IMSProjectile extends TickControlledWeapon
{
	
	private final Arrow arr;
	private final double projectileSpeed;
	private final double projectilePeekHeight;
	private final IMS ims;
	private final PVPPlayer target;
	private final float exploStr;
	private double time = 0;
	private int timer = 1;
	private int state = 0;
	private final Location startLoc;

	public IMSProjectile(Match match, Location startLoc, Arrow arr, double projectileSpeed, double projectilePeekHeight, IMS ims, PVPPlayer target, float exploStr)
	{
		super(match);
		this.ims = ims;
		this.arr = arr;
		this.projectileSpeed = projectileSpeed;
		this.projectilePeekHeight = projectilePeekHeight;
		this.target = target;
		this.exploStr = exploStr;
		this.startLoc = startLoc;
	}

	@Override
	public void doUpdate()
	{
		if(timer > GameEngine.tps/10d)
		{
			timer = 0;
			time += 0.1d;
			if(time > 60d)
			{
				this.unregisterTickControlled();
				EntitySyncCalls.removeEntity(arr);
				ims.release(this.target);
				return;
			}
			if(state == 0)
			{
				Location locDesired = startLoc.clone().add(0d,projectilePeekHeight,0d);
				Vector dir = locDesired.clone().subtract(this.arr.getLocation()).toVector();
				Vector vel = dir.clone().multiply(projectileSpeed/dir.length());
				this.arr.setVelocity(vel);
				if(this.arr.getLocation().distance(locDesired) < 2d)
				{
					this.state++;
				}
			}
			else
			{
				Location locDesired = this.target.thePlayer.getLocation();
				Vector dir = locDesired.clone().subtract(this.arr.getLocation()).toVector();
				Vector vel = dir.clone().multiply(projectileSpeed/Math.abs(dir.length()));
				this.arr.setVelocity(vel);
				if(this.arr.getLocation().distance(locDesired) < 2d)
				{
					this.explode();
				}
			}
		}
		timer++;		
	}

	private void explode()
	{
		this.unregisterTickControlled();
		if(match.canKill(ims.getOwner(),target))
		{
			match.kill(ims.getOwner(), target, "IMS", target.thePlayer.getHealth() > 0);
		}
		match.createExplosion(ims.getOwner(), this.arr.getLocation(), exploStr, exploStr,"IMS");
		EntitySyncCalls.removeEntity(arr);
		ims.release(this.target);
	}
}
