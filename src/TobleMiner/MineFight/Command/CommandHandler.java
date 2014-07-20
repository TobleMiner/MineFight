package TobleMiner.MineFight.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Command.Modules.CommandModule;
import TobleMiner.MineFight.Command.Modules.ModuleAdmin;
import TobleMiner.MineFight.Command.Modules.ModuleDebug;
import TobleMiner.MineFight.Command.Modules.ModuleFlag;
import TobleMiner.MineFight.Command.Modules.ModuleHelp;
import TobleMiner.MineFight.Command.Modules.ModuleInfo;
import TobleMiner.MineFight.Command.Modules.ModuleInfosign;
import TobleMiner.MineFight.Command.Modules.ModuleMatch;
import TobleMiner.MineFight.Command.Modules.ModulePlayer;
import TobleMiner.MineFight.Command.Modules.ModuleRadioStation;
import TobleMiner.MineFight.Permissions.PermissionManager;

public class CommandHandler
{
	public final PermissionManager pm;
	public final List<CommandModule> modules = new ArrayList<>(); 
	private final HashMap<String, CommandModule> moduleByName;
	
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
		if(args.length >= 1)
		{
			List<String> argsComp = new ArrayList<String>();
			boolean escaped = false;
			String current = "";
			for(String argRaw : args)
			{
				String arg = argRaw.trim();
				if(escaped)
				{
					if(arg.endsWith("\"") && arg.length() > 1)
					{
						escaped = false;
						argsComp.add(current+" "+arg.subSequence(0, arg.length()-1));
					}
					else
					{
						current += " "+arg;
					}
				}
				else
				{
					if(arg.startsWith("\"") && arg.length() > 1)
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
			if(argsComp.size() < 1) return false;
			String[] argsShort = new String[argsComp.size()-1];
			System.arraycopy(argsComp.toArray(new String[0]), 1, argsShort, 0, argsShort.length);
			CommandModule module = this.moduleByName.get(args[0]);
			if(module != null)
			{
				return module.handleCommand(argsShort, sender);
			}
			return false;
		}
		return false;
	}	
}
