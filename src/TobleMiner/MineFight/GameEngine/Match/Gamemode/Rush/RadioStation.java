package TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.GameEngine.Score;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.Location.FacingUtil;

public class RadioStation 
{
	private final Sign sign;
	private boolean destroyed = false;
	private final List<Block> torchBlocks;
	private int timer = 1;
	private double destructTimer = 0d;
	private final double destructTime;
	private boolean armed = false;
	private final Match match;
	public PVPPlayer armer;
	private PVPPlayer attacker;
	public PVPPlayer defender;
		
	public RadioStation(Sign sign, double destructTime, Match match)
	{
		this.sign = sign;
		RadioStation.buildRadioStation(sign,RadioStation.getFacing(sign));
		this.torchBlocks = RadioStation.getTorchBlocks(sign, RadioStation.getFacing(sign));
		for(Block b : torchBlocks)
		{
			if(!b.getType().equals(Material.WALL_SIGN))
			{
				b.setType(Material.AIR);
			}
		}
		this.destructTime = destructTime;
		this.match = match;
	}
	
	public void doUpdate()
	{
		if(timer > 10)
		{
			timer = 0;
			if(!destroyed)
			{
				int torches = 0;
				for(Block b : torchBlocks)
				{
					if(b.getType().equals(Material.REDSTONE_TORCH_ON) || b.getType().equals(Material.REDSTONE_TORCH_OFF))
					{
						torches++;
					}
				}
				if(torches > 6)
				{
					armed = true;
					if(this.armer != null)
					{
						this.armer.points += Main.gameEngine.configuration.getScore(this.armer.thePlayer.getWorld(),Score.RSARM);
						this.attacker = this.armer;
						this.armer = null;
					}
				}
				else if(torches == 0)
				{
					armed = false;
					if(this.defender != null)
					{
						this.defender.points += Main.gameEngine.configuration.getScore(this.defender.thePlayer.getWorld(),Score.RSDISARM);
						this.defender = null;
						this.attacker = null;
						this.armer = null;
					}
				}
				if(armed)
				{
					destructTimer += 0.1d;
					if(destructTimer >= destructTime)
					{
						this.destroy();
						if(this.attacker != null)
						{
							this.attacker.points += Main.gameEngine.configuration.getScore(this.attacker.thePlayer.getWorld(),Score.RSDEST);
							this.attacker = null;
						}
						match.radioStationDestroyed(this);
					}
				}
				else
				{
					destructTimer = 0d;
				}
			}
			sign.setLine(0,"Radio station");
			sign.setLine(1,(destroyed ? ChatColor.DARK_RED+"DESTROYED" : (armed ? ChatColor.RED+"ARMED" : ChatColor.DARK_GREEN+"RUNNING")));
			sign.setLine(2, ( destructTime == 0d ? "INSTANT": Integer.toString((int)Math.round(destructTimer/destructTime*100d))));
			sign.setLine(3, Integer.toString(match.getRemainingStations())+" | "+Integer.toString((int)Math.round(match.getTeamRed().getPoints())));
			sign.update(true);
		}
		timer++;
	}
	
	public static List<Block> getTorchBlocks(Sign sign,Location facing)
	{
		List<Block> blocks = new ArrayList<Block>();
		Location baseLoc = sign.getLocation().clone().add(facing).add(0d, -1d, 0d);
		for(int i=0;i<4;i++)
		{
			Vector vec = FacingUtil.getOffsetByFacing(i);
			Location loc = baseLoc.clone().add(vec);
			blocks.add(loc.getBlock());
			blocks.add(loc.clone().add(0d,1d,0d).getBlock());
		}
		return blocks;
	}
	
	public static void buildRadioStation(Sign sign,Location facing)
	{
		Location baseLoc = sign.getLocation().clone().add(facing);
		for(int i=-1;i<=1;i++)
		{
			Location loc = baseLoc.clone().add(0d, (double)i, 0d);
			Material mat = Material.AIR;
			switch(i)
			{
				case -1: mat = Material.IRON_BLOCK; break;
				case  0: mat = Material.LAPIS_BLOCK; break;
				case  1: mat = Material.GLOWSTONE; break;				
			}
			loc.getBlock().setType(mat);
		}
	}
	
	public void destroy()
	{
		this.destroyed = true;
		Location baseLoc = sign.getLocation().clone().add(getFacing(sign));
		baseLoc.getWorld().playSound(baseLoc, Sound.EXPLODE,50.0f,50.0f);
		for(int i=-1;i<=1;i++)
		{
			Location loc = baseLoc.clone().add(0d, (double)i, 0d);
			Material mat = Material.AIR;
			switch(i)
			{
				case -1: mat = Material.IRON_BLOCK; break;
				case  0: mat = Material.NETHERRACK; break;
				case  1: mat = Material.FIRE; break;				
			}
			loc.getBlock().setType(mat);
		}		
	}
	
	public static Location getFacing(Sign sign)
	{
		int blockOffsetX = 0;
		int blockOffsetZ = 0;
		MaterialData data = sign.getData();
		int dataInt = (int)data.getData();
		if(dataInt == 5)
		{
			blockOffsetX = -1;
		}
		else if(dataInt == 4)
		{
			blockOffsetX = 1;
		}
		else if(dataInt == 2)
		{
			blockOffsetZ = 1;
		}
		else if(dataInt == 3)
		{
			blockOffsetZ = -1;
		}
		return new Location(sign.getWorld(), blockOffsetX, 0, blockOffsetZ);
	}

	public Location getLocation()
	{
		return this.sign.getLocation();
	}
	
	public boolean isDestroyed()
	{
		return this.destroyed;
	}
}
