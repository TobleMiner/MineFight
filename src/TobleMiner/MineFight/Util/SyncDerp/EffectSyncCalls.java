package TobleMiner.MineFight.Util.SyncDerp;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.Util.Util;

public class EffectSyncCalls
{
	public static void createExplosion(final Location loc, final float exploStrength)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				loc.getWorld().createExplosion(loc.getX(),loc.getY(),loc.getZ(), exploStrength);
			}
			catch(Exception ex)
			{
				try
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					Error err = new Error("Exception while exiting", sw.toString(), "This problem exists due to a ugly botch and the related code is pending a rewrite.", EffectSyncCalls.class.getName(), ErrorSeverity.INFO);
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
				loc.getWorld().createExplosion(loc.getX(),loc.getY(),loc.getZ(), exploStrength);
			}
		});
	}
	
	
	public static void createExplosionRespectProtection(final Location loc, final float exploStrength)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				if(!Util.protect.isLocProtected(loc))
				{
					loc.getWorld().createExplosion(loc, exploStrength);
				}
			}
			catch(Exception ex)
			{
				try
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					Error err = new Error("Exception while exiting", sw.toString(), "This problem exists due to a ugly botch and the related code is pending a rewrite.", EffectSyncCalls.class.getName(), ErrorSeverity.INFO);
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
				if(!Util.protect.isLocProtected(loc))
				{
					loc.getWorld().createExplosion(loc, exploStrength);
				}
			}
		});
	}
	public static void showEffect(final Location loc, final Effect e, final int arg)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				loc.getWorld().playEffect(loc, e, arg);
			}
			catch(Exception ex)
			{
				try
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					Error err = new Error("Exception while exiting", sw.toString(), "This problem exists due to a ugly botch and the related code is pending a rewrite.", EffectSyncCalls.class.getName(), ErrorSeverity.INFO);
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
				loc.getWorld().playEffect(loc, e, arg);
			}
		});
	}
	
	public static void playSound(final Location loc, final Sound s, final float f1, final float f2)
	{
		if(Main.gameEngine.isExiting)
		{
			try
			{
				loc.getWorld().playSound(loc, s, f1, f2);
			}
			catch(Exception ex)
			{
				try
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					Error err = new Error("Exception while exiting", sw.toString(), "This problem exists due to a ugly botch and the related code is pending a rewrite.", EffectSyncCalls.class.getName(), ErrorSeverity.INFO);
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
				loc.getWorld().playSound(loc, s, f1, f2);
			}
		});
	}
}
