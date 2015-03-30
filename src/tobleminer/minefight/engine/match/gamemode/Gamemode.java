package tobleminer.minefight.engine.match.gamemode;

public enum Gamemode
{
	Conquest("conquest"),
	Teamdeathmatch("tdm"),
	Rush("rush"),
	FreeForAll("ffa");
	
	public final String transname;
	
	private Gamemode(String transname)
	{
		this.transname = transname;
	}
}
