package TobleMiner.MineFight.GameEngine.Match.Team;

import org.bukkit.ChatColor;

import TobleMiner.MineFight.Main;

public class TeamRed extends Team
{
	public TeamRed()
	{
		this.color = Main.gameEngine.configuration.config.getBoolean("20%cooler") ? ChatColor.YELLOW : ChatColor.RED;
		this.name = Main.gameEngine.configuration.config.getBoolean("20%cooler") ? "SEMP" : "RED";
	}
}
