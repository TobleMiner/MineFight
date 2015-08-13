package tobleminer.minefight.multiverse;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MultiverseLoader
{
	public MultiverseHandler getHandler()
	{
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		if (p == null)
			return null;
		return new MultiverseHandler(p);
	}
}
