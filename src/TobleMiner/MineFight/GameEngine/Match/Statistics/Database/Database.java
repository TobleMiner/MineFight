package TobleMiner.MineFight.GameEngine.Match.Statistics.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import TobleMiner.MineFight.GameEngine.Match.Statistics.StatConfig;

public class Database
{
	public Connection connect(StatConfig sc)
	{
		try 
		{
			return DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s",sc.host,sc.port), sc.user, sc.passwd);
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
	
	public boolean test(StatConfig sc)
	{
		Connection conn = this.connect(sc);
		if(conn != null)
		{
			this.closeConnection(conn);
			return true;
		}
		return false;
	}
}
