package tobleminer.minefight.permission;

import org.bukkit.entity.Player;

public class PermissionManager
{
	private final PermissionInterface pi;

	public PermissionManager()
	{
		pi = PermissionInterface.getPermissionInterface();
	}

	public boolean hasPlayerPermission(Player p, String perm)
	{
		if (perm.toString() == Permission.MPVP_NONE.toString())
			return true;
		if (p == null || perm == null)
			return false;
		return pi.hasPlayerPermissionTo(p, perm);
	}

	public boolean hasPlayerPermission(Player p, Permission perm)
	{
		if (perm == null)
			return false;
		return this.hasPlayerPermission(p, perm.toString());
	}

}
