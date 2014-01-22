package TobleMiner.MineFight.GameEngine.Match.Statistics;

import TobleMiner.MineFight.GameEngine.Match.Statistics.Database.Database;

public class Stats 
{
	private StatConfig sc;
	private final Database db;
	
	public Stats(StatConfig sc)
	{
		this.sc = sc;
		this.db = new Database();
	}
	
	public boolean init()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		}
		catch(Exception ex){}
		return false;
	}
		
	public void updateConfig(StatConfig sc) //TODO
	{
		this.sc = sc;
	}
	
	public boolean isEnabled()
	{
		return this.sc.enabled;
	}
}
