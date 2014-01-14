package TobleMiner.MineFight.GameEngine.Player;

public class Killhelper 
{
	public final PVPPlayer damager;
	private double damage = 0d;
	
	public Killhelper(PVPPlayer damager) 
	{
		this.damager = damager;
	}
	
	public void addDamage(double damage)
	{
		this.damage += damage;
	}
	
	public double getDamage()
	{
		return this.damage;
	}
}
