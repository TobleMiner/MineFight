package tobleminer.minefight.util.protection;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import tobleminer.minefight.Main;
import tobleminer.minefight.util.geometry.Area3D;

public class ProtectionUtil 
{
	public boolean isLocProtected(Location loc)
	{
		List<Area3D> lpa = Main.gameEngine.configuration.getProtectedAreasByWorld(loc.getWorld());
		if(lpa != null)
		{
			for(Area3D pa : lpa)
			{
				if(pa.isCoordInsideRegion(loc))
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean isBlockProtected(Block b)
	{
		return this.isLocProtected(b.getLocation());
	}
	
}
