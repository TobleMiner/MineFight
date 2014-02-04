package TobleMiner.MineFight.Weapon.TickControlled.Missile;

import org.bukkit.entity.Arrow;

import TobleMiner.MineFight.Configuration.Config;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

public class PlayerSeeker extends Missile
{
	private final Arrow arr;
	private PVPPlayer target;
	
	public PlayerSeeker(Match m, Arrow arr, PVPPlayer shooter, PVPPlayer target, Config cfg)
	{
		super(m, shooter);
		this.arr = arr;
		this.target = target;
	}

	@Override
	public void doUpdate() 
	{
		
	}
}
