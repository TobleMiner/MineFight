package TobleMiner.MineFight.Permissions.Interfaces;

import org.bukkit.entity.Player;

import TobleMiner.MineFight.Permissions.PermissionInterface;

public class PermissionInterfaceFallbackMode extends PermissionInterface
{

	@Override
	public boolean hasPlayerPermissionTo(Player p, String perm)
	{
		if(p != null)
		{
			return p.isOp();
		}
		return false;
	}

}
