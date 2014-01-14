package TobleMiner.MineFight.Air.Missiles.Tomahawk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Air.Airwhatever;
import TobleMiner.MineFight.Air.Missiles.Missile;

public class Tomahawk extends Missile
{
	private Entity target_ent = null;
	private Location target_loc = null;
	private Airwhatever target_air = null;
	final TomahawkMode mode;
	
	public Tomahawk(Arrow arrow, Location start,double maxSpeed, double acceleration, Location target, TomahawkMode mode)
	{
		super(arrow, start, maxSpeed, acceleration);
		this.target_loc = target;
		this.mode = mode;
	}
	
	public Tomahawk(Arrow arrow, Location start, double maxSpeed, double acceleration, Entity target, TomahawkMode mode)
	{
		super(arrow, start, maxSpeed, acceleration);
		this.target_ent = target;
		this.mode = mode;
	}

	
	public Tomahawk(Arrow arrow, Location start, double maxSpeed, double acceleration, Airwhatever target, TomahawkMode mode)
	{
		super(arrow, start, maxSpeed, acceleration);
		this.target_air = target;
		this.mode = mode;
	}

	private Block raytrace(List<Material> ignoreList)
	{
		Location pos = this.getLocation();
		Vector vel = this.getVelocity();
		for(int i=0;i<50;i++)
		{
			double fact = ((double)i)/10d;
			Vector vec = pos.clone().toVector().add(vel.clone().multiply(fact));
			Block next = pos.getWorld().getBlockAt(vec.getBlockX(),vec.getBlockY(),vec.getBlockZ());
			if(ignoreList == null || ignoreList.size() == 0)
			{
				return next;
			}
			else
			{
				if(!ignoreList.contains(next.getType()))
				{
					return next;
				}
			}
		}
		return null;
	}
	
	private double getHeightAboveGround()
	{
		Location loc = this.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		double height = (double)y;
		int z = loc.getBlockZ();
		for(int i=0;i<=y;i++)
		{
			Block b = loc.getWorld().getBlockAt(x, y-i, z);
			if(!b.getType().equals(Material.AIR))
			{
				height = (double)i;
				break;
			}
		}
		return height;
	}

	@Override
	public void update()
	{
		if(this.isDead())
		{
			this.doCancel();
			return;
		}
		//System.out.println("Tomahawk Loop 1");
		Location temploc = null;
		if(target_loc != null)
		{
			temploc = target_loc.clone();
		}
		else if(target_ent != null)
		{
			temploc = target_ent.getLocation();
		}
		else if(target_air != null)
		{
			if(target_air.isDead())
			{
				this.doCancel();
				return;
			}
			temploc = target_air.getLocation();
		}
		//System.out.println("Tomahawk Loop 2");
		try
		{
			List<Material> ignoreMats = new ArrayList<Material>();
			ignoreMats.add(Material.AIR);
			double dist_xz_now = Math.sqrt(Math.pow(temploc.clone().getX()-missile.getLocation().getX(),2)+Math.pow(temploc.clone().getZ()-missile.getLocation().getZ(),2));
			double dist_now = temploc.clone().distance(missile.getLocation().clone());
			double dist_xz_all = Math.sqrt(Math.pow(temploc.clone().getX()-start.getX(),2)+Math.pow(temploc.clone().getZ()-start.getZ(),2));
			//System.out.println("Tomahawk Loop 3");
			if(dist_now < 2d)
			{
				missile.getWorld().createExplosion(missile.getLocation(),4.0f);
				if(target_ent != null)
				{
					target_ent.remove();
				}
				else if(target_air != null)
				{
					target_air.doCancel();
				}
				this.doCancel();
				return;
			}
			double height_now_desired_rel = ((double)2) * Math.sin(dist_xz_now/dist_xz_all*Math.PI);
			double height_now_desired = height_now_desired_rel + temploc.getY();
			if(mode.equals(TomahawkMode.GroundProfile))
			{
				double heightAboveGround = this.getHeightAboveGround();
				height_now_desired = (this.getLocation().getY()-heightAboveGround+5d) * ((dist_xz_now/dist_xz_all)) + (temploc.getY()*(1-dist_xz_now/dist_xz_all));
				/*if(heightAboveGround > 7)
				{
					double angle= heightAboveGround/8d*(-90d);
					if(angle < (-90d))
					{
						angle = -90d;
					}
					vel = this.setAngleY(vel.clone(),angle,true);
				}*/		
			}
			else if(mode.equals(TomahawkMode.HeightProfile))
			{
				height_now_desired = (200d) * ((dist_xz_now/dist_xz_all)) + (temploc.getY()*(1-dist_xz_now/dist_xz_all));				
			}
			Location loc_desired = new Location(missile.getWorld(),temploc.getX(),height_now_desired,temploc.getZ());
			//System.out.println("Tomahawk Loop 4");
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
			//System.out.println("Tomahawk Loop 5");
			if(mode.equals(TomahawkMode.GroundProfile))
			{
				Block collider = this.raytrace(ignoreMats);
				boolean avoidance = false;
				if(collider != null)
				{
					double dist = Math.abs(collider.getLocation().clone().distance(this.getLocation()));
					if(dist < 8.0d)
					{
						avoidance = true;
						double angle= 8d/dist*90d;
						vel = this.setAngleY(vel.clone(),angle,false);
						System.out.println("Obstacle detected!");
					}
				}
				if(!avoidance || true)
				{
				}
			}
			//System.out.println("Tomahawk Loop 6");
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
			//System.out.println("Tomahawk Loop 7");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			this.doCancel();
		}
		
	}
	
	public Vector setAngleY(Vector vel,double angY,boolean keepXZ)
	{
		double rad = angY/180d*Math.PI;
		vel = vel.clone().setY(vel.length()*Math.sin(rad));
		if(!keepXZ)
		{
			vel = new Vector(0d, vel.getY(), 0d);
		}
		return vel;
	}
	
}
