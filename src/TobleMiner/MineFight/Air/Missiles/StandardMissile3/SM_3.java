package TobleMiner.MineFight.Air.Missiles.StandardMissile3;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Air.Airwhatever;
import TobleMiner.MineFight.Air.Missiles.Missile;
import TobleMiner.MineFight.Air.Targets.Flight;

public class SM_3 extends Missile
{
	final Arrow missile;
	final Flight flight;
	final Location start;
	final int maxHeight;
	final double maxSpeed;
	final double acceleration;
	private double speed = 0;
	private final Location temp_target;
	private Airwhatever target_locked = null;
	public SM_3(Arrow arrow,Location start, int maxHeight, double maxSpeed,double acceleration,Flight flight,Main mane)
	{
		super(arrow, start, maxSpeed, acceleration);
		this.missile = arrow;
		this.start = start;
		this.maxHeight = maxHeight;
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
		this.flight = flight;
		this.temp_target = flight.getLocation();
	}
	
	@Override
	public void update()
	{
		try
		{
			Location target = temp_target;
			if(target_locked != null)
			{
				target = target_locked.getLocation().clone();
			}
			if(this.isDead())
			{
				this.doCancel();
				return;
			}
			double dist_xz_now = Math.sqrt(Math.pow(target.clone().getX()-missile.getLocation().getX(),2)+Math.pow(target.clone().getZ()-missile.getLocation().getZ(),2));
			double dist_now = target.clone().distance(missile.getLocation().clone());
			double dist_xz_all = Math.sqrt(Math.pow(target.clone().getX()-start.getX(),2)+Math.pow(target.clone().getZ()-start.getZ(),2));
			if(dist_now < 20d)
			{
				if(target_locked == null)
				{
					List<Airwhatever> entities = Airwhatever.getNearAirwhatevers(this,50d,20d,50d);
					for(Airwhatever entity : entities)
					{
						this.target_locked = entity;
						break;
					}
				}
				else
				{
					if(dist_now < 20d)
					{
						Vector vec = target_locked.getLocation().clone().subtract(this.getLocation().clone()).toVector().multiply(0.5d);
						for(int i=0;i<12;i++)
						{
							Arrow arrow = this.missile.getWorld().spawnArrow(this.getLocation(),vec,(float)vec.length(),1.0f);
							new SM_3_sub(arrow,this.getLocation(),target_locked);						}
						this.doCancel();
						return;
					}
				}
			}
			double height_now_desired_rel = ((double)maxHeight) * Math.sin(dist_xz_now/dist_xz_all*Math.PI);
			double height_now_desired = height_now_desired_rel + target.getY();
			Location loc_desired = new Location(missile.getWorld(),target.getX(),height_now_desired,target.getZ());
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
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
