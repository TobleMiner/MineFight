package TobleMiner.MineFight.Air.Targets.Debugging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;

public class Swarm 
{
	private final String name;
	private final List<Location> waypoints; 
	private List<IntelegentArrow> entities;
	private final int swarmSize;
	private Location currentWaypoint;
	private final Main mane;
	public Swarm(Main mane,int size,String name)
	{
		this.mane = mane;
		this.name = name;
		this.swarmSize = size;
		this.waypoints = new ArrayList<Location>();
		this.entities = new ArrayList<IntelegentArrow>();
	}

	public Swarm(Main mane,int size,String name,List<Location> waypoints)
	{
		this.mane = mane;
		this.name = name;
		this.swarmSize = size;
		this.waypoints = waypoints;
		this.entities = new ArrayList<IntelegentArrow>();
	}
	
	public void kill()
	{
		if(entities == null) return;
		for(IntelegentArrow entity : entities)
		{
			entity.doCancel();
		}
	}
	
	public List<IntelegentArrow> getEntities()
	{
		return this.entities;
	}
	
	public void nextWaypoint(Location current)
	{
		if(currentWaypoint.equals(current))
		{
			//this.currentWaypoint = this.waypoints.get(this.waypoints.indexOf(this.currentWaypoint)); //Not threadsafe
		}
	}
	
	public void spawn(World world,double randomSpreadX,double randomSpreadY,double randomSpreadZ)
	{
		System.out.println("Fetching waypoint-size...");
		if(this.waypoints.size() < 2)
		{
			return;
		}
		currentWaypoint = this.waypoints.get(1);
		Location spawnpoint = this.waypoints.get(0);
		System.out.println("First waypoint set...");
		Vector baseSpawnVec = new Vector(0, 1, 0);
		Vector maxSpreadVec = new Vector(randomSpreadX,randomSpreadY,randomSpreadZ);
		Random rand = new Random();
		for(int i=0;i<swarmSize;i++)
		{
			Arrow ar = world.spawnArrow(spawnpoint.clone().add(new Location(world,rand.nextDouble()*randomSpreadX, rand.nextDouble()*randomSpreadY, rand.nextDouble()*randomSpreadZ)),baseSpawnVec.clone(),3.0f,1.0f);
			world.playEffect(spawnpoint,Effect.ENDER_SIGNAL,0);
			IntelegentArrow arr = new IntelegentArrow(mane, ar, this, currentWaypoint.clone(), maxSpreadVec);
			this.entities.add(arr);
		}
		for(IntelegentArrow entity : entities)
		{
			entity.start();
		}
	}
	
	public void addWaypoint(Location loc)
	{
		this.waypoints.add(loc);
	}
	
	public String getName()
	{
		return this.name;
	}
}
