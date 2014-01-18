package TobleMiner.MineFight.PacketModification.Listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class MatchPlayerPacketlistener extends PacketAdapter
{
	private final HashMap<Player,PVPPlayer> playerRegistry = new HashMap<Player,PVPPlayer>();
	
	public MatchPlayerPacketlistener(Plugin plugin, ConnectionSide connectionSide,Integer ... packets) 
	{
		super(plugin, connectionSide, packets);
		//System.out.println("Listener created. Listening for packets...");
	}

	@Override
	public void onPacketSending(PacketEvent pe)
	{
		//System.out.println("Packet received!");
		PacketContainer pc = pe.getPacket();
		//System.out.println("Packet ID:"+pc.getID()+"vs"+Packets.Server.NAMED_ENTITY_SPAWN);
		if(pc.getID() == Packets.Server.NAMED_ENTITY_SPAWN)
		{
			if(playerRegistry.get(pe.getPlayer()) != null)
			{
				Player p = Bukkit.getPlayer(pc.getSpecificModifier(String.class).read(0));
				if(p != null)
				{
					PVPPlayer ppvp = playerRegistry.get(p);
					if(ppvp != null)
					{
						pc.getSpecificModifier(String.class).write(0,ppvp.getName());
					}
				}
			}
		}
		/*else if(pc.getID() == Packets.Server.ITEM_DATA)
		{
			String name = "#YOLO";
			char[] schar = name.toCharArray();
			byte[] data = new byte[schar.length]; 
			for(int i=0;i<schar.length;i++)
			{
				data[i] = (byte)schar[i];
			}
			pc.getSpecificModifier(byte[].class).write(0,data);
		}*/
	}
	
	public void registerPlayer(Player p, PVPPlayer pvpp)
	{
		playerRegistry.put(p, pvpp);
	}
	
	public void unregisterPlayer(Player p)
	{
		playerRegistry.remove(p);
	}
}
