package TobleMiner.MineFight.Weapon.Stationary;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.TickControlled.SentryMissile;

public class SentryGun 
{
	public final Dispenser dispenser;
	private final PVPPlayer owner;
	private final float arrowSpeed;
	private final Match match;
	private final double missileSpeed;
	private final float missileExploStr;
	
	public SentryGun(Match match, Dispenser disp,PVPPlayer owner,float arrowSpeed, double missileSpeed, float missileExploStr)
	{
		this.dispenser = disp;
		this.owner = owner;
		this.arrowSpeed = arrowSpeed;
		this.match = match;
		this.missileSpeed = missileSpeed;
		this.missileExploStr = missileExploStr;
	}		
	
	public Arrow shoot(Location target)
	{
		if(!dispenser.getBlock().getType().equals(Material.DISPENSER))
		{
			return null;
		}
		Inventory inv = dispenser.getInventory();
		if(inv.contains(Material.ARROW))
		{
			Vector locHelp = target.clone().subtract(this.dispenser.getLocation()).toVector();
			Location shootLoc = this.dispenser.getLocation().clone().add(locHelp.multiply(2.0d/locHelp.length()));
			Vector dir = target.clone().subtract(shootLoc.clone()).toVector();
			Vector vel = dir.clone().multiply(arrowSpeed/dir.length());
			Arrow arr = dispenser.getWorld().spawnArrow(shootLoc, vel,arrowSpeed,1.0F);
			arr.setVelocity(vel);
			inv.removeItem(new ItemStack(Material.ARROW,1));
			return arr;
		}
		return null;
	}
	
	public SentryMissile shootMissile(Location target)
	{
		if(!dispenser.getBlock().getType().equals(Material.DISPENSER))
		{
			return null;
		}
		Inventory inv = dispenser.getInventory();
		if(inv.contains(Material.SULPHUR))
		{
			Vector locHelp = target.clone().subtract(this.dispenser.getLocation()).toVector();
			Location shootLoc = this.dispenser.getLocation().clone().add(locHelp.multiply(2.0d/locHelp.length()));
			Vector dir = target.clone().subtract(shootLoc.clone()).toVector();
			Vector vel = dir.clone().multiply(arrowSpeed/dir.length());
			Arrow arr = dispenser.getWorld().spawnArrow(shootLoc, vel,arrowSpeed,1.0F);
			SentryMissile missile = new SentryMissile(match, dir, this, missileExploStr, arr, missileSpeed);
			inv.removeItem(new ItemStack(Material.SULPHUR,1));
			return missile;
		}
		return null;
	}
	
	public PVPPlayer getOwner()
	{
		return this.owner;
	}
}
