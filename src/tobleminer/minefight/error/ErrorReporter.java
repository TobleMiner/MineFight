package tobleminer.minefight.error;

import java.io.PrintStream;

public class ErrorReporter
{
	public static void reportError(Error error)
	{
		reportErrorInternally(error);
	}
	
	private static void reportErrorInternally(Error error)
	{
		PrintStream stdout = System.out;
		stdout.println(" ");
		stdout.println("MineFight encountered an error!");
		stdout.println("Errorlevel: "+error.severity.toString());
		stdout.println(error.header);
		stdout.println("-------------------------------------");
		stdout.println(error.body);
		stdout.println(error.footer);
		stdout.println(" ");
	}

	private static boolean reportErrorExternally()
	{
		return true; //TODO
	}
}
