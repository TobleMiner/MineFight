package TobleMiner.MineFight.Air.Targets.Debugging;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Air.Airwhatever;

public class IntelegentArrow extends Airwhatever
{
	public final Arrow arr;
	private final Swarm swarm;
	private Location target;
	private Location lastTarget;
	private int maxHeight = 20;
	private double maxSpeed = 1.2d;
	private double speed = 0d;
	private double acceleration = 1d;
	private Vector targetVelocity;
	private Vector currentVelocity;
	private double maxVelocityChange = 0.4d*maxSpeed;
	private final double maxDistance;
	private double arrowSpacing = 3d;
	private boolean active = false;
	
	public IntelegentArrow(Main mane,Arrow arr,Swarm swarm,Location target,Vector spreadVec)
	{
		this.arr = arr;
		this.swarm = swarm;
		this.target = target;
		this.lastTarget = arr.getLocation();
		this.targetVelocity = arr.getVelocity();
		this.currentVelocity = arr.getVelocity();
		this.maxDistance = spreadVec.length()*2d;
	}
	
	public void start()
	{
		active = true;
	}
	
	public void setTarget(Location target)
	{
		this.lastTarget = this.target;
		this.target = target;
	}
		
	private void changeVelocity()
	{
		double currentX = currentVelocity.getX();
		double currentY = currentVelocity.getY();
		double currentZ = currentVelocity.getZ();
		double targetX = targetVelocity.getX();
		double targetY = targetVelocity.getY();
		double targetZ = targetVelocity.getZ();
		if(Math.abs(targetX-currentX) > maxVelocityChange)
		{
			currentX += (currentX < targetX ? maxVelocityChange : -maxVelocityChange);
		}
		else
		{
			currentX = targetX;
		}
		if(Math.abs(targetY-currentY) > maxVelocityChange)
		{
			currentY += (currentY < targetY ? maxVelocityChange : -maxVelocityChange);
		}
		else
		{
			currentY = targetY;
		}
		if(Math.abs(targetZ-currentZ) > maxVelocityChange)
		{
			currentZ += (currentZ < targetZ ? maxVelocityChange : -maxVelocityChange);
		}
		else
		{
			currentZ = targetZ;
		}
		this.currentVelocity = new Vector(currentX,currentY,currentZ);
		this.arr.setVelocity(currentVelocity.clone());
	}
	
	public void setVelocity(Vector vec)
	{
		this.targetVelocity = vec.clone();
	}

