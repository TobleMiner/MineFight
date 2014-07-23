package TobleMiner.MineFight.Util.SyncDerp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.bukkit.block.BlockState;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.ErrorHandling.Error;

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
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintWriter pw = new PrintWriter(baos);
					ex.printStackTrace(pw);
					Error err = new Error("Exception while exiting", baos.toString("UTF-8"), "This problem exists due to a ugly botch and the related code is pending a rewrite.", BlockSyncCalls.class.getName(), ErrorSeverity.INFO);
					ErrorReporter.reportError(err);
					pw.close();
					baos.close();
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
