package tobleminer.minefight.api;

import org.bukkit.entity.Player;

import tobleminer.minefight.Main;
import tobleminer.minefight.command.module.CommandModule;

public class MineFightCommandAPI
{
	public static MineFightCommandAPI instance;

	public MineFightCommandAPI()
	{
		instance = this;
	}

	public void registerCommandModule(CommandModule cm)
	{
		Main.cmdhandler.registerModule(cm);
	}

	public boolean hasPlayerPermission(Player p, String perm)
	{
		return Main.cmdhandler.pm.hasPlayerPermission(p, perm);
	}
}
