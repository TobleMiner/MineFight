package tobleminer.minefight.multiverse;

import org.bukkit.plugin.Plugin;

import tobleminer.minefight.error.Error;
import tobleminer.minefight.error.ErrorReporter;
import tobleminer.minefight.error.ErrorSeverity;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiversePlugin;

public class MultiverseHandler 
{
	private final MultiverseCore mc;
	
	public MultiverseHandler(Plugin p) 
	{
		if(!(p instanceof MultiversePlugin))
		{
			Error err = new Error("Type of Multiverse isn't MultiversePlugin", "Something about your Multiverse seems to be wrong.", "Make sure you have the latest version of Multiverse installed!", this.getClass().getName(), ErrorSeverity.WARNING);
			ErrorReporter.reportError(err);
		}
		this.mc = ((MultiversePlugin)p).getCore();
	}
}
