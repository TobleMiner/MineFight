package TobleMiner.MineFight.Protection;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;

public class Area3D 
{
	private final double pos1X;
	private final double pos1Y;
	private final double pos1Z;
	private final double pos2X;
	private final double pos2Y;
	private final double pos2Z;
	private final World world;
	
	private final Random rand = new Random();
	
	public Area3D(Location pos1, Location pos2)
	{
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
		if(loc.getWorld().equals(this.world))
		{
			return this.isCoordInsideRegion(loc.getX(),loc.getY(),loc.getZ());
		}
		return false;
	}
	
	public boolean isCoordInsideRegion(double x, double y, double z)
	{
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
		double x = pos1X + (pos2X - pos1X) * rand.nextDouble();
		double y = pos1Y + (pos2Y - pos1Y) * rand.nextDouble();
		double z = pos1Z + (pos2Z - pos1Z) * rand.nextDouble();
		return new Location(this.world, x, y, z);
	}
	
	@Override
	public String toString()
	{
		return String.format("World: '%s' pos1: [%d, %d, %d] pos2: [%d, %d, %d]", this.world.getName(), (int)this.pos1X, (int)this.pos1Y, (int)this.pos1Z, (int)this.pos2X, (int)this.pos2Y, (int)this.pos2Z);
	}
}
