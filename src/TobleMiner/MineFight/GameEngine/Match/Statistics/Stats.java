package TobleMiner.MineFight.GameEngine.Match.Statistics;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;

public class Stats 
{
	private StatConfig sc;
	
	public Stats(StatConfig sc)
	{
		this.sc = sc;
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
	
	public Connection openConnection()
	{
		try 
		{
			return DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s",this.sc.host,this.sc.port), this.sc.user, this.sc.passwd);
		} 
		catch (SQLException ex){}
		return null;
	}
	
	public void closeConnection(Connection conn)
	{
		try
		{
			conn.close();
		}
		catch(SQLException ex){}
	}
	
	public boolean test()
	{
		Connection conn = this.openConnection();
		if(conn != null)
		{
			this.closeConnection(conn);
			return true;
		}
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
