package TobleMiner.MineFight.GameEngine.Match.Team;

import org.bukkit.ChatColor;

public class Team 
{
	private double points;
	
	public ChatColor color = ChatColor.WHITE;
	protected String name = "NONE";
	
	public void setPoints(double points)
	{
		this.points = points;
	}
	
	public void addPoints(double points)
	{
		this.points += points;
	}
	
	public void subPoints(double points)
	{
		this.points -= points;
	}
	
	public double getPoints()
	{
		return this.points;
	}
	
	public String getName()
	{
		return name;
	}
}
