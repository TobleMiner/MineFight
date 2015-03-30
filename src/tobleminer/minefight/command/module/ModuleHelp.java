package tobleminer.minefight.command.module;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tobleminer.minefight.Main;
import tobleminer.minefight.cli.CliOutput;
import tobleminer.minefight.command.CommandHelp;
import tobleminer.minefight.permission.Permission;

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
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandModuleHelp.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandModuleHelp.values();
	}
	
	private enum CommandModuleHelp implements CommandHelp
	{
		MPVP_HELP("help","help",0,2,"","/mpvp help [module] [cmd]", Permission.MPVP_HELP.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandModuleHelp(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
		{
			this.module = module;
			this.cmd = cmd;
			this.argnumMin = argnumMin;
			this.argnumMax = argnumMax;
			this.syntax = syntax;
			this.descr = descr;
			this.perm = perm;
		}

		@Override
		public String getCmd()
		{
			return cmd;
		}
		
		@Override
		public String getModule()
		{
			return module;
		}

		@Override
		public int argMin() 
		{
			return argnumMin;
		}

		@Override
		public int argMax()
		{
			return argnumMax;
		}

		@Override
		public String getDescr() 
		{
			return Main.gameEngine.dict.get(descr);
		}

		@Override
		public String getPermission()
		{
			return perm;
		}

		@Override
		public String getSyntax()
		{
			return syntax;
		}
	}	
}
