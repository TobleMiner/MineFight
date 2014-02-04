package TobleMiner.MineFight.Weapon.TickControlled.Missile;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Weapon.TickControlled.TickControlledWeapon;

public abstract class Missile extends TickControlledWeapon
{
	protected final PVPPlayer shooter;
	
	public Missile(Match m, PVPPlayer shooter)
	{
		super(m);
		this.shooter = shooter;
	}
	
	protected boolean canTarget(PVPPlayer target)
	{
		return (this.shooter.getTeam() != target.getTeam() && target.isSpawned());
	}
}
