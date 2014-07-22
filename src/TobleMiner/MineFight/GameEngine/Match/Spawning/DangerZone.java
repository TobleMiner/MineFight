package TobleMiner.MineFight.GameEngine.Match.Spawning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.GameEngine.Match.Team.Team;
import TobleMiner.MineFight.Util.Geometry.Area3D;

public class DangerZone extends Area3D
{
	public final List<Team> teams;
	
	public DangerZone(Area3D area, Team ... teams)
	{
		super(area);
		this.teams = new ArrayList<Team>(Arrays.asList(teams));
	}
	
	public DangerZone(Location loc, Location loc1, Team ... teams)
	{
		super(loc, loc1);
		this.teams = new ArrayList<Team>(Arrays.asList(teams));
	}
	
	public DangerZone(Entity ent, Vector vec, Vector vec1, Team ... teams)
	{
		super(ent, vec, vec1);
		this.teams = new ArrayList<Team>(Arrays.asList(teams));
	}
}
