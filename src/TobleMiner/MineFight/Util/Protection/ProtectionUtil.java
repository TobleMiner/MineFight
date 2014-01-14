package TobleMiner.MineFight.Util.Protection;

import java.util.List;

import org.bukkit.block.Block;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Protection.ProtectedArea;

public class ProtectionUtil 
{
	public boolean isBlockProtected(Block b)
	{
		List<ProtectedArea> lpa = Main.gameEngine.configuration.protectionRegions.get(b.getWorld());
		if(lpa != null)
		{
			for(ProtectedArea pa : lpa)
			{
				if(pa.isBlockInsideRegion(b))
				{
					return true;
				}
			}
		}
		return false;
	}
}
