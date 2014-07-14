package TobleMiner.MineFight.Command.Modules;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.CommandHelp;
import TobleMiner.MineFight.Permissions.Permission;

public class ModuleAdmin extends CommandModule
{
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(!Main.cmdhandler.pm.hasPlayerPermission(p, Permission.MPVP_RELOAD))
					{
						p.sendMessage(this.noPermMsg);
						return true;
					}
				}
				Main.gameEngine.reload();
				sender.sendMessage(ChatColor.DARK_GREEN + Main.gameEngine.dict.get("configrl"));				
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName()
	{
		return "admin";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for(CommandHelp help : CommandAdmin.values())
			if(help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}
	
	@Override
	public CommandHelp[] getHelp()
	{
		return CommandAdmin.values();
	}
	
	private enum CommandAdmin implements CommandHelp
	{
		RELOAD("admin", "reload", 0, 0, "cmdDescrAdminReload", "/mpvp admin reload", Permission.MPVP_RELOAD.toString());

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;
		
		CommandAdmin(String module, String cmd, int argnumMin,int argnumMax, String descr, String syntax, String perm)
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