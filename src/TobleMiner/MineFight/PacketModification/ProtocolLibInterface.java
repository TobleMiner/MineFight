package TobleMiner.MineFight.PacketModification;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;

import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.PacketModification.Listener.MatchPlayerPacketlistener;
import TobleMiner.MineFight.PacketModification.Sender.MatchPlayerPacketSender;

public class ProtocolLibInterface 
{
	private final ProtocolManager pm;
	private final MatchPlayerPacketlistener mppl;
	private final MatchPlayerPacketSender mpps;
	
	public ProtocolLibInterface(Plugin p)
	{
		pm = ProtocolLibrary.getProtocolManager();
		this.mppl = this.init(p);
		this.mpps = new MatchPlayerPacketSender();
	}
	
	private MatchPlayerPacketlistener init(Plugin p)
	{
		MatchPlayerPacketlistener mppl = new MatchPlayerPacketlistener(p, ConnectionSide.SERVER_SIDE, Packets.Server.NAMED_ENTITY_SPAWN, Packets.Server.ITEM_DATA);
		pm.addPacketListener(mppl);
		return mppl;
	}
	
	public void registerPlayer(Player p, PVPPlayer ppvp)
	{
		this.mppl.registerPlayer(p, ppvp);
	}
	
	public void unregisterPlayer(Player p)
	{
		this.mppl.unregisterPlayer(p);
	}
	
	public void sendNamechange(PVPPlayer player, PVPPlayer receiver, boolean realname)
	{
		this.mpps.sendNameUpdateTo(player, receiver, realname);
	}
}
