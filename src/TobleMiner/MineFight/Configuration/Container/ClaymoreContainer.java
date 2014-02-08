package TobleMiner.MineFight.Configuration.Container;

public class ClaymoreContainer
{
	public final int maxClayNum;
	public final boolean canPickup;
	public final boolean canAvoid;
	
	public ClaymoreContainer(int maxClayNum, boolean canPickup, boolean canAvoid)
	{
		this.maxClayNum = maxClayNum;
		this.canPickup = canPickup;
		this.canAvoid = canAvoid;
	}
}
