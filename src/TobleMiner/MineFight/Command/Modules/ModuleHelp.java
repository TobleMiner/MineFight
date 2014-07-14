package TobleMiner.MineFight.Command.Modules;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.CLI.CliOutput;
import TobleMiner.MineFight.Command.CommandHelp;

public class ModuleHelp extends CommandModule
{
	private CliOutput clio = new CliOutput();
	
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		if(args.length == 0)
		{
			sender.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("helpModulesPre"));
			for(CommandModule cm : Main.cmdhandler.modules)
			{
				this.clio.sendMsgHC("   "+cm.getName(), sender);
			}
			sender.sendMessage(ChatColor.GOLD+Main.gameEngine.dict.get("helpModulesAfter"));
			return true;
		}
		CommandModule cm = null;
		if(args.length >= 1)
		{
			String modname = args[0];
			cm = Main.cmdhandler.getModule(modname);
			if(cm == null)
			{
				sender.sendMessage(ChatColor.DARK_RED+String.format(Main.gameEngine.dict.get("noSuchModule"),modname));
				return true;
			}
		}
		if(args.length == 1)
		{
			sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("helpModulePre"), cm.getName()));
			for(CommandHelp help : cm.getHelp())
			{
				this.clio.sendMsgHC("   "+help.getCmd(), sender);
			}
			sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("helpModuleAfter"),cm.getName()));
			return true;
		}
		if(args.length > 1)
		{
			String cmdname = args[1];
			CommandHelp cmd = cm.getHelp(cmdname);
			if(cmd == null)
			{
				sender.sendMessage(ChatColor.GOLD+String.format(Main.gameEngine.dict.get("moduleNoSuchCmd"),cm.getName(),cmdname));
				return true;
			}
			List<String> descr = new ArrayList<String>();
			descr.add(cmd.getDescr());
			descr.add(cmd.getSyntax());
			this.clio.sendMsgHC(descr.toArray(new String[0]), sender);
			return true;
		}
		return false;
	}

	@Override
	public String getName()
	{
		return "help";
	}

	@Override
	public CommandHelp[] getHelp()
	{
		return null;
	}

	@Override
	public CommandHelp getHelp(String cmd) 
	{
		return null;
	}
}
