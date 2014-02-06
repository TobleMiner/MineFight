package TobleMiner.MineFight.Configuration.Container;

public enum Killstreak 
{
	IMS("ims"),
	PLAYERSEEKER("playerseeker","ps"),
	NONE("dummy");
	
	private final String[] names;
	
	private Killstreak(String... names)
	{
		this.names = names;
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
