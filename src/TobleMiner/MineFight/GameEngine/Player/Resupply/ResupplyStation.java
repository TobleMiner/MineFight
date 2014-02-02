package TobleMiner.MineFight.GameEngine.Player.Resupply;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.GameEngine.Player.CombatClass.CombatClass;
import TobleMiner.MineFight.Util.SyncDerp.BlockSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;

public class ResupplyStation 
{
	private final Sign sign;
	private final Match match;
	private final double ammoRefillDist;
	private final double ammoRefillSpeed;
	private int timer = 1;
	private double time = 0.5d;
	private final Block chest;
	private final Block woolRedBack;
	private final Block woolRedLeft;
	private final Block woolRedRight;
	private final Block woolWhiteBackUp;
	private final PVPPlayer owner;
	private int fill;

	public ResupplyStation(Sign sign, Match match, PVPPlayer owner, double ammoRefillDist, double ammoRefillSpeed, int amount)
	{
		this.sign  = sign;
		this.match = match;
		this.owner = owner;
		this.ammoRefillDist = ammoRefillDist;
		this.ammoRefillSpeed = ammoRefillSpeed;
		Location baseLoc = sign.getLocation();
		Location facing = getFacing(sign);
		this.chest = baseLoc.clone().subtract(0d, 1d, 0d).getBlock();
		this.woolRedBack = baseLoc.clone().add(facing).subtract(0d,1d,0d).getBlock();
		this.woolRedLeft = baseLoc.clone().add(facing.getZ(),0d,-facing.getX()).subtract(0d,1d,0d).getBlock();
		this.woolRedRight = baseLoc.clone().add(-facing.getZ(),0d,facing.getX()).subtract(0d,1d,0d).getBlock();
		this.woolWhiteBackUp = baseLoc.clone().add(facing).getBlock();
		this.fill = amount;
	}
	
	public void doUpdate()
	{
		if(timer > GameEngine.tps/2d) //Update each 0.5s
		{
			try
			{
				if(time > ammoRefillSpeed)
				{
					boolean broken = false;
					if(!sign.getLocation().getBlock().getType().equals(Material.WALL_SIGN))
					{
						broken = true;
					}
					else if(!chest.getLocation().getBlock().getType().equals(Material.CHEST))
					{
						broken = true;
					}
					else if(!(woolRedBack.getLocation().getBlock().getType().equals(Material.WOOL) && woolRedBack.getLocation().getBlock().getData() == (byte)14))
					{
						broken = true;						
					}
					else if(!(woolRedLeft.getLocation().getBlock().getType().equals(Material.WOOL) && woolRedLeft.getLocation().getBlock().getData() == (byte)14))
					{
						broken = true;						
					}
					else if(!(woolRedRight.getLocation().getBlock().getType().equals(Material.WOOL) && woolRedRight.getLocation().getBlock().getData() == (byte)14))
					{
						broken = true;						
					}
					else if(!(woolWhiteBackUp.getLocation().getBlock().getType().equals(Material.WOOL) && woolWhiteBackUp.getLocation().getBlock().getData() == (byte)0))
					{
						broken = true;						
					}
					if(broken)
					{
						match.unregisterResupply(this);
						return;
					}
					time = 0d;
					List<PVPPlayer> players = match.getPlayers();
					for(PVPPlayer player : players)
					{
						double dist = player.thePlayer.getLocation().distance(this.sign.getLocation());
						if(dist <= ammoRefillDist)
						{
							if(player.isSpawned())
							{
								CombatClass cc = player.getCombatClass();
								if(cc != null)
								{
									PlayerInventory pi = player.thePlayer.getInventory();
									for(ItemStack is : cc.kit)
									{
										if(!(pi.contains(is.clone()) || (is.getType() == Material.WOOL || is.getType() == Material.SIGN || is.getType() == Material.CHEST)))
										{
											fill--;
											if(player.getTeam() == owner.getTeam() && (!player.equals(owner)))
											{
												owner.resupplyGiven();
											}
											if(pi.contains(is.getType()))
											{
												int size_current = 0;
												Collection<? extends ItemStack> stacks = pi.all(is.getType()).values();
												for(ItemStack istack : stacks)
												{
													size_current += istack.getAmount();
												}
												int size_max = 0;
												for(ItemStack stack : cc.kit)
												{
													if(stack.getType() == is.getType())
													{
														size_max += stack.getAmount();
													}
												}
												is = is.clone();
												int size = Math.max(size_max-size_current, 0);
												size = Math.min(size_max-size_current,64);
												is.setAmount(size);
												if(size > 0)
												{
													pi.addItem(is);
													EffectSyncCalls.playSound(player.thePlayer.getLocation(), Sound.BAT_TAKEOFF, 4f, 0f);
												}
											}
											else
											{
												pi.addItem(is.clone());
												EffectSyncCalls.playSound(player.thePlayer.getLocation(), Sound.BAT_TAKEOFF, 4f, 0f);
											}
											break;
										}
									}
								}
							}
						}
					}
					this.sign.setLine(0,Integer.toString(fill));
					BlockSyncCalls.updateBlockstate(this.sign);
				}
				time += 0.5d;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			timer = 0;
		}
		timer++;
	}
	
	public static Location getFacing(Sign sign)
	{
		int baseOffsetX = 0;
		int baseOffsetZ = 0;
		MaterialData data = sign.getData();
		int dataInt = (int)data.getData();
		if(dataInt == 5)
		{
			baseOffsetX = -1;
		}
		else if(dataInt == 4)
		{
			baseOffsetX = 1;
		}
		else if(dataInt == 2)
		{
			baseOffsetZ = 1;
		}
		else if(dataInt == 3)
		{
			baseOffsetZ = -1;
		}
		return new Location(sign.getWorld(), baseOffsetX, 0, baseOffsetZ);
	}
}
