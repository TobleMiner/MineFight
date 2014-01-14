package TobleMiner.MineFight.GameEngine.Player.Info;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.map.MinecraftFont;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest.Flag;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush.RadioStation;

public class MapInfoRenderer extends MapRenderer
{
	private final Match match;
	private byte identifier = (byte)'A';
	
	public MapInfoRenderer(Match m)
	{
		this.match = m;
	}
	
	@Override
	public void render(MapView mv, MapCanvas mc, Player p)
	{
		int blocksPerPixel = 1;
		Scale scale = mv.getScale();
		if(scale == Scale.CLOSE)
		{
			blocksPerPixel = 2;
		}
		else if(scale == Scale.NORMAL)
		{
			blocksPerPixel = 4;
		}
		else if(scale == Scale.FAR)
		{
			blocksPerPixel = 8;
		}
		else if(scale == Scale.FARTHEST)
		{
			blocksPerPixel = 16;
		}
		int midX = mv.getCenterX();
		int midZ = mv.getCenterZ();
		if(this.match.gmode == Gamemode.Conquest)
		{
			List<Flag> flags = this.match.getFlags();
			byte i = 0;
			for(Flag f : flags)
			{
				String color = "§58;";
				if(f.getOwner() == match.getTeamRed())
				{
					color = "§54;";
				}
				else if(f.getOwner() == match.getTeamBlue())
				{
					color = "§51;";
				}
				int posX = f.getLocation().getBlockX();
				int posZ = f.getLocation().getBlockZ();
				int dX = posX-midX;
				int dZ = posZ-midZ;
				int mapPosX = (int)Math.round((double)64+(((double)dX)/((double)blocksPerPixel)));
				int mapPosZ = (int)Math.round((double)64+(((double)dZ)/((double)blocksPerPixel)));
				mc.setPixel(mapPosX, mapPosZ, MapPalette.DARK_BROWN);
				mc.drawText(mapPosX+2, mapPosZ+2, MinecraftFont.Font, color+"FLAG "+(char)(identifier+i));
				i++;
			}
		}
		else if(this.match.gmode == Gamemode.Rush)
		{
			List<RadioStation> rss = this.match.getRadioStations();
			byte i = 0;
			for(RadioStation rs : rss)
			{
				String color = "�20;";
				if(rs.isDestroyed())
				{
					color = "�24;";
				}
				int posX = rs.getLocation().getBlockX();
				int posZ = rs.getLocation().getBlockZ();
				int dX = posX-midX;
				int dZ = posZ-midZ;
				int mapPosX = (int)Math.round((double)64+(((double)dX)/((double)blocksPerPixel)));
				int mapPosZ = (int)Math.round((double)64+(((double)dZ)/((double)blocksPerPixel)));
				mc.setPixel(mapPosX, mapPosZ, MapPalette.DARK_BROWN);
				mc.drawText(mapPosX+2, mapPosZ+2, MinecraftFont.Font, color+"POINT "+(char)(identifier+i));
				i++;
			}
		}
	}

}
