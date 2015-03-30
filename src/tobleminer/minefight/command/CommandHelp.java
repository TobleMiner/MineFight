package tobleminer.minefight.command;

public interface CommandHelp
{		
	public abstract String getCmd();
	public abstract String getModule();
	public abstract int argMin();
	public abstract int argMax();
	public abstract String getDescr();
	public abstract String getPermission();
	public abstract String getSyntax(); 		
}