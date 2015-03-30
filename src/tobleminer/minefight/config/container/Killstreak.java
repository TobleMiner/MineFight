package tobleminer.minefight.config.container;

public enum Killstreak 
{
	IMS("killstreakims","ims"),
	PLAYERSEEKER("killstreakps","playerseeker","ps"),
	NONE("killstreakdummy","dummy");
	
	private final String[] names;
	public final String transname;
	
	private Killstreak(String transname, String... names)
	{
		this.names = names;
		this.transname = transname;
	}
	
	public static Killstreak getByName(String name)
	{
		name = name.trim();
		for(Killstreak ks : Killstreak.values())
		{
			for(String s : ks.names)
			{
				if(s.equalsIgnoreCase(name))
				{
					return ks;
				}
			}
		}
		return NONE;
	}
}
