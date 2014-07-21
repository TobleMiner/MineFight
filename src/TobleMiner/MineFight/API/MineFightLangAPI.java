package TobleMiner.MineFight.API;

import java.io.File;

import TobleMiner.MineFight.Main;

public class MineFightLangAPI 
{
	public static MineFightLangAPI instance;

	public MineFightLangAPI()
	{
		instance = this;
	}
		
	public void addTranslations(File langFile)
	{
		Main.gameEngine.dict.loadLanguageFileExt(langFile);
	}

	public String getTranslation(String key)
	{
		return Main.gameEngine.dict.get(key);
	}
}
