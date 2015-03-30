package tobleminer.minefight.weapon.tickcontrolled;

import tobleminer.minefight.engine.match.Match;

public abstract class TickControlledWeapon
{
	public final Match match;
	
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
