package tobleminer.minefight.api;

import org.bukkit.plugin.Plugin;

import tobleminer.minefight.error.Logger;

public class MineFightAPI
{
	public static MineFightAPI instance;

	public MineFightAPI()
	{
		instance = this;
	}

	public Logger getLogger(Plugin p)
	{
		return new Logger(p);
	}
}
