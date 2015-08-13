package tobleminer.minefight.util.material;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BlockUtil
{
	private List<Vector> igniteVects = new ArrayList<Vector>();

	public BlockUtil()
	{
		this.igniteVects.add(new Vector(0d, 1d, 0d));
		this.igniteVects.add(new Vector(1d, 0d, 0d));
		this.igniteVects.add(new Vector(0d, 0d, -1d));
		this.igniteVects.add(new Vector(-1d, 0d, 0d));
		this.igniteVects.add(new Vector(0d, 0d, 1d));
	}

	public boolean ignite(Block b)
	{
		for (Vector vect : this.igniteVects)
		{
			Location loc = b.getLocation().clone().add(vect);
			if (loc.getBlock() != null)
			{
				if (loc.getBlock().getType().equals(Material.AIR))
				{
					loc.getBlock().setType(Material.FIRE);
					return true;
				}
			}
		}
		return false;
	}
}
