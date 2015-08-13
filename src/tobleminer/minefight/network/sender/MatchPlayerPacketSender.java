package tobleminer.minefight.network.sender;

import tobleminer.minefight.engine.player.PVPPlayer;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

public class MatchPlayerPacketSender
{
	private ProtocolManager pm = ProtocolLibrary.getProtocolManager();

	public void sendNameUpdateTo(PVPPlayer watched, PVPPlayer watcher, boolean realName)
	{
		try
		{
			PacketContainer pc = pm.createPacketConstructor(Packets.Server.NAMED_ENTITY_SPAWN, watched.thePlayer)
					.createPacket(watched.thePlayer);
			pc.getSpecificModifier(String.class).write(0, realName ? watched.thePlayer.getName() : watched.getName());
			pm.sendServerPacket(watcher.thePlayer, pc);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
