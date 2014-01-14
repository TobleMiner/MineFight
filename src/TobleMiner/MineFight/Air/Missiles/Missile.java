package TobleMiner.MineFight.Air.Missiles;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Air.Airwhatever;
import TobleMiner.MineFight.Air.Targets.Flight;

public class Missile extends Airwhatever
{
	protected Arrow missile;
	protected Location start;
	protected double maxSpeed;
	protected double acceleration;
	protected double speed = 0;
	
	public Missile(Arrow arrow,Location start, int maxHeight, double maxSpeed,double acceleration,Flight flight)
	{
		super();
		this.missile = arrow;
		this.start = start;
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
	}

	public Missile(Arrow arrow,Location start, double maxSpeed,double acceleration)
	{
		super();
		this.missile = arrow;
		this.start = start;
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
	}
	
	
	@Override
	public void update()
	{
		/*try
		{
			Location target = flight.getLocation();
			if(this.missile == null || this.missile.isDead())
			{
				this.cancel();
				return;
			}
			double dist_xz_now = Math.sqrt(Math.pow(target.clone().getX()-missile.getLocation().getX(),2)+Math.pow(target.clone().getZ()-missile.getLocation().getZ(),2));
			double dist_now = target.clone().distance(missile.getLocation().clone());
			double dist_xz_all = Math.sqrt(Math.pow(target.clone().getX()-start.getX(),2)+Math.pow(target.clone().getZ()-start.getZ(),2));
			if(dist_now < 2d)
			{
				missile.getWorld().createExplosion(missile.getLocation(),4.0f);
				flight.cancel();
				this.cancel();
				return;
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
			missile.setVelocity(vec.clone().multiply(fact));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}*/
	}

	@Override
	public Vector getVelocity()
	{
		return this.missile.getVelocity().clone();
	}

	@Override
	public Location getLocation()
	{
		return this.missile.getLocation().clone();
	}
	
	@Override
	public boolean isDead()
	{
		return (this.missile == null ? true : (this.missile.isDead() || this.missile.isOnGround()));
	}
	
	@Override
	public void doCancel()
	{
		super.doCancel();
		this.missile.remove();
	}
	
	public void doCancelKeepEntity()
	{
		super.doCancel();
	}
	
	@Override
	public double getAcceleration()
	{
		return this.acceleration;
	}

	@Override
	public Location getStartpoint()
	{
		return start.clone();
	}
	
}
