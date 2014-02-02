package TobleMiner.MineFight.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.CLI.CliOutput;
import TobleMiner.MineFight.Command.Command.CommandModule;

public class CommandHelp extends CommandHandler
{
	private CliOutput clio = new CliOutput();
	
	public CommandHelp(CommandSender sender) 
	{
		super(sender);
	}

	public boolean handle(String[] args)
	{
		if(args.length == 0)
		{
			this.sender.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("helpModulesPre"));
			for(CommandModule cm : CommandModule.values())
			{
				this.clio.sendMsgHC("   "+cm.name, this.sender);
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
			for(Command cmd : Command.getCommandsByModule(cm))
			{
				this.clio.sendMsgHC("   "+cmd.cmd, this.sender);
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
				this.sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("moduleNoSuchCmd"),cm.name,cmdname));
				return true;
			}
			this.clio.sendMsgHC(cmd.getInformation().toArray(new String[0]), this.sender);
			return true;
		}
		return false;
	}
}
