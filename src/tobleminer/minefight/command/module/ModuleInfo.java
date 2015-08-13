package tobleminer.minefight.command.module;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import tobleminer.minefight.Main;
import tobleminer.minefight.command.CommandHelp;

public class ModuleInfo extends CommandModule
{
	@Override
	public boolean handleCommand(String[] args, CommandSender sender)
	{
		PluginDescriptionFile pdf = Main.main.getDescription();
		sender.sendMessage(String.format("%s v%s Copyright by %s 2014", pdf.getName(), pdf.getVersion(),
				StringUtils.join(pdf.getAuthors(), ",")));
		return true;
	}

	@Override
	public String getName()
	{
		return "info";
	}

	@Override
	public CommandHelp getHelp(String cmd)
	{
		for (CommandHelp help : CommandInfo.values())
			if (help.getCmd().equalsIgnoreCase(cmd))
				return help;
		return null;
	}

	@Override
	public CommandHelp[] getHelp()
	{
		return CommandInfo.values();
	}

	private enum CommandInfo implements CommandHelp
	{
		MPVP_INFO_VERSION("info", "version", 0, 0, "cmdDescrVersion", "/mpvp info version", "");

		public final String module;
		public final String cmd;
		public final int argnumMin;
		public final int argnumMax;
		private final String descr;
		public final String perm;
		public final String syntax;

		CommandInfo(String module, String cmd, int argnumMin, int argnumMax, String descr, String syntax, String perm)
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
