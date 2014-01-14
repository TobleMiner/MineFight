package TobleMiner.MineFight.Weapon.Projectile;

import org.bukkit.entity.Arrow;

import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

public class SimpleProjectile 
{
	public final PVPPlayer shooter;
	public ProjectileType type;
	public final boolean isCritical;
	public final double dmgMul;
	public final Arrow proj;
	
	public SimpleProjectile(PVPPlayer p, ProjectileType type, boolean crit, double mul, Arrow arr)
	{
		this.shooter = p;
		this.type = type;
		this.isCritical = crit;
		this.dmgMul = mul;
		this.proj = arr;
	}
}