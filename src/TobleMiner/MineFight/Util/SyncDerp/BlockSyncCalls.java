package TobleMiner.MineFight.Util.SyncDerp;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bukkit.block.BlockState;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;

public class BlockSyncCalls 
{
	public static void updateBlockstate(final BlockState bs)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				bs.update();
			}
			catch(Exception ex)
			{
				try
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					Error err = new Error("Exception while exiting", sw.toString(), "This problem exists due to a ugly botch and the related code is pending a rewrite.", BlockSyncCalls.class.getName(), ErrorSeverity.INFO);
					ErrorReporter.reportError(err);
					pw.close();
					sw.close();
				}
				catch(Exception exint)
				{
					exint.printStackTrace();
				}
			}
			return;
		}
		Main.main.getServer().getScheduler().runTask(Main.main, new Runnable()
		{		
			public void run()
			{
				bs.update();
			}
		});
	}
}
