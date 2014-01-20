package TobleMiner.MineFight.Weapon.TickControlled;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EffectSyncCalls;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;

public class IMS extends TickControlledWeapon
{

	private final Item item;
	private final double triggerDist;
	private int projNum;
	private final PVPPlayer owner;
	private int timer = 1;
	private final double grenadeSpeed;
	private final double grenadeHeight;
	private final float grenadeExploStr;
	private List<PVPPlayer> targeted = new ArrayList<PVPPlayer>();
	private double time = 0d;
	private boolean armed = false;
	
	public IMS(Match match,Item item, double triggerDist, int projNum, PVPPlayer owner)
	{
		super(match);
		this.item = item;
		this.triggerDist = triggerDist;
		this.projNum = projNum;	
		this.owner = owner;
		this.grenadeSpeed = Main.gameEngine.configuration.getIMSGrenadeSpeed();
		this.grenadeHeight = Main.gameEngine.configuration.getIMSGrenadePeekHeight();
		this.grenadeExploStr = Main.gameEngine.configuration.getIMSGrenadeExploStr();
	}

	@Override
	public void doUpdate() 
	{
		if(timer > GameEngine.tps/10d)
		{
			timer = 0;
			time += 0.1d;
			if(time > 10.0d)
			{
				if(!armed)
				{
					this.owner.thePlayer.sendMessage(ChatColor.GOLD+"IMS armed!");
				}
				armed = true;
				List<PVPPlayer> victims = match.getSpawnedPlayersNearLocation(item.getLocation(), triggerDist);
				for(PVPPlayer victim : victims)
				{
					if(owner.getTeam().equals(victim.getTeam()) || this.targeted.contains(victim))
					{
						continue;
					}
					targeted.add(victim);
					Location loc = this.item.getLocation().add(0d,2d,0d);
					Arrow arr = this.item.getWorld().spawnArrow(loc, new Vector(0d,1d,0d),1f,1f);
					new IMSProjectile(match, loc, arr, grenadeSpeed, grenadeHeight, this, victim, grenadeExploStr);
					EffectSyncCalls.showEffect(arr.getLocation(), Effect.BLAZE_SHOOT, 1);
					this.projNum--;
					if(projNum <= 0)
					{
						match.unregisterIMS(item);
						this.unregisterTickControlled();
						EntitySyncCalls.removeEntity(item);
					}
					break;
				}
			}
		}
		timer++;
	}
	
	public PVPPlayer getOwner()
	{
		return this.owner;
	}

	public void release(PVPPlayer target)
	{
		this.targeted.remove(target);
	}
}
