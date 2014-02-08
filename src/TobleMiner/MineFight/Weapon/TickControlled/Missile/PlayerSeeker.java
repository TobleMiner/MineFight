package TobleMiner.MineFight.Weapon.TickControlled.Missile;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Configuration.Config;
import TobleMiner.MineFight.Configuration.Container.PlayerSeekerContainer;
import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

public class PlayerSeeker extends Missile
{
	private PVPPlayer target;
	private Config cfg;
	private Location base;
	private int state = 0;
	private double speed = 0;
	
	public PlayerSeeker(Match m, Arrow arr, PVPPlayer shooter, PVPPlayer target, Config cfg)
	{
		super(m, shooter, arr);
		this.target = target;
		this.cfg = cfg;
	}

	@Override
	public void doUpdate() 
	{
		if(this.arr == null || this.arr.isDead() || !this.arr.isValid()) this.explode();
		PlayerSeekerContainer psc = this.cfg.getPlayerSeekerConf(this.match.getWorld(), this.match.gmode);
		if(this.target == null)
		{
			List<PVPPlayer> players = match.getSpawnedPlayersNearLocation(this.arr.getLocation().clone(), psc.detectionDist);
			for(PVPPlayer p : players)
			{
				if(this.canTarget(p))
				{
					this.target = p;
					this.base = this.arr.getLocation().clone();
					break;
				}
			}
		}
		if(this.target != null)
		{
			if(!this.canTarget(this.target)) this.explode();
			this.speed += psc.maxAccel/GameEngine.tps;
			if(speed > psc.maxSpeed) speed = psc.maxSpeed;
			Vector dirTarget = null;
			if(state == 0)
			{
				dirTarget = new Vector(0d, 1d, 0d);
				if(this.arr.getLocation().getY() - this.base.getY() >= psc.peakHeight)
				{
					state = 1;
				}
			}
			if(state == 1)
			{
				dirTarget = this.target.thePlayer.getLocation().clone().subtract(this.arr.getLocation()).toVector();
				if(dirTarget.length() < psc.threshold)
				{
					state = 2;
					this.explode();
				}
			}
			Vector dirCurrent = this.arr.getVelocity().clone();
			dirTarget = dirTarget.clone().multiply(this.speed/dirTarget.length());
			Vector delta = dirTarget.clone().subtract(dirCurrent.clone());
			Vector dirEff = dirCurrent.clone().add(delta.clone().multiply(Math.min(1d, delta.length()/(psc.maxAccel/GameEngine.tps))));
			Vector velocity = dirEff.clone();
			this.arr.setVelocity(velocity);
		}
	}
	
	@Override
	public void explode()
	{
		PlayerSeekerContainer psc = this.cfg.getPlayerSeekerConf(this.match.getWorld(), this.match.gmode);
		this.match.createExplosion(this.shooter, this.arr.getLocation().clone(), psc.exploStr, psc.exploStr, "PLAYER SEEKER");
		super.explode();
	}
}
