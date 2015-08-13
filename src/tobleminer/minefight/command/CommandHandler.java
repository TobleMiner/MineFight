package tobleminer.minefight.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

import tobleminer.minefight.Main;
import tobleminer.minefight.command.module.CommandModule;
import tobleminer.minefight.command.module.ModuleAdmin;
import tobleminer.minefight.command.module.ModuleDebug;
import tobleminer.minefight.command.module.ModuleFlag;
import tobleminer.minefight.command.module.ModuleHelp;
import tobleminer.minefight.command.module.ModuleInfo;
import tobleminer.minefight.command.module.ModuleInfosign;
import tobleminer.minefight.command.module.ModuleMatch;
import tobleminer.minefight.command.module.ModulePlayer;
import tobleminer.minefight.command.module.ModuleRadioStation;
import tobleminer.minefight.permission.PermissionManager;

public class CommandHandler
{
	public final PermissionManager					pm;
	public final List<CommandModule>				modules	= new ArrayList<>();
	private final HashMap<String, CommandModule>	moduleByName;

	public CommandHandler(Main mane)
	{
		this.pm = Main.pm;
		this.moduleByName = new HashMap<>();
		this.registerModule(new ModuleAdmin(mane));
		this.registerModule(new ModuleDebug());
		this.registerModule(new ModuleFlag());
		this.registerModule(new ModuleHelp());
		this.registerModule(new ModuleInfo());
		this.registerModule(new ModuleInfosign());
		this.registerModule(new ModuleMatch());
		this.registerModule(new ModulePlayer());
		this.registerModule(new ModuleRadioStation());
	}

	public void registerModule(CommandModule module)
	{
		this.modules.add(module);
		this.moduleByName.put(module.getName(), module);
	}

	public CommandModule getModule(String name)
	{
		return this.moduleByName.get(name);
	}

	public boolean handleCommand(String[] args, CommandSender sender)
	{
		if (args.length >= 1)
		{
			List<String> argsComp = new ArrayList<String>();
			boolean escaped = false;
			String current = "";
			for (String argRaw : args)
			{
				String arg = argRaw.trim();
				if (escaped)
				{
					if (arg.endsWith("\"") && arg.length() > 1)
					{
						escaped = false;
						argsComp.add(current + " " + arg.subSequence(0, arg.length() - 1));
					}
					else
					{
						current += " " + arg;
					}
				}
				else
				{
					if (arg.startsWith("\"") && arg.length() > 1)
					{
						escaped = true;
						current = arg.substring(1);
					}
					else
					{
						argsComp.add(arg);
					}
				}
			}
			if (argsComp.size() < 1)
				return false;
			String[] argsShort = new String[argsComp.size() - 1];
			System.arraycopy(argsComp.toArray(new String[0]), 1, argsShort, 0, argsShort.length);
			CommandModule module = this.moduleByName.get(args[0]);
			if (module != null)
			{
				return module.handleCommand(argsShort, sender);
			}
			return false;
		}
		return false;
	}
}
