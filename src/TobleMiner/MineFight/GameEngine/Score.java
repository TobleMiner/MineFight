package TobleMiner.MineFight.GameEngine;

public enum Score
{
	FLAGCAP("flagCapture"),
	KILL("kill"),
	RSARM("radioArm"),
	RSDEST("radioDest"),
	RSDISARM("radioDisarm");
	
	public final String name;
	
	Score(String s)
	{
		this.name = s;
	}
}
