package tobleminer.minefight.engine.player.info;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.map.MinecraftFont;

import tobleminer.minefight.Main;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.match.gamemode.Gamemode;
import tobleminer.minefight.engine.match.gamemode.conquest.Flag;
import tobleminer.minefight.engine.match.gamemode.rush.RadioStation;
import tobleminer.minefight.engine.match.team.Team;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.error.Error;
import tobleminer.minefight.error.ErrorReporter;
import tobleminer.minefight.error.ErrorSeverity;

public class MapInfoRenderer extends MapRenderer
{
	private final Match match;
	private byte identifier = (byte)'A';
	public final boolean _20pcooler;
	private boolean isRendered = false;
	private PVPPlayer player;
	private Team lastTeam;
	
	public MapInfoRenderer(Match m, PVPPlayer player)
	{
		this.match = m;
		this.lastTeam = player.getTeam();
		this._20pcooler = Main.gameEngine.configuration.config.getBoolean("20%cooler", false);
		this.player = player;
	}
	
	@Override
	public void render(MapView mv, MapCanvas mc, Player p)
	{
		if(this.lastTeam != this.player.getTeam())
		{
			this.isRendered = false;
			this.lastTeam = this.player.getTeam();
		}
		if(this._20pcooler)
		{
			if(this.isRendered)
				return;
			for(int i = 0; i < 128; i++)
				for(int j = 0; j < 128; j++)
					mc.setPixel(i, j, (byte)0);
			String resource = "img/nlr.png";
			if(this.match.getTeamRed() == this.player.getTeam())
				resource = "img/solarempire.png";
			try
			{
				InputStream is = this.getClass().getResourceAsStream(resource);
				BufferedImage img = ImageIO.read(is);
				Image img_ = img;
				int max = Math.max(img.getWidth(), img.getHeight());
				if(max > 128)
				{
					int scaledHeight = (int)Math.ceil(128d / ((double)max) * ((double)img.getHeight()));
					int scaledWidth = (int)Math.ceil(128d / ((double)max) * ((double)img.getWidth()));
					img_ = img.getScaledInstance(scaledWidth, scaledHeight, 0);
				}
				mc.drawImage(0, 0, img_);
				img.flush();
				img_.flush();
				is.close();
				this.isRendered = true;
			}
			catch(Exception ex)
			{
				try
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					Error err = new Error("I don't know what went wrong!", sw.toString(), "Failed reading and printing images for easteregg.", this.getClass().getName(), ErrorSeverity.DOUBLERAINBOOM);
					ErrorReporter.reportError(err);
					pw.close();
					sw.close();
				}
				catch(Exception exint)
				{
					exint.printStackTrace();
				}
			}
			return;
		}
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
