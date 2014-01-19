package TobleMiner.MineFight.Command;

import java.util.ArrayList;
import java.util.List;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Permissions.Permission;

public enum Command
{	
	MPVP_ADMIN_RELOAD(CommandModule.ADMIN,"reload",0,0,"cmdDescrAdminReload","/mpvp admin reload",Permission.MPVP_RELOAD),
	
	MPVP_MATCH_CREATE(CommandModule.MATCH,"create",3,4,"cmdDescrMatchCreate","/mpvp match create <world> <gamemode> <name> [hardcore]",Permission.MPVP_MATCH_START),
	MPVP_MATCH_END(CommandModule.MATCH,"end",1,1,"cmdDescrMatchEnd","/mpvp match end <name>",Permission.MPVP_MATCH_END),
	MPVP_MATCH_JOIN(CommandModule.MATCH,"join",1,1,"cmdDescrMatchJoin","/mpvp match join <name>",Permission.MPVP_MATCH_JOIN),
	MPVP_MATCH_LEAVE(CommandModule.MATCH,"leave",0,0,"cmdDescrMatchLeave","/mpvp match leave",Permission.MPVP_MATCH_LEAVE),
	MPVP_MATCH_LIST(CommandModule.MATCH,"list",0,0,"cmdDescrMatchList","/mpvp match list",Permission.MPVP_MATCH_LIST),
	MPVP_MATCH_INFO(CommandModule.MATCH,"info",1,1,"cmdDescrMatchInfo","/mpvp match INFO <name>",Permission.MPVP_MATCH_INFO),
	
	MPVP_FLAG_ADD(CommandModule.FLAG,"add",0,0,"cmdDescrFlagAdd","/mpvp flag add",Permission.MPVP_FLAG_ADD),
	MPVP_FLAG_DEL(CommandModule.FLAG,"remove",0,0,"cmdDescrFlagDel","/mpvp flag remove",Permission.MPVP_FLAG_DEL),
	
	MPVP_INFOSIGN_ADD(CommandModule.INFOSIGN,"add",1,1,"cmdDescrIsAdd","/mpvp is add <gmode>",Permission.MPVP_INFOSIGN_ADD),
	MPVP_INFOSIGN_DEL(CommandModule.INFOSIGN,"remove",1,1,"cmdDescrIsDel","/mpvp is remove <gmode>",Permission.MPVP_INFOSIGN_DEL),
	MPVP_INFOSIGN_LIST(CommandModule.INFOSIGN,"list",2,2,"cmdDescrIsList","/mpvp is list <world> <gmode>",Permission.MPVP_INFOSIGN_DEL),

	MPVP_RADIOSTATION_ADD(CommandModule.RS,"add",0,0,"cmdDescrRsAdd","/mpvp rs add",Permission.MPVP_RS_ADD),
	MPVP_RADIOSTATION_DEL(CommandModule.RS,"remove",0,0,"cmdDescrRsDel","/mpvp rs remove",Permission.MPVP_RS_DEL);
		
	public final CommandModule cm;
	public final String cmd;
	public final int argnumMin;
	public final int argnumMax;
	private final String descr;
	public final Permission perm;
	public final String syntax;
	public String aliases;
	
	Command(CommandModule cm,String cmd, int argnumMin,int argnumMax, String descr, String syntax, Permission perm)
	{
		this.cm = cm;
		this.cmd = cmd;
		this.argnumMin = argnumMin;
		this.argnumMax = argnumMax;
		this.syntax = syntax;
		this.descr = descr;
		this.perm = perm;
	}
	
	public List<String> getInformation()
	{
		List<String> descr = new ArrayList<String>();
		descr.add(Main.gameEngine.dict.get(this.descr));
		descr.add(this.syntax);
		return descr;
	}
	
	public static List<Command> getCommandsByModule(CommandModule cm)
	{
		List<Command> cmds = new ArrayList<Command>();
		for(Command cmd : values())
		{
			if(cmd.cm.equals(cm)) cmds.add(cmd);
		}
		return cmds;
	}
	
	public static Command getCommand(CommandModule cm, String name)
	{
		List<Command> cmds = getCommandsByModule(cm);
		for(Command cmd : cmds)
		{
			if(cmd.cmd.equalsIgnoreCase(name)) return cmd;
		}
		return null;
	}
	
	public enum CommandModule
	{
		ADMIN("admin"),
		FLAG("flag"),
		INFOSIGN("is"),
		MATCH("match"),
		RS("rs");
		
		public final String name;
		
		CommandModule(String name)
		{
			this.name = name;
		}
		
		public static CommandModule getModule(String name)
		{
			for(CommandModule cm : values())
			{
				if(cm.name.equalsIgnoreCase(name)) return cm;
			}
			return null;
		}
	}
}


