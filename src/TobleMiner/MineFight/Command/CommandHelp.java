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
			this.sender.sendMessage(Main.gameEngine.dict.get("helpModulesAfter"));
			return true;
		}
		if(args.length == 1)
		{
			String modname = args[0];
			this.sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("helpModulePre"),modname));
			CommandModule cm = CommandModule.getModule(modname);
			if(cm == null)
			{
				this.sender.sendMessage(ChatColor.DARK_RED+String.format(Main.gameEngine.dict.get("noSuchModule"),modname));
				return true;
			}
			int i = 0;
			for(Command cmd : Command.getCommandsByModule(cm))
			{
				this.sender.sendMessage((i % 2 == 0 ? ChatColor.RED : ChatColor.GREEN)+"   "+cmd.cmd);
				i++;
			}
			this.sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("helpModuleAfter"),modname));
			return true;
		}
		return false;
	}
}
