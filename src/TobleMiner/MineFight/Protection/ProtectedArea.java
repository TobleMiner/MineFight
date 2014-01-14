package TobleMiner.MineFight.Protection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;

public class ProtectedArea 
{
	private final double pos1X;
	private final double pos1Y;
	private final double pos1Z;
	private final double pos2X;
	private final double pos2Y;
	private final double pos2Z;
	private final World world;
	
	public ProtectedArea(Location pos1, Location pos2)
	{
		this.pos1X = pos1.getX();
		this.pos1Y = pos1.getY();
		this.pos1Z = pos1.getZ();
		this.pos2X = pos2.getX();
		this.pos2Y = pos2.getY();
		this.pos2Z = pos2.getZ();
		if(!pos1.getWorld().equals(pos2.getWorld()))
		{
			Error error = new Error("Iternal error!","Protectionregion worldmissmatch!","I've got to find a way, to make this all ok.", this.getClass().getCanonicalName(), ErrorSeverity.WARNING);
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
}
