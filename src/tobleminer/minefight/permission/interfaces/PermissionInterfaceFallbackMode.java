package tobleminer.minefight.permission.interfaces;

import org.bukkit.entity.Player;

import tobleminer.minefight.permission.PermissionInterface;

public class PermissionInterfaceFallbackMode extends PermissionInterface
{

	@Override
	public boolean hasPlayerPermissionTo(Player p, String perm)
	{
		if (p != null)
		{
			return p.isOp();
		}
		return false;
	}

}
