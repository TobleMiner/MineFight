package TobleMiner.MineFight.API;

import java.io.File;
import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFight.Util.Util;
import TobleMiner.MineFight.Util.Protection.ProtectionUtil;
import TobleMiner.MineFight.Weapon.Weapon;

public class MineFightAPI 
{
	public static MineFightAPI instance;
	
	public MineFightAPI()
	{
		MineFightAPI.instance = this;
	}
	
	public Logger getLogger(Plugin p)
	{
		return new Logger(p);
	}
	
	public void addTranslations(File langFile)
	{
		Main.gameEngine.dict.loadLanguageFileExt(langFile);
	}
	
	public boolean registerWeapon(Weapon weapon, World w)
	{
		return Main.gameEngine.weaponRegistry.registerWeapon(weapon, w);
	}
	
	public List<World> getKnownWorlds()
	{
		return Main.gameEngine.configuration.getLoadtimeWorlds();
	}
	
	public ProtectionUtil getProtections()
	{
		return Util.protect;
	}
}
