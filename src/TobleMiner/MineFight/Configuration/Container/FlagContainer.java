package TobleMiner.MineFight.Configuration.Container;

import org.bukkit.block.Sign;

public class FlagContainer
{
	public final String name;
	public final Sign sign;
	public final boolean sky;
	
	public FlagContainer(Sign s, String n, boolean sky)
	{
		this.sign = s;
		this.name = n;
		this.sky = sky;
	}
}
