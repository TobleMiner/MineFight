package tobleminer.minefight.permission.interfaces;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import tobleminer.minefight.permission.PermissionInterface;

public class PermissionInterfacePEx extends PermissionInterface
{
	private final PermissionManager pm;

	public PermissionInterfacePEx(PermissionsEx p)
	{
		pm = p.getPermissionsManager();
	}

	@Override
	public boolean hasPlayerPermissionTo(Player p, String perm)
	{
		return pm.has(p, perm);
	}

}
