package tobleminer.minefight.engine.player.info;

import org.bukkit.block.Sign;

import tobleminer.minefight.engine.match.Match;

public class InformationSign
{
	private final Match	match;
	private final Sign	sign;
	private int			timer	= 0;

	public InformationSign(Match m, Sign s)
	{
		this.match = m;
		this.sign = s;
	}

	public void doUpdate()
	{
		if (timer >= 100)
		{
			timer = 0;
			String[] lines = match.getInformationSignText();
			int i = 0;
			for (String line : lines) // TODO: Multi sign support
			{
				sign.setLine(i, line);
				i++;
			}
			sign.update(true);
		}
		timer++;
	}
}
