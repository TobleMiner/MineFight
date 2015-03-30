package tobleminer.minefight.util.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Line3D 
{
	private final Location pos1; //Defined by two positions on the line
	private final Location pos2;
	
	public Line3D(Location loc, Vector vec)
	{
		this.pos1 = loc.clone();
		this.pos2 = loc.clone().clone().add(vec);
	}
	
	public Line3D(Location pos1, Location pos2)
	{
		this.pos1 = pos1.clone();
		this.pos2 = pos2.clone();
	}
	
	public double getSmallestDist(Location loc)
	{
		return this.getSmallestDist(loc, false);
	}
	
	public double getSmallestDist(Location loc, boolean limit)
	{
		Vector v = this.pos2.clone().subtract(this.pos1).toVector();
		Vector w = loc.clone().subtract(this.pos1).toVector();
		double c1 = v.dot(w);
		double c2 = v.dot(v);
		if (c1 <= 0 && limit)
			return loc.distance(this.pos1);
        if (c2 <= c1 && limit)
        	return loc.distance(this.pos2);
        double b = c1 / c2;
        Location Pb = this.pos1.clone().add(v.clone().multiply(b));
        return loc.distance(Pb);	
	}
	
	public Vector getDirVec()
	{
		return this.pos1.clone().subtract(this.pos2).toVector();
	}
	
	public Location getPos1()
	{
		return this.pos1.clone();
	}

	public Location getPos2()
	{
		return this.pos2.clone();
	}
}
