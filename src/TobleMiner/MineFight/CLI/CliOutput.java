package TobleMiner.MineFight.CLI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CliOutput 
{
	public int cnt = 0;
	
	public ChatColor c1 = ChatColor.GREEN;
	public ChatColor c2 = ChatColor.RED;
	
	public CliOutput(){};
	
	public CliOutput(ChatColor c1, ChatColor c2)
	{
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public static void broadcastMsg(String msg)
	{
		Bukkit.broadcastMessage(msg);
	}
	
	public void broadcastMsgHC(String[] msgs)
	{
		int i = 0;
		for(String msg : msgs)
		{
			broadcastMsg((i % 2 == 0 ? c1 : c2)+msg);
			i++;
		}
	}
	
	public void broadcastMsgHC(String msg)
	{
		broadcastMsg((this.cnt % 2 == 0 ? c1 : c2)+msg);
		this.cnt++;
	}

	public static void sendMsg(String msg, CommandSender receiver)
	{
		receiver.sendMessage(msg);
	}
	
	public void sendMsgHC(String[] msgs, CommandSender receiver)
	{
		int i = 0;
		for(String msg : msgs)
		{
			sendMsg((i % 2 == 0 ? c1 : c2)+msg,receiver);
			i++;
		}
	}
	
	public void sendMsgHC(String msg, CommandSender receiver)
	{
		sendMsg((this.cnt % 2 == 0 ? c1 : c2)+msg,receiver);
		this.cnt++;
	}
	
	public void reset()
	{
		this.cnt = 0;
	}
}
