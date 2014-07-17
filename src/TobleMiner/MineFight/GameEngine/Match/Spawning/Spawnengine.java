package TobleMiner.MineFight.GameEngine.Match.Spawning;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Protection.Area3D;

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
					Debugger.writeDebugOut(String.format("Area: (%s) Location: [%d, %d, %d]", dzone.toString(), current.getBlockX(), current.getBlockY(), current.getBlockZ()));
					if(dzone.isCoordInsideRegion(current))
					{
						safe = false;
						Debugger.writeDebugOut(String.format("Spawn not safe for '%s' due to near enemy player '%s' Radius: %.2f", player.thePlayer.getName(), p.thePlayer.getName(), radius));
					}
					if(safe && radius <= maxLOScomputationDistance)
					{
						Vector look = p.thePlayer.getLocation().getDirection();
						Vector lookat = p.thePlayer.getLocation().clone().subtract(current).toVector();
						double lookAngle = look.angle(lookat) * 180d * Math.PI;
						if(lookAngle < smallestLOSangle)
						{
							safe = false;
							Debugger.writeDebugOut(String.format("Spawn not safe for '%s' due to look from enemy player '%s' Radius: %.2f Look-angle: %.2f°", player.thePlayer.getName(), p.thePlayer.getName(), radius, lookAngle));
						}
					}
				}
			}
			radius += 1d;
		}
		return current;
	}
}