	@Override
	public void update()
	{
		if(!active)
		{
			return;
		}
		try
		{
			Location target = this.target.clone();
			if(this.isDead())
			{
				this.doCancel();
				return;
			}
			double dist_xz_now = Math.sqrt(Math.pow(target.clone().getX()-this.getLocation().getX(),2)+Math.pow(target.clone().getZ()-this.getLocation().getZ(),2));
			double dist_now = target.clone().distance(arr.getLocation().clone());
			double dist_xz_all = Math.sqrt(Math.pow(target.clone().getX()-lastTarget.getX(),2)+Math.pow(target.clone().getZ()-lastTarget.getZ(),2));
			if(dist_now < 2d)
			{
				swarm.nextWaypoint(this.target);
				//this.arr.remove();
				this.deactivate();
				return;
			}
			double height_now_desired_rel = ((double)maxHeight) * Math.sin(dist_xz_now/dist_xz_all*Math.PI);
			double height_now_desired = height_now_desired_rel + target.getY();
			Location loc_desired = new Location(arr.getWorld(),target.getX(),height_now_desired,target.getZ());
			this.speed += acceleration/10d;
			if(speed > maxSpeed)
			{
				speed = maxSpeed;
			}
			Location loc = loc_desired.clone().subtract(arr.getLocation().clone());
			Vector vec = loc.toVector();
			List<IntelegentArrow> entities = swarm.getEntities();
			Vector averageDir = new Vector(0d,0d,0d);
			Location averageLoc = this.getLocation().clone();
			IntelegentArrow nearest = null;
			double smallestDist = Double.MAX_VALUE;
			double averageDist = 0d;
			double maxX = Double.MIN_VALUE;
			double maxY = Double.MIN_VALUE;
			double maxZ = Double.MIN_VALUE;
			double minX = Double.MAX_VALUE;
			double minY = Double.MAX_VALUE;
			double minZ = Double.MAX_VALUE;
			double localSpeed = speed;
			int skippedEntities = 0;
			for(IntelegentArrow entity : entities)
			{
				if(entity == this || entity.isDead())
				{
					skippedEntities++;
					continue;
				}
				Location pos = entity.getLocation();
				maxX = Math.max(maxX,pos.getX());
				maxY = Math.max(maxY,pos.getY());
				maxZ = Math.max(maxZ,pos.getZ());
				minX = Math.min(minX,pos.getX());
				minY = Math.min(minY,pos.getY());
				minZ = Math.min(minZ,pos.getZ());
				double dist = entity.getLocation().clone().distance(this.getLocation().clone());
				if(dist < smallestDist)
				{
					smallestDist = dist;
					nearest = entity;
				}
				averageDir = averageDir.clone().add(entity.getVelocity().clone().multiply(1d/dist));
				averageDist += dist;
				averageLoc = averageLoc.clone().add(entity.getLocation().clone());
			}
			Bukkit.getServer().broadcastMessage("SkippedEnt: "+skippedEntities);
			averageLoc = averageLoc.clone().multiply(1d/((double)entities.size()));
			Location loc_current = this.getLocation().clone();
			if(loc_current.clone().distance(averageLoc.clone()) > this.maxDistance)
			{
				this.doCancel();
				return;
			}
			double deltaX = Math.abs(Math.abs(maxX-minX));
			double deltaY = Math.abs(Math.abs(maxY-minY));
			double deltaZ = Math.abs(Math.abs(maxZ-minZ));
			//Vector volumeVect = new Vector(deltaX,deltaY,deltaZ);
			double volumeIs = deltaX * deltaY * deltaZ;
			double volumeNeeded = ((double)(entities.size()-skippedEntities))*Math.pow(arrowSpacing,3)*2;
			double deltaV = volumeIs - volumeNeeded;
			Bukkit.getServer().broadcastMessage("DeltaV: "+Double.toString(deltaV));
			double factX = (averageLoc.getX()-loc_current.getX())/this.currentVelocity.clone().getX();
			if(deltaV < 0)
			{
				Vector vector = currentVelocity.clone().multiply(factX);
				if(factX < 0)
				{
					localSpeed = speed*1.2d*vector.length()/arrowSpacing; //TODO
				}
				else
				{
					localSpeed = speed*0.8d*vector.length()/arrowSpacing; //TODO
				}
			}
			else
			{
				/*Vector vector = currentVelocity.clone().multiply(factX);
				if(factX < 0)
				{
					localSpeed = speed*0.8d*vector.length()/arrowSpacing; //TODO
				}
				else
				{
					localSpeed = speed*1.2d*vector.length()/arrowSpacing; //TODO
				}*/
			}
			averageDir = averageDir.clone().multiply(1d/((double)(entities.size()-skippedEntities)));
			averageDist *= (1/((double)(entities.size()-skippedEntities)));
			//Bukkit.getServer().broadcastMessage("AvgDist: "+averageDist);
			//Bukkit.getServer().broadcastMessage("AvgPos: "+averageLoc.getBlockX()+" "+averageLoc.getBlockY()+" "+averageLoc.getBlockZ());
			//Bukkit.getServer().broadcastMessage("posThis: "+this.arr.getLocation().getBlockX()+" "+this.arr.getLocation().getBlockY()+" "+this.arr.getLocation().getBlockZ());
			//Bukkit.getServer().broadcastMessage("posTarget: "+target.getBlockX()+" "+target.getBlockY()+" "+target.getBlockZ());
			Vector vec_part = averageDir.clone();
			if(nearest != null)
			{
				vec_part = vec_part.clone().add(new Vector(nearest.getLocation().getX()-loc_current.getX() < 0 ? 1d/smallestDist : -1/smallestDist,nearest.getLocation().getY()-loc_current.getY() < 0 ? 1/smallestDist : -1/smallestDist,nearest.getLocation().getZ()-loc_current.getZ() < 0 ? 1/smallestDist : -1/smallestDist).multiply(arrowSpacing/smallestDist*(dist_now/lastTarget.clone().distance(target.clone()))));
			}
			vec_part = vec_part.clone().add(averageLoc.clone().subtract(this.getLocation().clone()).toVector().multiply(averageDist/15d));
			vec = vec.clone().multiply(1/(dist_now/lastTarget.clone().distance(target.clone()))).add(vec_part.clone());
			if(averageDist > 10)
			{
				//localSpeed *= averageDist/10d;
			}
			/*if(averageDist < 7)
			{
				vec = averageDir.multiply(-1d);
				Bukkit.getServer().broadcastMessage("Avoiding flockmate...");
			}
			else
			{
				if(averageDist > 20)
				{
					vec = averageLoc.clone().subtract(this.arr.getLocation().clone()).toVector();
					Bukkit.getServer().broadcastMessage("Finding way back to flock...");
				}
			}*/
			double vec_lenght = vec.length();
			double fact = vec_lenght == 0 ? 1d : Math.abs(localSpeed/vec_lenght);
			setVelocity(vec.clone().multiply(fact));
			changeVelocity();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}

	@Override
	public Vector getVelocity()
	{
		return arr.getVelocity().clone();
	}

	@Override
	public Location getLocation()
	{
		return arr.getLocation().clone();
	}

	@Override
	public boolean isDead()
	{
		return (this.arr == null ? true : this.arr.isDead() || this.arr.isOnGround());
	}

	@Override
	public double getAcceleration()
	{
		return acceleration;
	}

	@Override
	public Location getStartpoint()
	{
		return lastTarget.clone();
	}
	
	@Override
	public void doCancel()
	{
		super.doCancel();
		this.arr.remove();
	}
	
	public void deactivate()
	{
		super.doCancel();
	}
}
