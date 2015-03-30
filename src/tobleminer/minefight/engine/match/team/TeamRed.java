package tobleminer.minefight.engine.match.team;

import org.bukkit.ChatColor;

import tobleminer.minefight.Main;

public class TeamRed extends Team
{
	public TeamRed()
	{
		this.color = Main.gameEngine.configuration.config.getBoolean("20%cooler") ? ChatColor.YELLOW : ChatColor.RED;
		this.name = Main.gameEngine.configuration.config.getBoolean("20%cooler") ? "SEMP" : "RED";
	}
}
