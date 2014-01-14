package TobleMiner.MineFight.Permissions.Interfaces;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.Permissions.PermissionInterface;

public class PermissionInterfaceVault extends PermissionInterface
{

	private final Permission vaultPermInterface;
	
	public PermissionInterfaceVault()
	{
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if(permissionProvider != null)
        {
        	vaultPermInterface = permissionProvider.getProvider();
        }
        else
        {
        	vaultPermInterface = null;
        	Error err = new Error("Permission setup error!","Vault permissions could NOT be set up correctly!","MineFight will fail as long as vault isn't fixed!",this.getClass().getName(),ErrorSeverity.ETERNALCHAOS);
        	ErrorReporter.reportError(err);
        }
	}
	
	@Override
	public boolean hasPlayerPermissionTo(Player p, String perm)
	{
		if(vaultPermInterface != null)
		{
			return vaultPermInterface.has(p, perm);
		}
		else
		{
			return false;
		}
	}
}
