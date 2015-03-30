package tobleminer.minefight.engine.match.team;

import org.bukkit.ChatColor;

import tobleminer.minefight.Main;

public class TeamBlue extends Team
{
	public TeamBlue()
	{
		this.color = ChatColor.BLUE;
		this.name = Main.gameEngine.configuration.config.getBoolean("20%cooler") ? "NLR" : "BLUE";
	}
}
