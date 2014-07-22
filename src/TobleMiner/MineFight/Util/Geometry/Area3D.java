package TobleMiner.MineFight.Util.Geometry;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;

public class Area3D 
{
	private double pos1X;
	private double pos1Y;
	private double pos1Z;
	private double pos2X;
	private double pos2Y;
	private double pos2Z;
	private World world;
	private final Entity entity;
	private final Vector vec1;
	private final Vector vec2;
	
	private final Random rand = new Random();
	
	protected Area3D(Area3D area)
	{
		if(area.entity != null)
		{
			this.entity = area.entity;
			this.vec1 = area.vec1;
			this.vec2 = area.vec2;
		}
		else
		{
			this.entity = null;
			this.vec1 = null;
			this.vec2 = null;
			this.pos1X = area.pos1X;
			this.pos1Y = area.pos1Y;
			this.pos1Z = area.pos1Z;
			this.pos2X = area.pos2X;
			this.pos2Y = area.pos2Y;
			this.pos2Z = area.pos2Z;
		}
	}
	
	public Area3D(Entity ent, Vector vec1, Vector vec2)
	{
		this.entity = ent;
		this.vec1 = vec1;
		this.vec2 = vec2;
	}
	
	public Area3D(Location pos1, Location pos2)
	{
		this.entity = null;
		this.vec1 = null;
		this.vec2 = null;
		this.pos1X = pos1.getX();
		this.pos1Y = pos1.getY();
		this.pos1Z = pos1.getZ();
		this.pos2X = pos2.getX();
		this.pos2Y = pos2.getY();
		this.pos2Z = pos2.getZ();
		if(!pos1.getWorld().equals(pos2.getWorld()))
		{
			Error error = new Error("Internal error!","Area3D world missmatch!","I've got to find a way, to make this all ok.", this.getClass().getCanonicalName(), ErrorSeverity.WARNING);
			ErrorReporter.reportError(error);
		}
		this.world = pos1.getWorld();
	}
	
	public boolean isBlockInsideRegion(Block b)
	{
		return isCoordInsideRegion(b.getLocation());
	}
	
	public boolean isCoordInsideRegion(Location loc)
	{
		World world = this.world;
		if(this.entity != null)
		{
			world = this.entity.getWorld();
		}
		if(loc.getWorld().equals(world))
		{
			return this.isCoordInsideRegion(loc.getX(),loc.getY(),loc.getZ());
		}
		return false;
	}
	
	public boolean isCoordInsideRegion(double x, double y, double z)
	{
		double pos1X = this.pos1X;
		double pos1Y = this.pos1Y;
		double pos1Z = this.pos1Z;
		double pos2X = this.pos2X;
		double pos2Y = this.pos2Y;
		double pos2Z = this.pos2Z;
		if(this.entity != null)
		{
			Location entBound1 = this.entity.getLocation().clone().add(this.vec1);
			pos1X = entBound1.getX();
			pos1Y = entBound1.getY();
			pos1Z = entBound1.getZ();
			Location entBound2 = this.entity.getLocation().clone().add(this.vec2);
			pos2X = entBound2.getX();
			pos2Y = entBound2.getY();
			pos2Z = entBound2.getZ();
		}
		if((x <= pos1X && x >= pos2X) || (x <= pos2X && x >= pos1X))
		{
			if((y <= pos1Y && y >= pos2Y) || (y <= pos2Y && y >= pos1Y))
			{
				if((z <= pos1Z && z >= pos2Z) || (z <= pos2Z && z >= pos1Z))
				{
					return true;
				}				
			}			
		}
		return false;
	}
	
	public Location pickRandomPoint()
	{
		double pos1X = this.pos1X;
		double pos1Y = this.pos1Y;
		double pos1Z = this.pos1Z;
		double pos2X = this.pos2X;
		double pos2Y = this.pos2Y;
		double pos2Z = this.pos2Z;
		World world = this.world;
		if(this.entity != null)
		{
			Location entBound1 = this.entity.getLocation().clone().add(this.vec1);
			pos1X = entBound1.getX();
			pos1Y = entBound1.getY();
			pos1Z = entBound1.getZ();
			Location entBound2 = this.entity.getLocation().clone().add(this.vec2);
			pos2X = entBound2.getX();
			pos2Y = entBound2.getY();
			pos2Z = entBound2.getZ();
			world = this.entity.getWorld();
		}
		double x = pos1X + (pos2X - pos1X) * rand.nextDouble();
		double y = pos1Y + (pos2Y - pos1Y) * rand.nextDouble();
		double z = pos1Z + (pos2Z - pos1Z) * rand.nextDouble();
		return new Location(world, x, y, z);
	}
	
	@Override
	public String toString()
	{
		double pos1X = this.pos1X;
		double pos1Y = this.pos1Y;
		double pos1Z = this.pos1Z;
		double pos2X = this.pos2X;
		double pos2Y = this.pos2Y;
		double pos2Z = this.pos2Z;
		World world = this.world;
		if(this.entity != null)
		{
			Location entBound1 = this.entity.getLocation().clone().add(this.vec1);
			pos1X = entBound1.getX();
			pos1Y = entBound1.getY();
			pos1Z = entBound1.getZ();
			Location entBound2 = this.entity.getLocation().clone().add(this.vec2);
			pos2X = entBound2.getX();
			pos2Y = entBound2.getY();
			pos2Z = entBound2.getZ();
			world = this.entity.getWorld();
		}
		return String.format("World: '%s' pos1: [%d, %d, %d] pos2: [%d, %d, %d]", world.getName(), (int)pos1X, (int)pos1Y, (int)pos1Z, (int)pos2X, (int)pos2Y, (int)pos2Z);
	}
}
