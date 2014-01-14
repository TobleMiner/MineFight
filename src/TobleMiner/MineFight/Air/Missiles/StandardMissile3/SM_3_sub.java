package TobleMiner.MineFight.Air.Missiles.StandardMissile3;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;

import TobleMiner.MineFight.Air.Airwhatever;
import TobleMiner.MineFight.Air.Missiles.Missile;

public class SM_3_sub extends Missile
{
	final Arrow missile;
	final Airwhatever target;

	public SM_3_sub(Arrow arrow,Location start, Airwhatever target)
	{
		super(arrow, start, 0d, 0d);
		this.missile = arrow;
		this.target = target;
	}
	
	@Override
	public void update()
	{
		try
		{
			Location target = this.target.getLocation();
			if(this.isDead())
			{
				this.doCancel();
				return;
			}
			double dist_now = target.clone().distance(this.getLocation().clone());
			if(dist_now < 2d)
			{
				this.missile.getWorld().createExplosion(this.getLocation(),1.0f);
				this.target.doCancel();
				this.doCancelKeepEntity();
				return;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
