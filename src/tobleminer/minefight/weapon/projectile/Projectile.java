package tobleminer.minefight.weapon.projectile;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;

import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.engine.player.PVPPlayer.HitZone;

public abstract class Projectile
{
	public final PVPPlayer	shooter;
	public final Arrow		proj;
	private final Location	startLoc;
	public final boolean	isCritical;
	private final String	wpName;

	public Projectile(PVPPlayer p, Arrow arr, boolean crit, String wpName)
	{
		this.shooter = p;
		this.proj = arr;
		this.isCritical = crit;
		this.startLoc = arr.getLocation().clone();
		this.wpName = wpName;
	}

	public double getFlightDistance(Location loc)
	{
		return this.startLoc.distance(loc);
	}

	public String getWpName()
	{
		return this.wpName;
	}

	public abstract double getDmg(double base, HitZone hz, Location loc);
}
