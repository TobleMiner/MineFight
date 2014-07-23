package TobleMiner.MineFight.Util.SyncDerp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;

public class InventorySyncCalls 
{
	public static void removeItemStack(final Inventory i, final ItemStack is)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				i.removeItem(is);
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
				i.removeItem(is);
			}
		});
	}

	public static void addItemStack(final Inventory i, final ItemStack is)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				i.addItem(is);
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
				i.addItem(is);
			}
		});
	}

	public static void clear(final Inventory i)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				i.clear();
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
				i.clear();
			}
		});
	}
}
