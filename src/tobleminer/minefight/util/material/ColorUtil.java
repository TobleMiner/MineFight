package tobleminer.minefight.util.material;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class ColorUtil
{
	public static DyeColor ChatColorToDyeColor(ChatColor col)
	{
		if (col == ChatColor.AQUA)
			return DyeColor.LIGHT_BLUE;
		if (col == ChatColor.BLUE || col == ChatColor.DARK_BLUE)
			return DyeColor.BLUE;
		if (col == ChatColor.GREEN)
			return DyeColor.LIME;
		if (col == ChatColor.DARK_GREEN)
			return DyeColor.GREEN;
		if (col == ChatColor.RED || col == ChatColor.DARK_RED)
			return DyeColor.RED;
		if (col == ChatColor.BLACK)
			return DyeColor.BLACK;
		if (col == ChatColor.GRAY || col == ChatColor.DARK_GRAY)
			return DyeColor.GRAY;
		if (col == ChatColor.GOLD || col == ChatColor.YELLOW)
			return DyeColor.YELLOW;
		if (col == ChatColor.LIGHT_PURPLE)
			return DyeColor.MAGENTA;
		if (col == ChatColor.DARK_PURPLE)
			return DyeColor.PURPLE;
		return DyeColor.PINK;
	}
}
