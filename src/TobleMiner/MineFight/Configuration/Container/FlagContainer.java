package TobleMiner.MineFight.Configuration.Container;

import org.bukkit.block.Sign;

public class FlagContainer
{
	public final String name;
	public final Sign sign;
	
	public FlagContainer(Sign s, String n)
	{
		this.sign = s;
		this.name = n;
	}
}
