package TobleMiner.MineFight.API;

import java.io.File;

import org.bukkit.plugin.Plugin;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Logger;
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
	
	public boolean registerWeapon(Weapon weapon)
	{
		return Main.gameEngine.weaponRegistry.registerWeapon(weapon);
	}
}
