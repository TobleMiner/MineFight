package TobleMiner.MineFight.Air.Targets;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Air.Airwhatever;

public class Flight extends Airwhatever
{

	final Arrow aircraft;
	final Location start;
	final Location target;
	public final int maxHeight;
	final double maxSpeed;
	final double acceleration;
	private double speed = 0;
	
	public Flight(Arrow arrow,Location start, Location target, int maxHeight, double maxSpeed,double acceleration)
	{
		super();
		this.aircraft = arrow;
		this.start = start;
		this.target = target;
		this.maxHeight = maxHeight;
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
	}
	
	public Location getLocation()
	{
		return this.aircraft.getLocation().clone();
	}
	
	@Override
	public void update()
	{
		Location target = this.target.clone();
		try
		{
			if(this.isDead())
			{
				this.doCancel();
				return;
			}
			double dist_now = Math.abs(aircraft.getLocation().clone().distance(target.clone()));
			double dist_xz_now = Math.sqrt(Math.pow(target.getX()-aircraft.getLocation().clone().getX(),2)+Math.pow(target.getZ()-aircraft.getLocation().clone().getZ(),2));
			double dist_xz_all = Math.sqrt(Math.pow(target.getX()-start.getX(),2)+Math.pow(target.getZ()-start.getZ(),2));
			if(dist_now < 2d)
			{
				aircraft.getWorld().createExplosion(aircraft.getLocation(),4.0f);
				this.doCancel();
				return;
			}
			double height_now_desired_rel = ((double)maxHeight) * Math.sin(dist_xz_now/dist_xz_all*Math.PI);
			double height_now_desired = height_now_desired_rel + start.getY();
			Location loc_desired = new Location(aircraft.getWorld(),target.getX(),height_now_desired,target.getZ());
			this.speed += acceleration/10d;
			if(speed > maxSpeed)
			{
				speed = maxSpeed;
			}
			Location loc = loc_desired.clone().subtract(aircraft.getLocation().clone());
			Vector vec = loc.toVector();
			double vec_lenght = vec.length();
			double fact = vec_lenght == 0 ? 1d : Math.abs(maxSpeed/vec_lenght);
			aircraft.setVelocity(vec.clone().multiply(fact));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public Vector getVelocity()
	{
		return this.aircraft.getVelocity().clone();
	}

	@Override
	public boolean isDead()
	{
		return (this.aircraft == null ? true : (this.aircraft.isDead() || this.aircraft.isOnGround()));
	}

	@Override
	public double getAcceleration()
	{
		return this.acceleration;
	}
	
	@Override
	public void doCancel()
	{
		super.doCancel();
		this.aircraft.remove();
	}

	@Override
	public Location getStartpoint()
	{
		return start.clone();
	}
}
