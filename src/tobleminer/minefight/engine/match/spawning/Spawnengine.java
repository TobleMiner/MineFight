package tobleminer.minefight.engine.match.spawning;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import tobleminer.minefight.Main;
import tobleminer.minefight.debug.Debugger;
import tobleminer.minefight.engine.match.Match;
import tobleminer.minefight.engine.player.PVPPlayer;
import tobleminer.minefight.util.geometry.Area3D;
import tobleminer.minefight.util.geometry.Line3D;

public class Spawnengine
{
	private Match match;
	private Random rand = new Random();
	
	public Spawnengine(Match m)
	{
		this.match = m;
	}
	
	public Location findSafeSpawn(Location base, PVPPlayer player)
	{
		if(!Main.gameEngine.configuration.isSpawnengineEnabled(this.match.getWorld()))
			return base;
		Debugger.writeDebugOut("Spawnengine active");
		double minDist = Main.gameEngine.configuration.minEnemySpawnDistance(this.match.getWorld());
		Debugger.writeDebugOut(String.format("Min spawn dist: %.2f", minDist));
		double smallestLOSangle = Main.gameEngine.configuration.smallestLineOfSightAngle(this.match.getWorld());
		double maxLOScomputationDistance = Main.gameEngine.configuration.maxLOScomputationDistance(this.match.getWorld());
		Debugger.writeDebugOut(String.format("Max los compdist: %.2f", maxLOScomputationDistance));
		double minProjDist = Main.gameEngine.configuration.minProjectileDist(this.match.getWorld());
		boolean safe = false;
		double radius = 0d;
		Location current = base.clone();
		while(!safe)
		{
			safe = true;
			for(PVPPlayer p : this.match.getPlayers())
			{
				current = base.clone();
				double rndAngle = rand.nextDouble() * 2 * Math.PI;
				current.add(new Vector(radius * Math.sin(rndAngle), 0d, radius * Math.cos(rndAngle)));
				if(p.getTeam() != player.getTeam() && p.isSpawned())
				{
					Vector vect = new Vector(minDist, minDist, minDist);
					if(Main.gameEngine.configuration.isMinEnemySpawnDistance2D(this.match.getWorld()))
						vect.setY(0d);
					Area3D dzone = new Area3D(p.thePlayer.getLocation().clone().add(vect), p.thePlayer.getLocation().clone().add(vect.clone().multiply(-1d)));
					if(dzone.isCoordInsideRegion(current))
					{
						safe = false;
						Debugger.writeDebugOut(String.format("Spawn not safe for '%s' due to near enemy player '%s' Radius: %.2f", player.thePlayer.getName(), p.thePlayer.getName(), radius));
						break;
					}
					if(safe && radius <= maxLOScomputationDistance)
					{
						Vector look = p.thePlayer.getLocation().getDirection();
						Vector lookat = current.clone().subtract(p.thePlayer.getLocation()).toVector();
						double lookAngle = look.angle(lookat) / Math.PI * 180d;
						if(lookAngle < smallestLOSangle)
						{
							safe = false;
							Debugger.writeDebugOut(String.format("Spawn not safe for '%s' due to look from enemy player '%s' Radius: %.2f Look-angle: %.2f°", player.thePlayer.getName(), p.thePlayer.getName(), radius, lookAngle));
							break;
						}
					}
					if(safe)
					{
						for(Area3D dangerZone : match.dangerZones)
						{
							if(dangerZone instanceof DangerZone)
							{
								if(((DangerZone)dangerZone).teams.contains(player.getTeam()))
									continue;
							}
							if(dangerZone.isCoordInsideRegion(current))
							{
								safe = false;
								break;
							}
						}
					}
					if(safe)
					{
						for(Projectile proj : match.allProjectiles)
						{
							Location projLoc = proj.getLocation();
							Vector projDir = proj.getVelocity();
							Line3D path = new Line3D(projLoc, projDir);
							if(path.getSmallestDist(current) < minProjDist)
							{
								safe = false;
								break;
							}
						}
					}
				}
			}
			radius += 1d;
		}
		return current;
	}
}
