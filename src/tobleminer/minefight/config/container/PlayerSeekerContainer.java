package tobleminer.minefight.config.container;

public class PlayerSeekerContainer
{
	public final double	detectionDist;
	public final float	exploStr;
	public final double	maxSpeed;
	public final double	peakHeight;
	public final double	maxAccel;
	public final double	threshold;

	public PlayerSeekerContainer(double detDist, float exploStr, double maxSpeed, double peakHeight, double maxAccel,
			double threshold)
	{
		this.detectionDist = detDist;
		this.exploStr = exploStr;
		this.maxSpeed = maxSpeed;
		this.peakHeight = peakHeight;
		this.maxAccel = maxAccel;
		this.threshold = threshold;
	}
}
