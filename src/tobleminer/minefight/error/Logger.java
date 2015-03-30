package tobleminer.minefight.error;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Logger 
{
	private final String prefix;
	
	public Logger(Plugin p)
	{
		prefix = p.getName();
	}
	
	public void log(Level lev, String str)
	{
		Bukkit.getServer().getLogger().log(lev,"["+prefix+"] "+str);
	}
}
