package tobleminer.minefight.api;

import java.io.File;

import tobleminer.minefight.Main;

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

	public String localize(String key)
	{
		return Main.gameEngine.dict.get(key);
	}
}
