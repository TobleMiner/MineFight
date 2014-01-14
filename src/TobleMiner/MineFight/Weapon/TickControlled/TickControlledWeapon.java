package TobleMiner.MineFight.Weapon.TickControlled;

import TobleMiner.MineFight.GameEngine.Match.Match;

public abstract class TickControlledWeapon
{
	protected final Match match;
	
	public TickControlledWeapon(Match match)
	{
		this.match = match;
		this.match.registerTickControlled(this);
	}
	
	public abstract void doUpdate();
	
	protected void unregisterTickControlled()
	{
		this.match.unregisterTickControlled(this);
	}
}
