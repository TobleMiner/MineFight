package TobleMiner.MineFight.Air;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class Airwhatever
{
	private static List<Airwhatever> airObjects = new ArrayList<Airwhatever>();
	
	public Airwhatever()
	{
		Airwhatever.registerAirwhatever(this);
	}
	
	public static List<Airwhatever> getNearAirwhatevers(Airwhatever self,double x, double y, double z)
	{
		List<Airwhatever> nearAirwhatevers = new ArrayList<Airwhatever>();
		if(airObjects.size() > 0)
		{
			for(int i=0;i<airObjects.size();i++)
			{
				Airwhatever airwhatever = airObjects.get(i);
	 			if(!airwhatever.isDead())
				{
					if(airwhatever != self && Math.abs(airwhatever.getLocation().getX()-self.getLocation().getX()) <= x && Math.abs(airwhatever.getLocation().getY()-self.getLocation().getY()) <= y && Math.abs(airwhatever.getLocation().getZ()-self.getLocation().getZ()) <= z)
					{
						nearAirwhatevers.add(airwhatever);
					}
				}
			}
		}
		return nearAirwhatevers;
	}
	
	public static void registerAirwhatever(Airwhatever obj)
	{
		airObjects.add(obj);
	}
	
	public static void unregisterAirwhatever(Airwhatever obj)
	{
		airObjects.remove(obj);
	}

	public boolean isRegistered()
	{
		return airObjects.contains(this);
	}
	
	public abstract Vector getVelocity();
	
	public abstract Location getLocation();
	
	public abstract boolean isDead();
	
	public abstract void update();
	
	public static void doUpdate()
	{
		try
		{
			if(airObjects.size() > 0)
			{
				for(int i=0;i<airObjects.size();i++)
				{
					Airwhatever obj = airObjects.get(i);
					obj.update();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void doCancel()
	{
		Airwhatever.unregisterAirwhatever(this);
	}
	
	public abstract double getAcceleration();
	
	public abstract Location getStartpoint();
}
