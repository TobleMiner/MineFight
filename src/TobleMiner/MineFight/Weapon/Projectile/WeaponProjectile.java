package TobleMiner.MineFight.Weapon.Projectile;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;

import TobleMiner.MineFight.Configuration.Weapon.WeaponDescriptor;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer.HitZone;

public class WeaponProjectile extends Projectile
{

	private final WeaponDescriptor weapon;
	
	public WeaponProjectile(PVPPlayer p, Arrow arr, WeaponDescriptor wd, boolean crit)
	{
		super(p, arr, crit, wd.getName());
		this.weapon = wd;
	}
	
	@Override
	public double getDmg(double dmg, HitZone hz, Location loc)
	{
		return this.weapon.getDamage(this.getFlightDistance(loc)) * this.weapon.multipliers.get(hz);
	}

}
