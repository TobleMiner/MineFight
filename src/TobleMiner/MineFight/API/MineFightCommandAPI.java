package TobleMiner.MineFight.API;

import org.bukkit.entity.Player;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.Modules.CommandModule;

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
