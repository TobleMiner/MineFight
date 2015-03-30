package tobleminer.minefight.weapon.projectile;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;

import tobleminer.minefight.config.weapon.WeaponDescriptor;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.engine.player.PVPPlayer.HitZone;

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
