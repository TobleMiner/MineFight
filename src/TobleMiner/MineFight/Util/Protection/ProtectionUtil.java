package TobleMiner.MineFight.Util.Protection;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Protection.Area3D;

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
