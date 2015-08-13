package tobleminer.minefight.network;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import tobleminer.minefight.Main;
import tobleminer.minefight.engine.player.PVPPlayer;

public class ProtocolLibSafeLoader
{
	public final ProtocolLibInterface pli;

	public ProtocolLibSafeLoader(Plugin p)
	{
		if (Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") != null)
		{
			pli = new ProtocolLibInterface(p);
			Main.logger.log(Level.INFO, Main.gameEngine.dict.get("protocolLibInstalled"));
		}
		else
		{
			pli = null;
			Main.logger.log(Level.WARNING, Main.gameEngine.dict.get("protocolLibNotInstalled"));
		}
	}

	public void registerPlayer(Player p, PVPPlayer ppvp)
	{
		if (this.pli != null)
		{
			this.pli.registerPlayer(p, ppvp);
		}
	}

	public void unregisterPlayer(Player p)
	{
		if (this.pli != null)
		{
			this.pli.unregisterPlayer(p);
		}
	}

	public void sendNamechange(PVPPlayer player, PVPPlayer receiver, boolean realname)
	{
		if (this.pli != null)
		{
			this.pli.sendNamechange(player, receiver, realname);
		}
	}
}
