package tobleminer.minefight.config.container;

import org.bukkit.block.Sign;

public class RadioStationContainer
{
	public final Sign sign;
	public final String name;
	public final boolean sky;
	
	public RadioStationContainer(Sign sign, String name, boolean sky)
	{
		this.sign = sign;
		this.name = name;
		this.sky = sky;
	}
}
