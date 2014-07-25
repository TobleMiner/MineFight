package TobleMiner.MineFight.GameEngine.Match.Team;

import org.bukkit.ChatColor;

import TobleMiner.MineFight.Main;

public class TeamBlue extends Team
{
	public TeamBlue()
	{
		this.color = ChatColor.BLUE;
		this.name = Main.gameEngine.configuration.config.getBoolean("20%cooler") ? "NLR" : "BLUE";
	}
}
