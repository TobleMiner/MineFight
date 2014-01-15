package TobleMiner.MineFight.Util.Protection;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Protection.ProtectedArea;

public class ProtectionUtil 
{
	public boolean isLocProtected(Location loc)
	{
		List<ProtectedArea> lpa = Main.gameEngine.configuration.getProtectedAreasByWorld(loc.getWorld());
		if(lpa != null)
		{
			for(ProtectedArea pa : lpa)
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
