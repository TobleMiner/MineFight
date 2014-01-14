package TobleMiner.MineFight.Air.Missiles.Stinger;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Air.Airwhatever;
import TobleMiner.MineFight.Air.Missiles.Missile;

public class Stinger extends Missile
{
	private Airwhatever target;
	private int maxHeight;
	final Main main;

		public Stinger(Arrow arrow,Location start, double maxSpeed,double acceleration,Main main)
		{
			super(arrow, start, maxSpeed,0d);
			this.missile = arrow;
			this.start = start;
			this.maxSpeed = maxSpeed;
			this.target = null;
			this.main = main;
			this.acceleration = acceleration;
		}
		
	@Override
	public void update()
	{
		try
		{
			if(this.isDead())
			{
				this.doCancel();
			}
			if(target == null)
			{
				List<Airwhatever> nearEntities = Airwhatever.getNearAirwhatevers(this,10,10,10);
				for(Airwhatever airwhatever : nearEntities)
				{
					this.target = airwhatever;
					this.maxHeight = (int)Math.round((double)target.getLocation().getY()-target.getStartpoint().getY()+target.getVelocity().length()*1.3d);
					this.acceleration = target.getAcceleration()*1.2d;
					return;
				}
			}
			else
			{
				Location targetLoc = target.getLocation();
				if(this.missile == null || this.isDead())
				{
					this.doCancel();
					return;
				}
				double dist_xz_now = Math.sqrt(Math.pow(targetLoc.clone().getX()-missile.getLocation().getX(),2)+Math.pow(targetLoc.clone().getZ()-missile.getLocation().getZ(),2));
				double dist_now = targetLoc.clone().distance(missile.getLocation().clone());
				double dist_xz_all = Math.sqrt(Math.pow(targetLoc.clone().getX()-start.getX(),2)+Math.pow(targetLoc.clone().getZ()-start.getZ(),2));
				if(dist_now < 2d)
				{
					missile.getWorld().createExplosion(missile.getLocation(),4.0f);
					target.doCancel();
					this.doCancel();
					return;
				}
				double height_now_desired_rel = ((double)maxHeight) * Math.sin(dist_xz_now/dist_xz_all*Math.PI);
				double height_now_desired = height_now_desired_rel + targetLoc.getY();
				Location loc_desired = new Location(missile.getWorld(),targetLoc.getX(),height_now_desired,targetLoc.getZ());
				this.speed += acceleration/10d;
				if(speed > maxSpeed)
				{
					speed = maxSpeed;
				}
				Location loc = loc_desired.clone().subtract(missile.getLocation().clone());
				Vector vec = loc.toVector();
				double vec_lenght = vec.length();
				double fact = vec_lenght == 0 ? 1d : Math.abs(maxSpeed/vec_lenght);
				Vector vel = vec.clone().multiply(fact);
				//Smoothing
				Vector targetVel = vel.clone();
				Vector currentVel = this.getVelocity();
				if(targetVel.getX() > currentVel.getX())
				{
					double deltaX = targetVel.getX() - currentVel.getX();
					if(deltaX > acceleration)
					{
						deltaX = acceleration;
					}
					vel = vel.clone().setX(currentVel.clone().getX()+deltaX);
				}
				else if(targetVel.getX() < currentVel.getX())
				{
					double deltaX = currentVel.getX() - targetVel.getX();
					if(deltaX > acceleration)
					{
						deltaX = acceleration;
					}
					vel = vel.clone().setX(currentVel.clone().getX()-deltaX);
					
				}
				if(targetVel.getY() > currentVel.getY())
				{
					double deltaY = targetVel.getY() - currentVel.getY();
					if(deltaY > acceleration)
					{
						deltaY = acceleration;
					}
					vel = vel.clone().setY(currentVel.clone().getY()+deltaY);
				}
				else if(targetVel.getY() < currentVel.getY())
				{
					double deltaY = currentVel.getY() - targetVel.getY();
					if(deltaY > acceleration)
					{
						deltaY = acceleration;
					}
					vel = vel.clone().setY(currentVel.clone().getY()-deltaY);
					
				}
				if(targetVel.getZ() > currentVel.getZ())
				{
					double deltaZ = targetVel.getZ() - currentVel.getZ();
					if(deltaZ > acceleration)
					{
						deltaZ = acceleration;
					}
					vel = vel.clone().setZ(currentVel.clone().getZ()+deltaZ);
				}
				else if(targetVel.getZ() < currentVel.getZ())
				{
					double deltaZ = currentVel.getZ() - targetVel.getZ();
					if(deltaZ > acceleration)
					{
						deltaZ = acceleration;
					}
					vel = vel.clone().setZ(currentVel.clone().getZ()-deltaZ);
					
				}
				missile.setVelocity(vel);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
