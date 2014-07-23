package TobleMiner.MineFight.Util.SyncDerp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.bukkit.entity.Entity;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;

public class EntitySyncCalls 
{
	public static void removeEntity(final Entity ent)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				ent.remove();
			}
			catch(Exception ex)
			{
				try
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintWriter pw = new PrintWriter(baos);
					ex.printStackTrace(pw);
					Error err = new Error("Exception while exiting", baos.toString("UTF-8"), "This problem exists due to a ugly botch and the related code is pending a rewrite.", EntitySyncCalls.class.getName(), ErrorSeverity.INFO);
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
				ent.remove();
			}
		});
	}
}
