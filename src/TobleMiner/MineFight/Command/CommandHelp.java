package TobleMiner.MineFight.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.Command.CommandModule;

public class CommandHelp extends CommandHandler
{
	public CommandHelp(CommandSender sender) 
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(args.length == 0)
		{
			this.sender.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("helpModulesPre"));
			int i = 0;
			for(CommandModule cm : CommandModule.values())
			{
				this.sender.sendMessage((i % 2 == 0 ? ChatColor.RED : ChatColor.GREEN)+"   "+cm.name);
				i++;
			}
			this.sender.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("helpModulesAfter"));
			return true;
		}
		CommandModule cm = null;
		if(args.length >= 1)
		{
			String modname = args[0];
			cm = CommandModule.getModule(modname);
			if(cm == null)
			{
				this.sender.sendMessage(ChatColor.DARK_RED+String.format(Main.gameEngine.dict.get("noSuchModule"),modname));
				return true;
			}
		}
		if(args.length == 1)
		{
			this.sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("helpModulePre"),cm.name));
			int i = 0;
			for(Command cmd : Command.getCommandsByModule(cm))
			{
				this.sender.sendMessage((i % 2 == 0 ? ChatColor.RED : ChatColor.GREEN)+"   "+cmd.cmd);
				i++;
			}
			this.sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("helpModuleAfter"),cm.name));
			return true;
		}
		if(args.length > 1)
		{
			String cmdname = args[1];
			Command cmd = Command.getCommand(cm, cmdname);
			if(cmd == null)
			{
				this.sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("moduleNoSuchCmd"),cmdname,cm.name));
				return true;
			}
			int i = 0;
			for(String s : cmd.getInformation())
			{
				this.sender.sendMessage((i % 2 == 0 ? ChatColor.RED : ChatColor.GREEN)+s);
				i++;
			}
			return true;
		}
		return false;
	}
}
