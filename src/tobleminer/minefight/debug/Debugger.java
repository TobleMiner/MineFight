package tobleminer.minefight.debug;

import java.util.logging.Level;

import tobleminer.minefight.Main;

public class Debugger
{
	public static void writeDebugOut(String s)
	{
		if (Main.gameEngine.configuration.isDebuging())
		{
			Main.logger.log(Level.INFO, String.format("[DEBUG] %s", s));
		}
	}
}
