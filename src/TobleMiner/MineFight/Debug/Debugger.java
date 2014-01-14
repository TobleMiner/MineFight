package TobleMiner.MineFight.Debug;

import java.io.PrintStream;

import TobleMiner.MineFight.Main;

public class Debugger 
{
	public static void writeDebugOut(String s)
	{
		if(Main.gameEngine.configuration.isDebuging())
		{
			PrintStream stdout = System.out;
			stdout.println(" ");
			stdout.println("----------------DEBUG----------------");
			stdout.println(s);
			stdout.println("----------------DEBUG----------------");
			stdout.println(" ");
		}
	}
}
