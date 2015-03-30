package tobleminer.minefight.util.location;

import org.bukkit.util.Vector;

public class FacingUtil 
{	
	public static Vector getOffsetByFacing(int facing)
	{
		switch(facing)
		{
			case 0: return new Vector(0d, 0d, 1d);
			case 1: return new Vector(-1d, 0d, 0d);
			case 2: return new Vector(0d, 0d, -1d);
			case 3: return new Vector(1d, 0d, 0d);
		}
		return null;
	}	
}
